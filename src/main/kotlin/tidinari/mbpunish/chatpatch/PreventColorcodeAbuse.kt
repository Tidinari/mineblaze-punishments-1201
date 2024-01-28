package tidinari.mbpunish.chatpatch

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

class PreventColorcodeAbuse {
    fun registerListener() {
        ClientReceiveMessageEvents.MODIFY_GAME.register(
                ClientReceiveMessageEvents.ModifyGame { message, _ ->
                    if (message.string.contains("§")) {
                        message.siblings.forEach {
                            if (it.string.contains("§")) {
                                it.siblings.replaceAll { text ->
                                    if (text.string.contains("§x")) {
                                        return@replaceAll Text.literal(text.string.replace("§x", ""))
                                    }
                                    if (text.string.contains("§")) {
                                        return@replaceAll Text.literal(text.string.replace("§", ""))
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