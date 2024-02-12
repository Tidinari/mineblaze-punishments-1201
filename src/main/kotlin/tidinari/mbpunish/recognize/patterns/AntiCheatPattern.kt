package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.AntiCheatInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class AntiCheatPattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return siblings.size >= 5
                && siblings[0].string.equals("АнтиЧит")
                && siblings[1].string.equals(" ▸ ")
                && siblings[siblings.size - 1].string.equals(" кикнут по подозрению в использовании читов.")
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        val nick = siblings[siblings.size - 2].string.trim()
        return AntiCheatInfo(nick)
    }
}