package tidinari.mbpunish.data

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import tidinari.mbpunish.screens.utils.CorrectTextBox

@Serializable
class Punishment(private val name: String, private val command: String) {

    fun component(violator: Violator) = Components.button(Text.literal(name)) { execute(violator) }.margins(Insets.left(3))

    private fun execute(violator: Violator) {
        val commandToExecute = command.replace("%name%", violator.name())
        if ((violator.name().isBlank() || violator.name().isEmpty()) && commandToExecute != command) {
            return
        }
        MinecraftClient.getInstance().networkHandler?.sendCommand(commandToExecute)
    }

    fun asEditable(): EditablePunishment {
        val editable = EditablePunishment()
        editable.init(name, command)
        return editable
    }
}

@Serializable
class EditablePunishment {
    private val name
        get() = nameBox.text()
    private val command
        get() = commBox.text()

    @Transient
    private val nameBox = CorrectTextBox(Sizing.fill(20))

    @Transient
    private val commBox = CorrectTextBox(Sizing.fill(40))

    fun init(name: String, command: String) {
        nameBox.construct(name)
        commBox.construct(command)
    }

    fun display(root: FlowLayout) {
        root.child(
            Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .apply {
                    verticalAlignment(VerticalAlignment.CENTER)
                    children(
                        listOf(
                            editNameComponent(),
                            editCommandComponent()
                        )
                    )
                }
        )
    }

    private fun editNameComponent() = Containers
        .horizontalFlow(Sizing.content(), Sizing.content())
        .apply {
            verticalAlignment(VerticalAlignment.CENTER)
            nameBox.display(this)
        }

    private fun editCommandComponent() = Containers
        .horizontalFlow(Sizing.content(), Sizing.content())
        .apply {
            verticalAlignment(VerticalAlignment.CENTER)
            commBox.display(this)
        }

    fun isNotBlank(): Boolean {
        return name.isNotBlank() && command.isNotBlank()
    }

    fun asNormalPunishment(): Punishment {
        return Punishment(name, command)
    }
}