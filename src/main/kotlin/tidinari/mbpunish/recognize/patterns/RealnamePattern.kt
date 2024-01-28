package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.RealnameInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class RealnamePattern : MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size >= 5
                && siblings[0].string.equals("|")
                && siblings[1].string.equals(" ")
                && siblings[siblings.size - 2].string.equals(" является ")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        val nick = siblings.last().string.trim()
        return RealnameInfo(nick)
    }
}