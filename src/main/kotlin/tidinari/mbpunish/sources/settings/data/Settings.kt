package tidinari.mbpunish.sources.settings.data

import kotlinx.serialization.Serializable

@Serializable
class Settings(
    val punishmentsInRow: Int = 1,
    val rulesInColumn: Int = 5,
    val useEntityAction: Boolean = true,
    val chatsAction: Boolean = true,
    val stopChatOnFocus: Boolean = true,
    val rulesInSettingPerPage: Int = 5,
    val punishment: String = "§4[Н]",
    val unpunish: String = "§a[Р]",
    val victim: String = "§e[П]",
    val realname: String = "§3[R]",
    val oldMenu: Boolean = true,
    val spamFilter: Boolean = true,
    val disableColorAbuse: Boolean = true
    ) {

    fun rebuildWithParameters(
        changedPunishmentsInRow: Int? = null,
        changedRulesInColumn: Int? = null,
        changedUseEntityAction: Boolean? = null,
        changedChatsAction: Boolean? = null,
        changedStopChatOnFocus: Boolean? = null,
        changedRulesInSettingPerPage: Int? = null,
        changedPunishment: String? = null,
        changedUnpunish: String? = null,
        changedVictim: String? = null,
        changedRealname: String? = null,
        changedOldMenu: Boolean? = null,
        changedSpamFilter: Boolean? = null,
        changedDisableColorAbuse: Boolean? = null
    ): Settings {
        return Settings(
            changedPunishmentsInRow ?: punishmentsInRow,
            changedRulesInColumn ?: rulesInColumn,
            changedUseEntityAction ?: useEntityAction,
            changedChatsAction ?: chatsAction,
            changedStopChatOnFocus ?: stopChatOnFocus,
            changedRulesInSettingPerPage ?: rulesInSettingPerPage,
            changedPunishment?.replace("&", "§") ?: punishment,
            changedUnpunish?.replace("&", "§") ?: unpunish,
            changedVictim?.replace("&", "§") ?: victim,
            changedRealname?.replace("&", "§") ?: realname,
            changedOldMenu ?: oldMenu,
            changedSpamFilter ?: spamFilter,
            changedDisableColorAbuse ?: disableColorAbuse
        )
    }
}