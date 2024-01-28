package tidinari.mbpunish.screens

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.abstraction.NickInfo
import tidinari.mbpunish.recognize.information.abstraction.PossibleNickInfo
import tidinari.mbpunish.recognize.menu.MenuChooser
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource


class NearMenu(private val players: List<Pair<String, Boolean>>, private val rulesSource: RulesSource, private val settingsSource: SettingsSource) : BaseOwoScreen<FlowLayout>() {
    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    override fun build(rootComponent: FlowLayout?) {
        rootComponent?.apply {
            surface(Surface.VANILLA_TRANSLUCENT)
            padding(Insets.of(3))
            val mainContainer = Containers.horizontalFlow(Sizing.content(), Sizing.content())
            val scrollableContainer = Containers.verticalScroll(Sizing.fixed(width), Sizing.fill(100), mainContainer)
            child(scrollableContainer)

            var additionContainer = Containers.verticalFlow(Sizing.content(), Sizing.content())
            for ((index, player) in players.withIndex()) {
                if (index % 7 == 0 || index == players.lastIndex) {
                    mainContainer.child(additionContainer)
                    additionContainer = Containers.verticalFlow(Sizing.content(), Sizing.content())
                }
                additionContainer.child(
                        Components.button(Text.literal(player.first)) { _ ->
                            val menuChooser = MenuChooser(rulesSource, settingsSource)
                            if (player.second) {
                                val menu = menuChooser.chooseMenu(object : NickInfo(player.first) {})
                                menuChooser.openMenu(menu!!)
                            } else {
                                menuChooser.executeCommand(player.first)
                            }
                        }
                )
            }
        }
    }
}