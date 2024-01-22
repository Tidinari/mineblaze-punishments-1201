package tidinari.mbpunish.sources.settings

import tidinari.mbpunish.sources.settings.data.Settings

interface SettingsSource {
    fun init()
    fun read(): Settings
    fun save(settings: Settings)
}