package tidinari.mbpunish.sources.replace

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import tidinari.mbpunish.MineBlazePunishments
import tidinari.mbpunish.chat.ReplacerMessage
import tidinari.mbpunish.sources.replace.data.ReplacerPatterns
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.readText

class ReplaceFileSource: ReplaceSource {

    override fun init() {
        val config = FabricLoader.getInstance().configDir.resolve("mb-config-replacer.json")
        if (!config.exists()) {
            try {
                val stream = MineBlazePunishments.javaClass.classLoader.getResourceAsStream("replacer.json")
                Files.copy(stream, config)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    override fun read(): List<ReplacerMessage> {
        val config = FabricLoader.getInstance().configDir.resolve("mb-config-replacer.json")
        val jsonString = config.readText()
        return Json.decodeFromString<ReplacerPatterns>(jsonString).replaciests
    }

    override fun save(replacerMessages: List<ReplacerMessage>) {
        val config = FabricLoader.getInstance().configDir.resolve("mb-config-replacer.json")
        println("Saving: $replacerMessages")
        val jsonString = Json.encodeToString(ReplacerPatterns(replacerMessages))
        println("Saving: $jsonString")
        config.toFile().writeText(jsonString)
    }
}