package tidinari.mbpunish.sources.rules.data

import kotlinx.serialization.Serializable
import tidinari.mbpunish.data.EditableRule
import tidinari.mbpunish.data.Rule

@Serializable
data class Rules(
    val rules: List<Rule>
)

data class EditableRules(
    val rules: List<EditableRule>
) {
    fun asNormalRules(): Rules {
        return Rules(
            rules.map { it.asNormalRule() }
        )
    }
}