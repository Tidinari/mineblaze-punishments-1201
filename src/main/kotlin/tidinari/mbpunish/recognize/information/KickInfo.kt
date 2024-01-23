package tidinari.mbpunish.recognize.information

class KickInfo(val punisher: String): MessageInfo {
    override fun getValues(): List<Any> {
        return listOf(punisher)
    }
}