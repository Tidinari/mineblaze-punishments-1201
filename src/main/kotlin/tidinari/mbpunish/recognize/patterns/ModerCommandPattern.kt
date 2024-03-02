package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.*
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

open class ModerCommandPattern : MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        if (siblings.size >= 7) {
            if (siblings[0].string.startsWith("[")
                    && siblings[1].string.startsWith("*")
                    && siblings[2].string.startsWith("]")
            ) {

                val punishment = siblings[5].string.trim()
                operator fun String.unaryPlus() = punishment.startsWith(this)
                return +"кикнул" || +"забанил" || +"замутил" || +"выдал предупреждение"
                        || +"разбанил игрока" || +"снял предупреждение с" || +"размутил игрока"

            } else if (siblings[0].string.startsWith("[Скрыт]") && siblings[1].string.startsWith("[")
                    && siblings[2].string.startsWith("*")
                    && siblings[3].string.startsWith("]")) {

                val punishment = siblings[6].string.trim()
                operator fun String.unaryPlus() = punishment.startsWith(this)
                return +"кикнул" || +"забанил" || +"замутил" || +"выдал предупреждение"
                        || +"разбанил игрока" || +"снял предупреждение с" || +"размутил игрока"

            }
        }
        return false
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        val isHide = siblings[0].string.startsWith("[Скрыт]")
        val startIndex = if (isHide) 5 else 4

        val punisher = siblings[startIndex].string.trim() // Ник наказавшего
        val victim = siblings[startIndex + 2].string.trim() // Ник потерпевшего
        val punishment = siblings[startIndex + 1].string.trim() // Наказание
        operator fun String.unaryPlus() = punishment.startsWith(this)
        when {
            +"кикнул" -> {
                val reason = siblings.subList(startIndex + 4, siblings.size).joinToString(separator = "") { it.string }.dropLast(1)
                return KickInfo(victim, punisher, reason)
            } // Игрока [игрок] кикнул [наказавший]
            +"забанил" -> {
                val reasonIndex = if (siblings[startIndex + 3].string.trim().startsWith("навсегда")) startIndex + 4 else startIndex + 6
                val reason = siblings.subList(reasonIndex, siblings.size).joinToString(separator = "") { it.string }.dropLast(1)
                return BanInfo(punisher, victim, reason)
            }
            +"замутил" -> {
                val reasonIndex = if (siblings[startIndex + 3].string.trim().startsWith("навсегда")) startIndex + 4 else startIndex + 6
                val reason = siblings.subList(reasonIndex, siblings.size).joinToString(separator = "") { it.string }.dropLast(1)
                return MuteInfo(punisher, victim, reason)
            }
            +"выдал предупреждение" -> {
                val reason = siblings.subList(startIndex + 4, siblings.size).joinToString(separator = "") { it.string }.dropLast(1)
                return WarnInfo(punisher, victim, reason)
            }
            +"разбанил игрока" -> return UnbanInfo(punisher, victim)
            +"снял предупреждение с" -> return UnwarnInfo(punisher, victim)
            +"размутил игрока" -> return UnmuteInfo(punisher, victim)
        }
        throw IllegalArgumentException()
    }
}