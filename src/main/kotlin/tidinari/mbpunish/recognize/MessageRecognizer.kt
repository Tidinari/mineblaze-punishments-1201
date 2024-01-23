package tidinari.mbpunish.recognize

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.patterns.MessagePattern

class MessageRecognizer(private val text: Text) {

    private val patterns = Patterns.entries.map { it.messagePattern }

    fun recognize(message: Text): MessagePattern {
        patterns.forEach {
            if (it.isMatches(message.siblings)) {
                return it
            }
        }
        throw IllegalArgumentException()
    }
}