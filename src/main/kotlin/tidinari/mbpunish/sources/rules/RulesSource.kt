package tidinari.mbpunish.sources.rules

import tidinari.mbpunish.data.Rule
import tidinari.mbpunish.sources.rules.data.EditableRules

interface RulesSource {
    fun init()
    fun read(): List<Rule>
    fun save(rules: EditableRules)
}