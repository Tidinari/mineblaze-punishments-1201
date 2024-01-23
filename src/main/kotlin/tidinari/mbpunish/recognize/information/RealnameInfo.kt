package tidinari.mbpunish.recognize.information

class RealnameInfo(val nick: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(nick)
    }
}