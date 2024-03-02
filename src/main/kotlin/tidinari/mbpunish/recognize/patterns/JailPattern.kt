package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.JailInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class JailPattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size > 8
                && siblings[0].string.startsWith("| ")
                && siblings[3].string.startsWith("посадил")
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        val punisher = siblings[2].string.trim()
        val victim = siblings[4].string.trim()
        val punishment = siblings.subList(8, siblings.size).joinToString(separator = "") { it.string }
        return JailInfo(punisher, victim, punishment)
    }
}