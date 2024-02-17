package tidinari.mbpunish

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import tidinari.mbpunish.chat.ChatReplacer
import tidinari.mbpunish.chat.ReplacerMenu
import tidinari.mbpunish.chat.ScrollDisableOnFocus
import tidinari.mbpunish.chatpatch.PreventColorcodeAbuse
import tidinari.mbpunish.commands.PunishCommand
import tidinari.mbpunish.commands.PunishModerCommand
import tidinari.mbpunish.commands.RealNameAliasCommand
import tidinari.mbpunish.commands.SendChatCommand
import tidinari.mbpunish.recognize.MessageRecognizer
import tidinari.mbpunish.recognize.information.*
import tidinari.mbpunish.recognize.information.abstraction.ModerInfo
import tidinari.mbpunish.recognize.information.abstraction.NickInfo
import tidinari.mbpunish.recognize.information.abstraction.PossibleNickInfo
import tidinari.mbpunish.recognize.menu.MenuChooser
import tidinari.mbpunish.recognize.patterns.SpamPattern
import tidinari.mbpunish.reminder.ModerCommandReminder
import tidinari.mbpunish.screens.OneNickMenu
import tidinari.mbpunish.screens.PunishmentEditMenu
import tidinari.mbpunish.sources.replace.ReplaceFileSource
import tidinari.mbpunish.sources.rules.RulesFileSource
import tidinari.mbpunish.sources.settings.SettingsFileSource


object MineBlazePunishments : ModInitializer {
    private val logger = LoggerFactory.getLogger("mineblaze-punishments")
    val menuChooser: MenuChooser
        get() = _menuChooser!!
    private var _menuChooser: MenuChooser? = null

