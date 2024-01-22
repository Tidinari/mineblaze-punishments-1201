package tidinari.mbpunish.sources.replace.data

import kotlinx.serialization.Serializable
import tidinari.mbpunish.chat.ReplacerMessage

@Serializable
data class ReplacerPatterns(
    val replaciests: List<ReplacerMessage>
)