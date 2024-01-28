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
import tidinari.mbpunish.commands.RealNameAliasCommand
import tidinari.mbpunish.commands.SendChatCommand
import tidinari.mbpunish.recognize.menu.MenuChooser
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
        RealNameAliasCommand().registerCommand()
        SendChatCommand().registerCommand()
        PreventColorcodeAbuse().registerListener()
        ClientReceiveMessageEvents.MODIFY_GAME.register(
            ClientReceiveMessageEvents.ModifyGame { fullMessage, _ ->
                if (!settingsFileSource.read().chatsAction) return@ModifyGame fullMessage

                val siblings = fullMessage.siblings
                if (isAModerCommand(siblings)) {
                    val violator = siblings[4].string.trim() // Ник наказавшего
                    val nick = siblings[6].string.trim() // Ник потерпевшего
                    val punishment = siblings[5].string.trim() // Наказание
                    if (punishment.startsWith("кикнул")) {
                        addPunishToMessage(siblings, nick)
                    } else {
                        addPunishToMessage(siblings, nick, "§e[П]§r")
                        if (punishment.startsWith("забанил")) {
                            addUnToMessage(siblings, "/unban $nick", "Разбанить §c$nick")
                        } else if (punishment.startsWith("замутил")) {
                            addUnToMessage(siblings, "/unmute $nick", "Размутить §c$nick")
                        } else if (punishment.startsWith("выдал предупреждение")) {
                            addUnToMessage(siblings, "/unwarn $nick", "Разварнить §c$nick")
                        }
                        addPunishToMessage(siblings, violator)
                    }
                } else if (isANearMessage(siblings)) {
                    val nicks = siblings.subList(3, siblings.size).map { it.string }
                    var nick = ""
                    var dropMeters = false
                    var nextDrop = false
                    var offside = 3
                    for ((index, str) in nicks.withIndex()) {
                        if (str == "(") {
                            if (nick.startsWith("~~")) {
                                addRealNameMessage(siblings, "/realname $nick", "Реальный ник $nick", index = index + offside)
                                offside++
                            } else {
                                addPunishToMessage(siblings, nick, index = index + offside)
                                offside++
                            }
                            nick = ""
                            dropMeters = true
                        } else if (dropMeters) {
                            nextDrop = true
                            dropMeters = false
                            continue
                        } else if (nextDrop) {
                            nick = str.drop(3)
                            nextDrop = false
                        } else {
                            nick += if (index == 0) str.trim() else str
                        }
                    }
                } else if (isAJailCommand(siblings)) {
                    val nick = siblings[4].string.trim() // Пострадавший
                    addPunishToMessage(siblings, siblings[2].string.trim())
                    addUnToMessage(siblings, "/jail free $nick", "Разджеилить $nick")
                    addPunishToMessage(siblings, nick, "§e[П]§r")
                } else if (isAnAFKMessage(siblings)) {
                    if (siblings[2].string.startsWith("~~")) {
                        val notRealName = siblings.subList(2, siblings.size - 1)
                            .joinToString("") { it.string }.removePrefix("~~")
                        addRealNameMessage(siblings, "/realname $notRealName", "Реальный ник $notRealName")
                    } else {
                        val realName = siblings[2].string.trim()
                        addPunishToMessage(siblings, realName)
                    }
                } else if (isASocialSpy(siblings)) {
                    if (siblings[8].string.startsWith("~~")) {
                        val notRealName = siblings.subList(8, siblings.map { it.string }.indexOf(" -> "))
                            .joinToString("") { it.string }.removePrefix("~~")
                        addRealNameMessage(siblings, "/realname $notRealName", "Реальный ник $notRealName")
                    } else {
                        val name = siblings[8].string
                        addPunishToMessage(siblings, name)
                    }
                } else if (isAPrivateMessage(siblings)) {
                    if (siblings[3].string.startsWith("~~")) {
                        val notRealName = siblings.subList(3, siblings.map { it.string }.indexOf(" -> "))
                            .joinToString("") { it.string }.removePrefix("~~")
                        addRealNameMessage(siblings, "/realname $notRealName", "Реальный ник $notRealName")
                    } else {
                        val name = siblings[3].string
                        addPunishToMessage(siblings, name)
                    }
                } else if (isABannedOrMutedMessage(siblings)) {
                    val nick = siblings[3].string.trim()
                    addPunishToMessage(siblings, nick, "§e[П]§r")
                } else if (isARealName(siblings)) {
                    val nick = siblings.last().string.trim()
                    addPunishToMessage(siblings, nick)
                } else if (isAChatGame(siblings)) {
                    val exercise = siblings[3].string
                    addAnswerToMessage(siblings, exercise)
                } else {
                    for (text in siblings) {
                        if (text.style.clickEvent?.action == ClickEvent.Action.SUGGEST_COMMAND &&
                            text.style.clickEvent?.value?.startsWith("/msg ") == true
                        ) {
                            val playerName = text.style.clickEvent!!.value.drop(5).dropLast(1)
                            addPunishToMessage(siblings, playerName)
                            break
                        }
                    }
                }
                return@ModifyGame fullMessage
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

    private fun isAPrivateMessage(siblings: List<Text>): Boolean {
        return siblings.size >= 6
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[2].string.equals("[")
    }

    private fun isASocialSpy(siblings: List<Text>): Boolean {
        return siblings.size >= 12
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[2].string.equals("[")
                && siblings[3].string.equals("SS")
                && siblings[4].string.equals("] ")
                && siblings[5].string.equals("|")
                && siblings[6].string.equals(" ")
                && siblings[7].string.equals("[")
    }

    private fun isANearMessage(siblings: List<Text>): Boolean {
        return siblings.size >= 12
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[2].string.equals("Окружающие игроки:")
    }

    private fun isARealName(siblings: List<Text>): Boolean {
        return siblings.size >= 5
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[siblings.size - 2].string.equals(" является ")
    }

    private fun isAModerCommand(extra: List<Text>): Boolean {
        return extra.size > 6 && extra[0].string.startsWith("[") && extra[1].string.startsWith("*") && extra[2].string.startsWith(
            "]"
        )
    }

    private fun isAJailCommand(extra: List<Text>): Boolean {
        return extra.size > 8 && extra[0].string.startsWith("| ") && extra[3].string.startsWith("посадил")
    }

    private fun isAChatGame(siblings: List<Text>): Boolean {
        return siblings.size == 5
                && siblings[0].string.equals("Chat Game ")
                && siblings[1].string.equals("» ")
                && siblings[2].string.equals("Решите пример: ")
                && siblings[4].string.equals("Ответ напишите в чат. Награда: 5000\$")
    }

    private fun isAnAFKMessage(siblings: List<Text>): Boolean {
        return siblings.size >= 5
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && (siblings.last().string.equals("вернулся.") || siblings.last().string.equals("отошел."))
    }

    private fun isABannedOrMutedMessage(siblings: List<Text>): Boolean {
        return siblings.size == 5
                && siblings[0].string.equals("[")
                && siblings[1].string.equals("*")
                && siblings[2].string.equals("] ")
                && siblings.last().string.startsWith("пытался")
    }

    private fun addUnToMessage(siblings: MutableList<Text>, command: String, desc: String) {
        siblings.add(
            0, Text.literal("§a[Р]§r").setStyle(
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

    private fun addRealNameMessage(siblings: MutableList<Text>, command: String, desc: String, index: Int = 0) {
        siblings.add(
            index, Text.literal("§3[R]§r").setStyle(
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

    private fun addAnswerToMessage(siblings: MutableList<Text>, expression: String) {
        val expressionsParts = expression.split(" ")
        val answer = if (expressionsParts[1] == "-") {
            expressionsParts[0].toInt() - expressionsParts[2].replace(".", "").toInt()
        } else if (expressionsParts[1] == "+") {
            expressionsParts[0].toInt() + expressionsParts[2].replace(".", "").toInt()
        } else {
            expressionsParts[0].toInt() * expressionsParts[2].replace(".", "").toInt()
        }
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