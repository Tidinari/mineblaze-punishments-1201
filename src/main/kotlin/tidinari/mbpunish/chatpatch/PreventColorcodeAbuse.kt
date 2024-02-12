package tidinari.mbpunish.chatpatch

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text

class PreventColorcodeAbuse {
    fun registerListener() {
        ClientReceiveMessageEvents.MODIFY_GAME.register(
                ClientReceiveMessageEvents.ModifyGame { message, _ ->
                    if (message.siblings.size >= 1 &&
                            message.siblings[0].style.clickEvent?.action == ClickEvent.Action.SUGGEST_COMMAND &&
                            message.siblings[0].style.clickEvent?.value?.startsWith("/msg ") == true
                    ) {
                        message.siblings.forEach {
                            if (it.string.contains("§")) {
                                it.siblings.replaceAll { text ->
                                    if (text.string.contains("§")) {
                                        return@replaceAll Text.literal(text.string
                                                .replace("§x", "").replace("§", ""))
                                    }
                                    text
                                }
                            }
                        }
                    }
                    return@ModifyGame message
                }
        )
    }
}