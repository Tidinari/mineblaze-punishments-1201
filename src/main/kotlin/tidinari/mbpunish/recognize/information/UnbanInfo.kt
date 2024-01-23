package tidinari.mbpunish.recognize.information

class UnbanInfo(val punisher: String, val victim: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(punisher, victim)
    }
}