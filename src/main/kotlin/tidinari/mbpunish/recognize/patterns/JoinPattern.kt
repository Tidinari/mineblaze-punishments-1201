package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.JoinInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class JoinPattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size >= 3
                && siblings[0].string.equals("› ")
                && siblings[1].string.equals("Игрок ")
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        val nick = siblings[siblings.size - 2].string.trim()
        val split = nick.split(" ")
        if (split.size == 2) {
            return JoinInfo(split[1])
        }
        return JoinInfo(nick)
    }
}