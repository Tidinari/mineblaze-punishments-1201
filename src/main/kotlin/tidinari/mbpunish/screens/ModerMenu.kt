package tidinari.mbpunish.screens

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import tidinari.mbpunish.data.UnsafeCommandExecutor
import tidinari.mbpunish.data.Violator
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource


class ModerMenu(
    punisher: String,
    victim: String,
    private val rulesSource: RulesSource,
    private val settingsSource: SettingsSource,
    private var selectedPlayer: Int = 0
) : BaseOwoScreen<FlowLayout>() {

    private val players = listOf(Violator(punisher), Violator(victim))
    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    override fun build(rootComponent: FlowLayout?) {
        rootComponent?.apply {
            surface(Surface.VANILLA_TRANSLUCENT)
            padding(Insets.of(3))
            val settings = settingsSource.read()

            // Violator chooser
            child(Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(45))
                .apply { horizontalAlignment(HorizontalAlignment.CENTER) }
                .children(listOf(
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .apply { verticalAlignment(VerticalAlignment.CENTER) }
                        .child(Components.checkbox(Text.literal(""))
                            .apply {
                                checked(selectedPlayer == 0)
                            }.onChanged {
                                rebuildWith(0, settings.punishmentsInRow, settings.rulesInColumn)
                            }
                        )
                        .child(Components.button(Text.literal(players[0].name())) {
                            rebuildWith(
                                0,
                                settings.punishmentsInRow,
                                settings.rulesInColumn
                            )
                        }.margins(Insets.right(10)))
                        .child(Components.button(Text.literal(players[1].name())) {
                            rebuildWith(
                                1,
                                settings.punishmentsInRow,
                                settings.rulesInColumn
                            )
                        }.margins(Insets.left(10)))
                        .child(Components.checkbox(Text.literal(""))
                            .apply {
                                checked(selectedPlayer == 1)
                            }.onChanged {
                                rebuildWith(1, settings.punishmentsInRow, settings.rulesInColumn)
                            }.margins(Insets.left(5))
                        ),
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .apply { verticalAlignment(VerticalAlignment.CENTER) }
                        .children(
                            listOf(
                                UnsafeCommandExecutor("homes", "home %name%:").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("rg list", "rg list -p %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("shist", "shist %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("hist", "hist %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("alts", "alts %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("unmute", "unmute %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("unwarn", "unwarn %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("unban", "unban %name%").component(players[selectedPlayer]),
                                UnsafeCommandExecutor("doninfo", "doninfo %name%").component(players[selectedPlayer]),
                            )
                        )
                )))

            // Main container
            val mainContainer = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(height - 45 - 3 - 50)).apply {
                alignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP)
            }
            child(mainContainer)

            // Bottom container
            val bottomContainer = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(50)).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP)
            }
            child(bottomContainer)
            // Rules container
            val rulesContainer = Containers.horizontalFlow(Sizing.content(), Sizing.content())
            val rulesScrollableContainer =
                Containers.verticalScroll(Sizing.fixed(width - 150 - 9), Sizing.fill(100), rulesContainer)
            mainContainer.child(rulesScrollableContainer)
            // Additional container
            val additionalContainer = Containers.verticalFlow(Sizing.fixed(150), Sizing.fill(100))
            mainContainer.child(additionalContainer)

            // Add bottom information
            val descComponent = Components.label(Text.literal("Описание правила: *Нажмите на название правила*"))
            bottomContainer.child(descComponent)
            // Add rules into rules container
            var rowContainer = constructRowRulesContainer()
            rulesContainer.child(rowContainer)
            for ((index, rule) in rulesSource.read().withIndex()) {
                if (index != 0 && index % settings.rulesInColumn == 0) {
                    rowContainer = constructRowRulesContainer()
                    rulesContainer.child(rowContainer)
                }
                rule.display(rowContainer, players[selectedPlayer], settings.punishmentsInRow) { name, desc ->
                    descComponent.text(Text.literal("Описание $name: $desc")).apply {
                        maxWidth(width - 20)
                        allowOverflow(false)
                    }
                }
            }
            // Add additional information
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                children(
                    listOf(
                        Components.label(Text.literal("Наказаний в строке: ${settings.punishmentsInRow}")),
                        Components.button(Text.literal("+")) {
                            rebuildWith(
                                settings.punishmentsInRow + 1,
                                settings.rulesInColumn
                            )
                        },
                    )
                )
                val minusPunishment = Components.button(Text.literal("-")) {
                    rebuildWith(
                        settings.punishmentsInRow - 1,
                        settings.rulesInColumn
                    )
                }
                if (settings.punishmentsInRow == 1) {
                    minusPunishment.active(false)
                }
                child(minusPunishment)
            })
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                children(listOf(
                    Components.label(Text.literal("Правил в столбце: ${settings.rulesInColumn}")),
                    Components.button(Text.literal("+")) {
                        rebuildWith(
                            settings.punishmentsInRow,
                            settings.rulesInColumn + 1
                        )
                    }
                ))
                val minusRules = Components.button(Text.literal("-")) {
                    rebuildWith(
                        settings.punishmentsInRow,
                        settings.rulesInColumn - 1
                    )
                }
                if (settings.rulesInColumn == 1) {
                    minusRules.active(false)
                }
                child(minusRules)
            })
            additionalContainer.child(
                Components.checkbox(Text.literal("ПКМ по игроку")).apply { checked(settings.useEntityAction) }
                    .onChanged {
                        settingsSource.save(settings.rebuildWithParameters(changedUseEntityAction = it))
                    })
            additionalContainer.child(
                Components.checkbox(Text.literal("Через чат")).apply { checked(settings.chatsAction) }.onChanged {
                    settingsSource.save(settings.rebuildWithParameters(changedChatsAction = it))
                })
            additionalContainer.child(
                Components.checkbox(Text.literal("Фиксация чата при фокусе"))
                    .apply { checked(settings.stopChatOnFocus) }.onChanged {
                    settingsSource.save(settings.rebuildWithParameters(changedStopChatOnFocus = it))
                })
            additionalContainer.child(
                Components.checkbox(Text.literal("Старое меню для банов/варнов/..")).apply { checked(settings.oldMenu) }
                    .onChanged {
                        settingsSource.save(settings.rebuildWithParameters(changedOldMenu = it))
                    })
            additionalContainer.child(
                Components.checkbox(Text.literal("Спам фильтр")).apply { checked(settings.spamFilter) }.onChanged {
                    settingsSource.save(settings.rebuildWithParameters(changedSpamFilter = it))
                })
            additionalContainer.child(
                Components.checkbox(Text.literal("КолорАбьюз")).apply { checked(settings.disableColorAbuse) }
                    .onChanged {
                        settingsSource.save(settings.rebuildWithParameters(changedDisableColorAbuse = it))
                    })

            additionalContainer.apply {
                children(listOf(
                    Components.label(Text.literal("Наказать:")),
                    Components.textBox(Sizing.fixed(75), settings.punishment).apply {
                        onChanged().subscribe {
                            settingsSource.save(settings.rebuildWithParameters(changedPunishment = it))
                        }
                    }
                ))

                children(listOf(
                    Components.label(Text.literal("Снять:")),
                    Components.textBox(Sizing.fixed(75), settings.unpunish).apply {
                        onChanged().subscribe {
                            settingsSource.save(settings.rebuildWithParameters(changedUnpunish = it))
                        }
                    }
                ))

                children(listOf(
                    Components.label(Text.literal("Пострадавший:")),
                    Components.textBox(Sizing.fixed(75), settings.victim).apply {
                        onChanged().subscribe {
                            settingsSource.save(settings.rebuildWithParameters(changedVictim = it))
                        }
                    }
                ))
                children(listOf(
                    Components.label(Text.literal("Realname:")),
                    Components.textBox(Sizing.fixed(75), settings.realname).apply {
                        onChanged().subscribe {
                            settingsSource.save(settings.rebuildWithParameters(changedRealname = it))
                        }
                    }
                ))
            }
        }
    }

    private fun rebuildWith(selectedPlayer: Int = 0, punishmentInRow: Int = 2, rulesInRow: Int = 3) {
        settingsSource.save(
            settingsSource.read().run {
                rebuildWithParameters(
                    changedPunishmentsInRow = punishmentInRow,
                    changedRulesInColumn = rulesInRow
                )
            })
        MinecraftClient.getInstance()
            .setScreen(ModerMenu(players[0].name(), players[1].name(), rulesSource, settingsSource, selectedPlayer))
    }

    private fun constructRowRulesContainer(): FlowLayout {
        return Containers.verticalFlow(Sizing.content(), Sizing.content())
            .apply {
                alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                margins(Insets.both(0, 3))
            }
    }
}