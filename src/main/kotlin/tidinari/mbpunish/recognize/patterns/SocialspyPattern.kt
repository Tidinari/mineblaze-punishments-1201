package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.SocialspyInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class SocialspyPattern : MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size >= 12
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[2].string.equals("[")
                && siblings[3].string.equals("SS")
                && siblings[4].string.equals("] ")
                && siblings[5].string.equals("|")
                && siblings[6].string.equals(" ")
                && siblings[7].string.equals("[")
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        if (siblings[8].string.startsWith("~~")) {
            val notRealName = siblings.subList(8, siblings.map { it.string }.indexOf(" -> "))
                    .joinToString("") { it.string }.removePrefix("~~")
            return SocialspyInfo(notRealName, false)
        } else {
            val name = siblings[8].string
            return SocialspyInfo(name, true)
        }
    }
}