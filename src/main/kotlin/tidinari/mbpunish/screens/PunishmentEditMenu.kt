package tidinari.mbpunish.screens

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import tidinari.mbpunish.data.EditableRule
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.rules.data.EditableRules
import tidinari.mbpunish.sources.settings.SettingsSource
import java.util.*

class PunishmentEditMenu(
    private val rulesSource: RulesSource,
    private val settingsSource: SettingsSource,
    private val editableRules: MutableList<EditableRule> = rulesSource.read().map { it.asEditable() }.toMutableList(),
    private val page: Int = 0
) : BaseOwoScreen<FlowLayout>() {
    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::horizontalFlow)
    }

    override fun build(rootComponent: FlowLayout?) {
        rootComponent?.apply {
            surface(Surface.VANILLA_TRANSLUCENT)
            padding(Insets.of(3))
            val settings = settingsSource.read()
            // Rules container
            val rulesContainer = Containers.horizontalFlow(Sizing.content(), Sizing.content())
            val rulesScrollableContainer = Containers.verticalScroll(Sizing.fixed(width - 150 - 9), Sizing.fill(100), rulesContainer)
            child(rulesScrollableContainer)
            // Additional container
            val additionalContainer = Containers.verticalFlow(Sizing.fixed(150), Sizing.fill(100))
            child(additionalContainer)

            // Add rules into rules container
            val rowContainer = constructRowRulesContainer()
            rulesContainer.child(rowContainer)
            // Included
            // 0 * 5 = 0
            // 1 * 5 = 5
            val rulesFromIndex = page * settings.rulesInSettingPerPage
            // Excluded
            // 1 * 5 = 5
            // 2 * 5 = 10
            var rulesToIndex = (page + 1) * settings.rulesInSettingPerPage
            if (rulesToIndex > editableRules.size)
                rulesToIndex = editableRules.size

            for (index in rulesFromIndex until rulesToIndex) {
                if (editableRules.isEmpty()) break
                editableRules[index].display(rowContainer, index,
                    {
                        if (index > 0) {
                            Collections.swap(editableRules, index, index - 1)
                            rebuildWith(editableRules, page)
                        }
                    }, {
                        if (editableRules.size - 1 != index) {
                            Collections.swap(editableRules, index, index + 1)
                            rebuildWith(editableRules, page)
                        }
                    },
                    { rebuildWith(editableRules, page) })
            }
            // Add additional information
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                child(Components.button(Text.literal("Сохранить")) {
                    val changedRules = editableRules.filter { it.isNotBlank() }
                    rulesSource.save(EditableRules(changedRules))
                    rebuildWith(changedRules.toMutableList(), page)
                })
                child(Components.button(Text.literal("Добавить")) {
                    editableRules.add(page * settings.rulesInSettingPerPage, EditableRule("", "", listOf()))
                    rebuildWith(editableRules, page)
                })
            })
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                child(Components.label(Text.literal("Правил в столбце: ${settings.rulesInSettingPerPage}")))
                val addPage = Components.button(Text.literal("+")) {
                    settingsSource.save(
                        settings.rebuildWithParameters(changedRulesInSettingPerPage = settings.rulesInSettingPerPage + 1)
                    )
                    rebuildWith(editableRules, page)
                }
                // (rulesInSettingPerPage + 1) * page - индекс начала страницы.
                // Если индекс начала страницы строго меньше количество всего правил в списке,
                // то деактивировать
                // 3 * 7 = 21 > 20
                // 3 * 0 > 20
                if ((settings.rulesInSettingPerPage + 1) * page > editableRules.size - 1) {
                    addPage.active(false)
                }
                child(addPage)
                val minusPage = Components.button(Text.literal("-")) {
                    settingsSource.save(
                        settings.rebuildWithParameters(changedRulesInSettingPerPage = settings.rulesInSettingPerPage - 1)
                    )
                    rebuildWith(editableRules, page)
                }
                if (settings.rulesInSettingPerPage == 1) {
                    minusPage.active(false)
                }
                child(minusPage)
            })
            additionalContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply {
                alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                child(Components.label(Text.literal("Страница: $page")))
                val addPage = Components.button(Text.literal("+")) { rebuildWith(editableRules, page + 1) }
                if (rulesToIndex == editableRules.size) {
                    addPage.active(false)
                }
                child(addPage)
                val minusPage = Components.button(Text.literal("-")) { rebuildWith(editableRules, page - 1) }
                if (page == 0) {
                    minusPage.active(false)
                }
                child(minusPage)
            })
        }
    }

    private fun rebuildWith(editableRules: MutableList<EditableRule>, page: Int) {
        MinecraftClient.getInstance().setScreen(PunishmentEditMenu(rulesSource, settingsSource, editableRules, page))
    }

    private fun constructRowRulesContainer(): FlowLayout {
        return Containers.verticalFlow(Sizing.content(), Sizing.content())
            .apply {
                alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                margins(Insets.both(0, 3))
            }
    }
}