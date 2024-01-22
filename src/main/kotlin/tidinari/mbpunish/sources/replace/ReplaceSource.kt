package tidinari.mbpunish.sources.replace

import tidinari.mbpunish.chat.ReplacerMessage

interface ReplaceSource {
    fun init()
    fun read(): List<ReplacerMessage>
    fun save(replacerMessages: List<ReplacerMessage>)
}