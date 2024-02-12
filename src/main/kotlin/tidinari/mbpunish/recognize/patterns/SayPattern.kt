package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.SayInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class SayPattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.isNotEmpty()
                && (siblings[0].string.startsWith("[~~")
                || (siblings[0].string.startsWith("[") && siblings[0].string.endsWith("]")))
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        if (siblings[0].string.startsWith("[~~")) {
            val text = siblings.joinToString("") { it.string }
            return SayInfo(text.substring(text.indexOf("["), text.indexOf("]")).drop(1), false)
        }
        return SayInfo(siblings[0].string.trim().drop(1).dropLast(1), true)
    }
}