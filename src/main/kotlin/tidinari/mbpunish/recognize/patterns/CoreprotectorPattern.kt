package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.CoreprotectorInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class CoreprotectorPattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size in 3..6
                && (siblings[1].string.startsWith(" §c- ") || siblings[1].string.startsWith(" §a+ "))
                && (siblings[2].string.endsWith("§f broke ") || siblings[2].string.endsWith("§f placed "))
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        val nick = siblings[2].string.split("§f")[0].trim()
        return CoreprotectorInfo(nick)
    }
}