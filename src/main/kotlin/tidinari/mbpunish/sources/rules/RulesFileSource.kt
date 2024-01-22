package tidinari.mbpunish.sources.rules

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import tidinari.mbpunish.MineBlazePunishments
import tidinari.mbpunish.data.Rule
import tidinari.mbpunish.sources.rules.data.EditableRules
import tidinari.mbpunish.sources.rules.data.Rules
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.readText

class RulesFileSource: RulesSource {

    override fun init() {
        val config = FabricLoader.getInstance().configDir.resolve("mb-config.json")
        if (!config.exists()) {
            try {
                val stream = MineBlazePunishments.javaClass.classLoader.getResourceAsStream("config.json")
                Files.copy(stream, config)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    override fun read(): List<Rule> {
        val config = FabricLoader.getInstance().configDir.resolve("mb-config.json")
        val jsonString = config.readText()
        return Json.decodeFromString<Rules>(jsonString).rules
    }

    override fun save(rules: EditableRules) {
        val config = FabricLoader.getInstance().configDir.resolve("mb-config.json")
        val jsonString = Json.encodeToString(rules.asNormalRules())
        config.toFile().writeText(jsonString)
    }
}