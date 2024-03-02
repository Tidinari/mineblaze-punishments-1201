package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.ClearChatInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class ClearChatPattern : MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size == 5
                && siblings[0].string.equals("[")
                && siblings[1].string.equals("*")
                && siblings[2].string.equals("] ")
                && siblings[3].string.equals("Чат очищен игроком ")
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        return ClearChatInfo(siblings[4].string.trim())
    }
}