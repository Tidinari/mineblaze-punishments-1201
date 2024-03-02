package tidinari.mbpunish.data

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.core.Insets
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class UnsafeCommandExecutor(private val name: String, private val command: String): CommandExecutor {
    override fun component(violator: Violator) = (Components.button(Text.literal(name)) { execute(violator) }).margins(Insets.left(3))

    override fun execute(violator: Violator) {
        val commandToExecute = command.replace("%name%", violator.name())
        if (violator.name().isBlank() || violator.name().isEmpty()) {
            return
        }
        MinecraftClient.getInstance().networkHandler?.sendCommand(commandToExecute)
    }
}