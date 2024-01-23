package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.BannedInfo
import tidinari.mbpunish.recognize.information.MessageInfo
import tidinari.mbpunish.recognize.information.MutedInfo

class PunishedPattern : MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size == 5
                && siblings[0].string.equals("[")
                && siblings[1].string.equals("*")
                && siblings[2].string.equals("] ")
                && siblings.last().string.startsWith("пытался")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        val nick = siblings[3].string.trim()
        when {
            siblings.last().string.contains("написать") -> return MutedInfo(nick)
            siblings.last().string.contains("зайти") -> return BannedInfo(nick)
        }
        throw IllegalArgumentException()
    }
}