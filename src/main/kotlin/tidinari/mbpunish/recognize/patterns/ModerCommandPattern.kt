package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.*
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

open class ModerCommandPattern : MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return (siblings.size >= 6
                && (
                (siblings[0].string.startsWith("[")
                && siblings[1].string.startsWith("*")
                && siblings[2].string.startsWith("]"))
                || (siblings[0].string.startsWith("[Скрыт]")
                && siblings[1].string.startsWith("[")
                && siblings[2].string.startsWith("*")
                && siblings[3].string.startsWith("]"))
                )
        )
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        val isHide = siblings[0].string.startsWith("[Скрыто]")
        val startIndex = if (isHide) 4 else 5

        val punisher = siblings[startIndex].string.trim() // Ник наказавшего
        val victim = siblings[startIndex + 2].string.trim() // Ник потерпевшего
        val punishment = siblings[startIndex + 1].string.trim() // Наказание
        operator fun String.unaryPlus() = punishment.startsWith(this)
        when {
            +"был кикнут" -> return KickInfo(victim, punisher) // [игрок] был кикнут [наказавший]
            +"забанил" -> return BanInfo(punisher, victim)
            +"замутил" -> return MuteInfo(punisher, victim)
            +"выдал предупреждение" -> return WarnInfo(punisher, victim)
            +"разбанил игрока" -> return UnbanInfo(punisher, victim)
            +"снял предупреждение с" -> return UnwarnInfo(punisher, victim)
            +"размутил игрока" -> return UnmuteInfo(punisher, victim)
        }
        throw IllegalArgumentException()
    }
}