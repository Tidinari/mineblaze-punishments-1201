package tidinari.mbpunish.recognize.information.abstraction

abstract class PossibleNickInfo(val nick: String, val isReal: Boolean): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(nick, isReal)
    }
}