package tidinari.mbpunish.data

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.text.Text
import tidinari.mbpunish.screens.utils.CorrectTextBox

@Serializable
class Rule(private val name: String, private val description: String, private val punishments: List<Punishment>) {

    fun display(root: FlowLayout, violator: Violator, punishmentsInARow: Int = 2, showDesc: (String, String) -> Unit) {
        root.children(
            listOf(
                nameAndButtonsComponent(violator, punishmentsInARow, showDesc),
            )
        )
    }

    private fun nameAndButtonsComponent(violator: Violator, punishmentsInARow: Int = 2, showDesc: (String, String) -> Unit) = Containers
        .horizontalFlow(Sizing.content(), Sizing.content())
        .apply {
            verticalAlignment(VerticalAlignment.CENTER)
            margins(Insets.bottom(7))
            if (punishments.size > punishmentsInARow) {
                child(nameComp(showDesc))
                child(Containers.verticalFlow(Sizing.content(), Sizing.content()).apply {
                    alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                    var rowContainer = constructPunishmentRow()
                    child(rowContainer)
                    for ((index, punishment) in punishments.withIndex()) {
                        if (index != 0 && index % punishmentsInARow == 0) {
                            rowContainer = constructPunishmentRow()
                            child(rowContainer)
                        }
                        rowContainer.child(punishment.component(violator))
                    }
                })
            } else {
                child(nameComp(showDesc))
                for (punish in punishments) {
                    child(punish.component(violator))
                }
            }
        }

    private fun constructPunishmentRow(): FlowLayout {
        return Containers.horizontalFlow(Sizing.content(), Sizing.content())
            .apply { alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER) }
    }

    fun nameComp(showDesc: (String, String) -> Unit) = Components
        .label(Text.literal(name))
        .margins(Insets.left(7).withRight(3))

    fun asEditable(): EditableRule {
        return EditableRule(name, description, punishments)
    }
}

class EditableRule(name: String, description: String, punishments: List<Punishment>) {
    private val name
        get() = nameBox.text()
    private val description
        get() = descBox.text()

    @Transient
    private val nameBox = CorrectTextBox(Sizing.fill(30), name).applyStyle {
        it.margins(Insets.left(7).withRight(3))
    }

    @Transient
    private val descBox = CorrectTextBox(Sizing.fill(95), description).applyStyle {
        it.margins(Insets.bottom(5))
    }

    @Transient
    private val punishments: MutableList<EditablePunishment> = punishments.map { it.asEditable() }.toMutableList()

    fun display(root: FlowLayout, index: Int, onUp: () -> Unit, onDown: () -> Unit, onAddingPunishment: () -> Unit) {
        root.child(
            nameAndButtonsComponent(index, onUp, onDown, onAddingPunishment),
        )
        descBox.display(root)
    }

    private fun nameAndButtonsComponent(index: Int, onUp: () -> Unit, onDown: () -> Unit, onAddingPunishment: () -> Unit) = Containers
        .horizontalFlow(Sizing.content(), Sizing.content())
        .apply {
            verticalAlignment(VerticalAlignment.CENTER)
            margins(Insets.bottom(2))
            child(
                Containers.verticalFlow(Sizing.content(), Sizing.content()).apply {
                    alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                }.children(listOf(
                    Components.button(Text.literal("⇧")) { onUp() }.apply {
                        if (index == 0) this.active(false)
                    },
                    Components.label(Text.literal(index.toString())),
                    Components.button(Text.literal("⇩")) { onDown() }.apply {  }
                ))
            )
            nameBox.display(this)
            if (punishments.size > 0) {
                child(Containers.verticalFlow(Sizing.content(), Sizing.content()).apply {
                    alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                    for (punishment in punishments) {
                        punishment.display(this)
                    }
                })
            }
            child(Components.button(Text.literal("✚")) {
                try {
                    if (punishments.last().isNotBlank()) {
                        punishments.add(Punishment("", "").asEditable())
                        onAddingPunishment()
                    }
                } catch (_: NoSuchElementException) {
                    punishments.add(Punishment("", "").asEditable())
                    onAddingPunishment()
                }
            })
        }

    fun isNotBlank(): Boolean {
        clearBlankPunishments()
        return name.isNotBlank() && punishments.any { it.isNotBlank() }
    }

    private fun clearBlankPunishments() {
        val newPunishments = punishments.filter { it.isNotBlank() }
        punishments.clear()
        punishments.addAll(newPunishments)
    }

    fun asNormalRule(): Rule {
        return Rule(name, description, punishments.filter { it.isNotBlank() }.map { it.asNormalPunishment() })
    }
}