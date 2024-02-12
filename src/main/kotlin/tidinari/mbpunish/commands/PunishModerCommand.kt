package tidinari.mbpunish.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import tidinari.mbpunish.screens.ModerMenu
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource


class PunishModerCommand(private val rulesSource: RulesSource, private val settingsSource: SettingsSource) {
    init {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (isExecuted) {
                open(punisher, victim)
                isExecuted = false
            }
        })
    }

    private val suggestPlayerNames: SuggestionProvider<FabricClientCommandSource> = SuggestionProvider { context, builder ->
        val players = context.source.playerNames
        try {
            val playerName = StringArgumentType.getString(context, "playerName")
            players.filter { it.startsWith(playerName, ignoreCase = true) }.forEach { builder.suggest(it) }
        } catch (emptyName: IllegalArgumentException) {
            players.forEach { builder.suggest(it) }
        }
        builder.buildFuture()
    }

    fun registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register(
                ClientCommandRegistrationCallback { dispatcher, _ ->
                    dispatcher.register(
                            LiteralArgumentBuilder
                                    .literal<FabricClientCommandSource?>("pnm")
                                    .then(ClientCommandManager.argument("punisher", StringArgumentType.string())
                                            .suggests(suggestPlayerNames)
                                            .then(ClientCommandManager.argument("victim", StringArgumentType.string())
                                                    .suggests(suggestPlayerNames)
                                                    .executes(::executes))
                                    )
                    )
                }
        )
    }

    private var isExecuted = false
    private var punisher = ""
    private var victim = ""

    private fun executes(context: CommandContext<FabricClientCommandSource>?): Int {
        punisher = try {
            StringArgumentType.getString(context, "punisher")
        } catch (emptyName: IllegalArgumentException) {
            ""
        }
        victim = try {
            StringArgumentType.getString(context, "victim")
        } catch (emptyName: IllegalArgumentException) {
            ""
        }
        isExecuted = true
        return 0
    }

    private fun open(punisher: String, victim: String) {
        MinecraftClient.getInstance().setScreenAndRender(ModerMenu(punisher, victim, rulesSource, settingsSource))
    }
}