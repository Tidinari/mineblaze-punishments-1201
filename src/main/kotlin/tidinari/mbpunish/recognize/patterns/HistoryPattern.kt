package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.*
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class HistoryPattern: MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        // PUNISH
        if (siblings.size >= 9) {
            if (siblings[0].string.equals("- ")) {
                val punishment = siblings[2].string.trim()
                operator fun String.unaryPlus() = punishment.startsWith(this)
                return +"кикнут" || +"забанен" || +"замучен" || +"предупрежден"
            }
        }

        // UNPUNISH
        if (siblings.size == 5) {
            if (siblings[0].string.equals("* ")) {
                val punishment = siblings[2].string.trim()
                operator fun String.unaryPlus() = punishment.startsWith(this)
                return +"был разбанен" || +"отменено предупреждение" || +"был размучен"
            }
        }
        return false
    }

    override fun parseMessage(siblings: List<Text>): MessageInfo {
        if (siblings.size == 5) {
            val punisher = siblings[3].string.trim() // Ник наказавшего
            val victim = siblings[1].string.trim() // Ник того, у кого сняли наказание
            val punishment = siblings[2].string.trim() // Снятое наказание
            operator fun String.unaryPlus() = punishment.startsWith(this)
            when {
                +"был разбанен" -> return UnbanInfo(punisher, victim)
                +"отменено предупреждение" -> return UnwarnInfo(punisher, victim)
                +"был размучен" -> return UnmuteInfo(punisher, victim)
            }
        } else {
            val punisher = siblings[3].string.trim() // Ник наказавшего
            val victim = siblings[1].string.trim() // Ник потерпевшего
            val punishment = siblings[2].string.trim() // Наказание
            operator fun String.unaryPlus() = punishment.startsWith(this)
            when {
                +"кикнут" -> {
                    val reason = siblings.subList(7, siblings.size - 2).joinToString(separator = "") { it.string }
                    return KickInfo(punisher, victim, reason)
                }
                +"забанен" -> {
                    val reason = siblings.subList(7, siblings.size - 2).joinToString(separator = "") { it.string }
                    return BanInfo(punisher, victim, reason)
                }
                +"замучен" -> {
                    val reason = siblings.subList(7, siblings.size - 2).joinToString(separator = "") { it.string }
                    return MuteInfo(punisher, victim, reason)
                }
                +"предупрежден" -> {
                    val reason = siblings.subList(7, siblings.size - 2).joinToString(separator = "") { it.string }
                    return WarnInfo(punisher, victim, reason)
                }
            }
        }
        throw IllegalArgumentException()
    }
}