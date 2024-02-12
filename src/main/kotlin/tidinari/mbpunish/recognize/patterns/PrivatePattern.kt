package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.PrivateInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class PrivatePattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size >= 6
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[2].string.equals("[")
                && !siblings[3].string.equals("SS")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        if (siblings[3].string.startsWith("~~")) {
            val notRealName = siblings.subList(3, siblings.map { it.string }.indexOf(" -> "))
                    .joinToString("") { it.string }.removePrefix("~~")
            return PrivateInfo(notRealName, false)
        }
        val name = siblings[3].string
        return PrivateInfo(name, true)
    }
}