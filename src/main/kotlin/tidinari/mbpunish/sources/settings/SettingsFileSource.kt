package tidinari.mbpunish.sources.settings

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import tidinari.mbpunish.MineBlazePunishments
import tidinari.mbpunish.sources.settings.data.Settings
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.readText

class SettingsFileSource: SettingsSource {
    private var instance: Settings? = null

    override fun init() {
        val config = FabricLoader.getInstance().configDir.resolve("mb-menu-settings.json")
        if (!config.exists()) {
            try {
                val stream = MineBlazePunishments.javaClass.classLoader.getResourceAsStream("menu_settings.json")
                Files.copy(stream, config)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    override fun read(): Settings {
        if (instance == null) {
            val config = FabricLoader.getInstance().configDir.resolve("mb-menu-settings.json")
            val jsonString = config.readText()
            instance = Json.decodeFromString<Settings>(jsonString)
            return instance!!
        } else {
            return instance!!
        }
    }

    override fun save(settings: Settings) {
        val config = FabricLoader.getInstance().configDir.resolve("mb-menu-settings.json")
        val jsonString = Json.encodeToString(settings)
        config.toFile().writeText(jsonString)
        instance = settings
    }
}