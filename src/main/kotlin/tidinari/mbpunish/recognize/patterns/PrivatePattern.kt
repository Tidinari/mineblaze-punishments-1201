package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.PrivateInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class PrivatePattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return message.style.clickEvent?.action == ClickEvent.Action.SUGGEST_COMMAND
                && message.style.clickEvent?.value?.startsWith("/m ") == true
                && message.style.hoverEvent?.action == HoverEvent.Action.SHOW_TEXT
                && message.style.hoverEvent?.getValue(HoverEvent.Action.SHOW_TEXT)
                    ?.string?.startsWith("Нажмите, чтобы ответить игроку") == true
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        return PrivateInfo(message.style.clickEvent!!.value.drop(3).dropLast(1), true)
    }
}