package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.ChatInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class ChatPattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        for (text in siblings) {
            return text.style.clickEvent?.action == ClickEvent.Action.SUGGEST_COMMAND &&
                    text.style.clickEvent?.value?.startsWith("/msg ") == true
        }
        return false
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        for (text in siblings) {
            if (text.style.clickEvent?.action == ClickEvent.Action.SUGGEST_COMMAND &&
                    text.style.clickEvent?.value?.startsWith("/msg ") == true
            ) {
                return ChatInfo(text.style.clickEvent!!.value.drop(5).dropLast(1))
            }
        }
        throw IllegalArgumentException()
    }
}