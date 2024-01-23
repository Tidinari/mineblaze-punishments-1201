package tidinari.mbpunish.recognize.information

class BannedInfo(val nick: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(nick)
    }
}