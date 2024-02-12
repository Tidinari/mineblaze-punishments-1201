package tidinari.mbpunish.recognize.information

import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class ChatGameInfo(val answer: Int): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(answer)
    }
}