package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.NearInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class NearPattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size > 4
                && siblings[0].string.equals("| ")
                && siblings[1].string.equals("Окружающие игроки: ")
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        val nicks = siblings.subList(3, siblings.size).map { it.string }
        var nick = ""
        var dropMeters = false
        var nextDrop = false
        var offside = 3
        val nearNicks = mutableListOf<Pair<String, Boolean>>()
        for ((index, str) in nicks.withIndex()) {
            if (str == "(") {
                if (nick.startsWith("~~")) {
                    nearNicks.add(nick to false)
                    offside++
                } else {
                    nearNicks.add(nick to true)
                    offside++
                }
                nick = ""
                dropMeters = true
            } else if (dropMeters) {
                nextDrop = true
                dropMeters = false
                continue
            } else if (nextDrop) {
                nick = str.drop(3)
                nextDrop = false
            } else {
                nick += if (index == 0) str.trim() else str
            }
        }
        return NearInfo(nearNicks)
    }
}