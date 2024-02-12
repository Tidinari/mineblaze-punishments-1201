package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.PrivateInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class AdminChatPattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size >= 2
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[2].string.equals("[Админ-Чат]")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        if (siblings[4].string.startsWith("~~")) {
            val text = siblings.map { it.string }
            val end = text.indexOf(":")
            return PrivateInfo(text.subList(4, end).drop(1).joinToString(""), false)
        }
        return PrivateInfo(siblings[4].string.trim().dropLast(1), true)
    }
}