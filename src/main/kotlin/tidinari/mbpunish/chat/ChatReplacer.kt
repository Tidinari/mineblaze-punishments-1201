package tidinari.mbpunish.chat

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.minecraft.client.MinecraftClient
import tidinari.mbpunish.sources.replace.ReplaceSource


class ChatReplacer(private val replaceSource: ReplaceSource) {
    fun registerListeners() {
        ClientSendMessageEvents.MODIFY_COMMAND.register(ClientSendMessageEvents.ModifyCommand {
            replaceMessage(it)
        })
        ClientSendMessageEvents.ALLOW_CHAT.register(ClientSendMessageEvents.AllowChat {
            val msg = replaceMessage(it)
            if (msg.first() == '/') {
                MinecraftClient.getInstance().networkHandler?.sendChatCommand(msg.drop(1))
                false
            } else {
                true
            }
        })
        ClientSendMessageEvents.MODIFY_CHAT.register(ClientSendMessageEvents.ModifyChat {
            val msg = replaceMessage(it)
            msg
        })
    }

    private fun replaceMessage(text: String): String {
        var modifiedMessage = text
        for (replacer in replaceSource.read()) {
            modifiedMessage = modifiedMessage.replace(replacer.pattern, replacer.toReplace)
        }
        return modifiedMessage
    }
}

