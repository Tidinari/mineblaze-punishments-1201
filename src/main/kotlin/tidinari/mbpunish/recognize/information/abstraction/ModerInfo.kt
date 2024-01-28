package tidinari.mbpunish.recognize.information.abstraction

abstract class ModerInfo(val punisher: String, val victim: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(punisher, victim)
    }
}