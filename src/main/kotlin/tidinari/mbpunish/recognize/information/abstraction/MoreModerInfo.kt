package tidinari.mbpunish.recognize.information.abstraction

abstract class MoreModerInfo(punisher: String, victim: String, val reason: String): ModerInfo(punisher, victim) {
    override fun getValues(): List<Any> {
        return listOf(punisher, victim, reason)
    }
}