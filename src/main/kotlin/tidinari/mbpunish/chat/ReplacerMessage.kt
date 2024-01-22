package tidinari.mbpunish.chat

import kotlinx.serialization.Serializable

@Serializable
data class ReplacerMessage(var pattern: String, var toReplace: String)
