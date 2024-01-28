package tidinari.mbpunish.screens

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import tidinari.mbpunish.data.Violator
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource


class OneNickMenu(player: String, private val rulesSource: RulesSource, private val settingsSource: SettingsSource) : BaseOwoScreen<FlowLayout>() {
    private val violator = Violator(player)

    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    override fun build(rootComponent: FlowLayout?) {
        rootComponent?.apply {
            surface(Surface.VANILLA_TRANSLUCENT)
            padding(Insets.of(3))
            val settings = settingsSource.read()

            violator.display(rootComponent)

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
            val rulesScrollableContainer = Containers.verticalScroll(Sizing.fixed(width - 150 - 9), Sizing.fill(100), rulesContainer)
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
                rule.display(rowContainer, violator, settings.punishmentsInRow) { name, desc ->
                    descComponent.text(Text.literal("Описание $name: $desc")).apply {
                        maxWidth(width - 20)
                        allowOverflow(false)
                    }
                }
            }
            // Add additional information
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                children(listOf(
                    Components.label(Text.literal("Наказаний в строке: ${settings.punishmentsInRow}")),
                    Components.button(Text.literal("+")) { rebuildWith(settings.punishmentsInRow + 1, settings.rulesInColumn) },
                ))
                val minusPunishment = Components.button(Text.literal("-")) { rebuildWith(settings.punishmentsInRow - 1, settings.rulesInColumn) }
                if (settings.punishmentsInRow == 1) {
                    minusPunishment.active(false)
                }
                child(minusPunishment)
            })
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                children(listOf(
                    Components.label(Text.literal("Правил в столбце: ${settings.rulesInColumn}")),
                    Components.button(Text.literal("+")) { rebuildWith(settings.punishmentsInRow, settings.rulesInColumn + 1) }
                ))
                val minusRules = Components.button(Text.literal("-")) { rebuildWith(settings.punishmentsInRow, settings.rulesInColumn - 1) }
                if (settings.rulesInColumn == 1) {
                    minusRules.active(false)
                }
                child(minusRules)
            })
            additionalContainer.child(Components.checkbox(Text.literal("ПКМ по игроку")).apply { checked(settings.useEntityAction) }.onChanged {
                settingsSource.save(settings.rebuildWithParameters(changedUseEntityAction = it))
            })
            additionalContainer.child(Components.checkbox(Text.literal("Через чат")).apply { checked(settings.chatsAction) }.onChanged {
                settingsSource.save(settings.rebuildWithParameters(changedChatsAction = it))
            })
            additionalContainer.child(Components.checkbox(Text.literal("Фиксация чата при фокусе")).apply { checked(settings.stopChatOnFocus) }.onChanged {
                settingsSource.save(settings.rebuildWithParameters(changedStopChatOnFocus = it))
            })
        }
    }

    private fun rebuildWith(punishmentInRow: Int = 2, rulesInRow: Int = 3) {
        settingsSource.save(settingsSource.read().run { rebuildWithParameters(changedPunishmentsInRow = punishmentInRow, changedRulesInColumn = rulesInRow) })
        MinecraftClient.getInstance().setScreen(OneNickMenu(violator.name(), rulesSource, settingsSource))
    }

    private fun constructRowRulesContainer(): FlowLayout {
        return Containers.verticalFlow(Sizing.content(), Sizing.content())
            .apply {
                alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                margins(Insets.both(0, 3))
            }
    }
}