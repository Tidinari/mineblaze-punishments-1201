package tidinari.mbpunish.recognize.information

class KickInfo(val punisher: String, val victim: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(punisher, victim)
    }
}