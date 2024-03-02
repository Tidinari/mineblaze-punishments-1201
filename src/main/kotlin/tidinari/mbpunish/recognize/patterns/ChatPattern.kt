package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.ChatInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class ChatPattern : MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.isNotEmpty() && siblings[0].siblings.isNotEmpty()
                && siblings[0].style.clickEvent?.action == ClickEvent.Action.SUGGEST_COMMAND
                && siblings[0].style.clickEvent?.value?.startsWith("/msg ") == true
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        return ChatInfo(siblings[0].style.clickEvent!!.value.drop(5).dropLast(1))
    }
}