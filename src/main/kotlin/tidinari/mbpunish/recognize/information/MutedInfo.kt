package tidinari.mbpunish.recognize.information

class MutedInfo(val nick: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(nick)
    }
}