package tidinari.mbpunish.recognize.information.abstraction

abstract class NickInfo(val nick: String): MessageInfo {
    override fun getValues() = listOf(nick)
}