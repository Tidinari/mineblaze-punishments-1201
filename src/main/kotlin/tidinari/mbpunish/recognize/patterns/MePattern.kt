package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.PrivateInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class MePattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size >= 3
                && siblings[0].string.equals("*")
                && siblings[1].string.startsWith(" ")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        if (siblings[1].string.startsWith(" ~~")) {
            val notRealName = siblings.subList(1, siblings.map { it.string }.indexOf(" "))
                    .joinToString("") { it.string }.trim()
            return PrivateInfo(notRealName, false)
        }
        val name = siblings[1].string.trim()
        return PrivateInfo(name, true)
    }
}