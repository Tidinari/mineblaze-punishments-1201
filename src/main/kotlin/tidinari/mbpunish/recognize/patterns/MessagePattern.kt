package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.MessageInfo

interface MessagePattern {
    fun isMatches(siblings: List<Text>): Boolean
    fun parseMessage(siblings: List<Text>): MessageInfo
}