package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.AfkInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class AfkPattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size >= 4
                && siblings[0].string.startsWith("|")
                && siblings[1].string.startsWith(" ")
                && (siblings.last().string.endsWith("вернулся.") || siblings.last().string.endsWith("отошел."))
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        if (siblings[2].string.startsWith("~~")) {
            val notRealName = siblings.subList(2, siblings.size - 1)
                    .joinToString("") { it.string }.trim()
            return AfkInfo(notRealName, false)
        }
        val name = siblings[2].string.trim()
        return AfkInfo(name, true)
    }
}