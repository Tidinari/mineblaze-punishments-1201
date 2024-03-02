package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

interface MessagePattern {
    fun isMatches(message: Text, siblings: List<Text>): Boolean
    fun parseMessage(message: Text, siblings: List<Text>): MessageInfo
}