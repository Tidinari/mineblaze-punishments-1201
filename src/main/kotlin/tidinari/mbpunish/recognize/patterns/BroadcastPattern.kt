package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.BroadcastInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class BroadcastPattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size >= 7
                && siblings[0].string.startsWith("[")
                && siblings[1].string.startsWith("Объявление")
                && siblings[2].string.startsWith("]")
                && siblings[siblings.size - 3].string.startsWith("(Пишет:")
                && siblings[siblings.size - 1].string.startsWith(")")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        return BroadcastInfo(siblings[siblings.size - 2].string.trim())
    }
}