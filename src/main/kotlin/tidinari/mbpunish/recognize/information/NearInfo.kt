package tidinari.mbpunish.recognize.information

import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class NearInfo(val nicks: List<Pair<String, Boolean>>): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(nicks)
    }
}