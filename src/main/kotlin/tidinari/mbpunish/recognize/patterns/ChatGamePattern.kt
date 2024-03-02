package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.ChatGameInfo
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class ChatGamePattern: MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        return siblings.size >= 4
                && siblings[0].string.startsWith("Chat Game ")
                && siblings[2].string.startsWith("Решите пример: ")
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        val expressionsParts = siblings[3].string.trim().split(" ")
        val answer = if (expressionsParts[1] == "-") {
            expressionsParts[0].toInt() - expressionsParts[2].replace(".", "").toInt()
        } else if (expressionsParts[1] == "+") {
            expressionsParts[0].toInt() + expressionsParts[2].replace(".", "").toInt()
        } else {
            expressionsParts[0].toInt() * expressionsParts[2].replace(".", "").toInt()
        }
        return ChatGameInfo(answer)
    }
}