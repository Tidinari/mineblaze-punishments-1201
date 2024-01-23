package tidinari.mbpunish.recognize.patterns

import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.*

open class ModerCommandPattern : MessagePattern {
    override fun isMatches(siblings: List<Text>): Boolean {
        return (siblings.size > 6
                && (
                (siblings[0].string.startsWith("[")
                && siblings[1].string.startsWith("*")
                && siblings[2].string.startsWith("]"))
                || (siblings[0].string.startsWith("[Скрыто]")
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
        val victim = siblings[startIndex].string.trim() // Ник потерпевшего
        val punishment = siblings[startIndex].string.trim() // Наказание
        when (punishment.trim()) {
            "кикнул" -> return KickInfo(punisher)
            "забанил" -> return BanInfo(punisher, victim)
            "замутил" -> return MuteInfo(punisher, victim)
            "выдал предупреждение" -> return WarnInfo(punisher, victim)
        }
        throw IllegalArgumentException()
    }
}