    override fun onInitialize() {
        val replacerFileSource = ReplaceFileSource()
        replacerFileSource.init()
        ChatReplacer(replacerFileSource).registerListeners()

        val rulesFileSource = RulesFileSource()
        rulesFileSource.init()
        val settingsFileSource = SettingsFileSource()
        settingsFileSource.init()

        _menuChooser = MenuChooser(rulesFileSource, settingsFileSource)

        ScrollDisableOnFocus(settingsFileSource).handleChat()
        UseEntityCallback.EVENT.register(UseEntityHandler(rulesFileSource, settingsFileSource))

        PunishCommand(rulesFileSource, settingsFileSource).registerCommand()
        PunishModerCommand(rulesFileSource, settingsFileSource).registerCommand()
        RealNameAliasCommand().registerCommand()
        SendChatCommand().registerCommand()
        PreventColorcodeAbuse(settingsFileSource).registerListener()
        ModerCommandReminder().registerListener()
        ClientReceiveMessageEvents.ALLOW_GAME.register(ClientReceiveMessageEvents.AllowGame { message, _ ->
            if (!settingsFileSource.read().spamFilter) return@AllowGame true
            if (message.string.trim().isEmpty()) {
                return@AllowGame false
            }
            !SpamPattern().isMatches(message.siblings)
        })

        val messageRecognizer = MessageRecognizer()

        ClientReceiveMessageEvents.MODIFY_GAME.register(
            ClientReceiveMessageEvents.ModifyGame { fullMessage, _ ->
                val siblings = fullMessage.siblings
                val textToAdd = Text.empty()
                textToAdd.siblings.add(Text.empty())
                textToAdd.append(fullMessage)
                val settings = settingsFileSource.read()
                if (!settings.chatsAction) return@ModifyGame textToAdd
                val siblingsToAdd = textToAdd.siblings[0].siblings
                try {
                    when (val info = messageRecognizer.recognize(siblings).parseMessage(siblings)) {
                        is ModerInfo -> {
                            if (settings.oldMenu) {
                                addPunishToMessage(siblingsToAdd, info.victim, text = settings.victim)
                                addUnpunishVictimToMessage(siblingsToAdd, info.victim, info, text = settings.unpunish)
                                addPunishToMessage(siblingsToAdd, info.punisher, text = settings.punishment)
                            } else {
                                addUnpunishVictimToMessage(siblingsToAdd, info.victim, info, text = settings.unpunish)
                                addPunishModerToMessage(siblingsToAdd, info.punisher, info.victim, text = settings.punishment)
                            }
                        }

                        is NickInfo ->
                            addPunishToMessage(siblingsToAdd, info.nick, text = settings.punishment)

                        is PossibleNickInfo ->
                            if (info.isReal) addPunishToMessage(siblingsToAdd, info.nick, text = settings.punishment)
                            else addRealNameMessage(siblingsToAdd, "/realname ${info.nick}", "Настоящее имя ${info.nick}", text = settings.realname)

                        is ChatGameInfo ->
                            addAnswerToMessage(siblingsToAdd, info.answer)
                    }
                } catch (_: Exception) {
                }
                return@ModifyGame textToAdd
            }
        )

        val punishmenu = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.mbpunishment.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.mbpunishment"
            )
        )
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (punishmenu.wasPressed()) {
                MinecraftClient.getInstance().setScreen(OneNickMenu("", rulesFileSource, settingsFileSource))
            }
        })

        val punishmenuedit = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.mbpunishment.settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.mbpunishment"
            )
        )
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (punishmenuedit.wasPressed()) {
                MinecraftClient.getInstance().setScreen(PunishmentEditMenu(rulesFileSource, settingsFileSource))
            }
        })

        val settingsmenu = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.mbpunishment.replacer",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.mbpunishment"
            )
        )
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (settingsmenu.wasPressed()) {
                MinecraftClient.getInstance().setScreen(ReplacerMenu(replacerFileSource))
            }
        })
    }

    private fun addRealNameMessage(siblings: MutableList<Text>, command: String, desc: String, index: Int = 0, text: String = "§3[R]") {
        siblings.add(
            index, Text.literal(text).setStyle(
                Style.EMPTY.withClickEvent(
                    ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        command
                    )
                ).withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal(desc)
                    )
                )
            )
        )
    }

    private fun addPunishToMessage(siblings: MutableList<Text>, violator: String, text: String = "§c[Н]§r", index: Int = 0) {
        siblings.add(
            index, Text.literal(text).setStyle(
                Style.EMPTY.withClickEvent(
                    ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/pn $violator"
                    )
                ).withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Наказать §c$violator")
                    )
                )
            )
        )
    }

    private fun addPunishModerToMessage(siblings: MutableList<Text>, punisher: String, victim: String, text: String = "§c[Н]§r", index: Int = 0) {
        siblings.add(
            index, Text.literal(text).setStyle(
                Style.EMPTY.withClickEvent(
                    ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/pnm $punisher $victim"
                    )
                ).withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Наказать §c$punisher -> $victim")
                    )
                )
            )
        )
    }

    private fun addUnpunishVictimToMessage(siblings: MutableList<Text>, victim: String, info: ModerInfo, text: String = "§c[Н]§r") {
        val command = when (info) {
            is BanInfo -> "/unban $victim"
            is WarnInfo -> "/unwarn $victim"
            is MuteInfo -> "/unmute $victim"
            is JailInfo -> "/jail free $victim"
            else -> ""
        }
        if (command == "") return
        siblings.add(
            0, Text.literal(text).setStyle(
                Style.EMPTY.withClickEvent(
                    ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        command
                    )
                ).withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Исполнить $command")
                    )
                )
            )
        )
    }

    private fun addAnswerToMessage(siblings: MutableList<Text>, answer: Int) {
        siblings.add(
            0, Text.literal("§a§l[A]§r").setStyle(
                Style.EMPTY.withClickEvent(
                    ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "$answer"
                    )
                ).withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Написать ответ: $answer")
                    )
                )
            )
        )
    }
}