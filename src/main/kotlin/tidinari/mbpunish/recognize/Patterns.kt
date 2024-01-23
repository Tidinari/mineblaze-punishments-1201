package tidinari.mbpunish.recognize

import tidinari.mbpunish.recognize.patterns.MessagePattern
import tidinari.mbpunish.recognize.patterns.ModerCommandPattern

enum class Patterns(val messagePattern: MessagePattern) {
    PUNISH(ModerCommandPattern())
}