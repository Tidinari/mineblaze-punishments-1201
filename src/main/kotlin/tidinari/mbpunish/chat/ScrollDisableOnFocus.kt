package tidinari.mbpunish.chat

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.Text
import tidinari.mbpunish.sources.settings.SettingsSource

class ScrollDisableOnFocus(private val settingsSource: SettingsSource) {
    private var chatFocused = false

    fun handleChat() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            if (it.player == null)
                return@EndTick
            if (!chatFocused && it.currentScreen is ChatScreen) {
                if (settingsSource.read().stopChatOnFocus) {
                    it.inGameHud.chatHud.addMessage(Text.literal(" "))
                    it.inGameHud.chatHud.scroll(1)
                }
                chatFocused = true
            } else if (chatFocused && it.currentScreen !is ChatScreen) {
                chatFocused = false
            }
        })
    }
}