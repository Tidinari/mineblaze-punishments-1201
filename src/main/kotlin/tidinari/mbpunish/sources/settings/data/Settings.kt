package tidinari.mbpunish.sources.settings.data

import kotlinx.serialization.Serializable

@Serializable
class Settings(
    val punishmentsInRow: Int = 1,
    val rulesInColumn: Int = 5,
    val useEntityAction: Boolean = true,
    val chatsAction: Boolean = true,
    val stopChatOnFocus: Boolean = true,
    val rulesInSettingPerPage: Int = 5
    ) {

    fun rebuildWithParameters(
        changedPunishmentsInRow: Int? = null,
        changedRulesInColumn: Int? = null,
        changedUseEntityAction: Boolean? = null,
        changedChatsAction: Boolean? = null,
        changedStopChatOnFocus: Boolean? = null,
        changedRulesInSettingPerPage: Int? = null
    ): Settings {
        return Settings(
            changedPunishmentsInRow ?: punishmentsInRow,
            changedRulesInColumn ?: rulesInColumn,
            changedUseEntityAction ?: useEntityAction,
            changedChatsAction ?: chatsAction,
            changedStopChatOnFocus ?: stopChatOnFocus,
            changedRulesInSettingPerPage ?: rulesInSettingPerPage
        )
    }
}