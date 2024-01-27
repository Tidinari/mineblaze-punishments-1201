package tidinari.mbpunish.recognize.information

class SocialspyInfo(val nick: String, val isReal: Boolean): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(nick, isReal)
    }
}