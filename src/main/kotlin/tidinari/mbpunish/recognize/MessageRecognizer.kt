package tidinari.mbpunish.recognize

import net.minecraft.text.Text
import org.apache.commons.lang3.NotImplementedException
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo
import tidinari.mbpunish.recognize.patterns.MessagePattern

class MessageRecognizer {

    private val patterns = Patterns.entries.map { it.messagePattern }

    fun recognize(message: Text): MessagePattern {
        val siblings = message.siblings.last().siblings.drop(1)
        patterns.forEach {
            if (it.isMatches(siblings[0].siblings)) {
                return it
            }
        }
        throw NotImplementedException()
    }

    fun recognize(siblings: List<Text>): MessagePattern {
        patterns.forEach {
            if (it.isMatches(siblings)) {
                return it
            }
        }
        throw NotImplementedException()
    }

    fun parseMessage(message: Text, messagePattern: MessagePattern): MessageInfo {
        val siblings = message.siblings.last().siblings.drop(1)
        return messagePattern.parseMessage(siblings[0].siblings)
    }
}