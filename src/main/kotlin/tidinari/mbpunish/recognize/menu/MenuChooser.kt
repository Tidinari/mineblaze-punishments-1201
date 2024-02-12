package tidinari.mbpunish.recognize.menu

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.container.FlowLayout
import net.minecraft.client.MinecraftClient
import tidinari.mbpunish.recognize.information.ChatGameInfo
import tidinari.mbpunish.recognize.information.NearInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo
import tidinari.mbpunish.recognize.information.abstraction.ModerInfo
import tidinari.mbpunish.recognize.information.abstraction.NickInfo
import tidinari.mbpunish.recognize.information.abstraction.PossibleNickInfo
import tidinari.mbpunish.screens.ModerMenu
import tidinari.mbpunish.screens.OneNickMenu
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource

class MenuChooser(private val rulesSource: RulesSource, private val settingsSource: SettingsSource) {

    fun chooseMenu(messageInfo: MessageInfo): BaseOwoScreen<FlowLayout>? {
        return when (messageInfo) {
            is ModerInfo -> ModerMenu(messageInfo.punisher, messageInfo.victim, rulesSource, settingsSource)
            is NickInfo -> OneNickMenu(messageInfo.nick, rulesSource, settingsSource)
            is PossibleNickInfo ->
                if (messageInfo.isReal) { OneNickMenu(messageInfo.nick, rulesSource, settingsSource) } else {
                    return null
                }
            is NearInfo -> return null
            is ChatGameInfo -> {
                MinecraftClient.getInstance().networkHandler?.sendChatMessage("${messageInfo.answer}")
                return null
            }
            else -> throw NotImplementedError("Menu is not found!")
        }
    }

    fun executeCommand(notRealName: String) {
        MinecraftClient.getInstance().networkHandler?.sendCommand("realname $notRealName")
    }

    fun openMenu(menu: BaseOwoScreen<FlowLayout>) {
        MinecraftClient.getInstance().setScreenAndRender(menu)
    }
}