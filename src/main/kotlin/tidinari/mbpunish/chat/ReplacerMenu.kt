package tidinari.mbpunish.chat

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import tidinari.mbpunish.sources.replace.ReplaceSource

class ReplacerMenu(
    private val replaceSource: ReplaceSource,
    private val replacerPatterns: MutableList<ReplacerMessage> = replaceSource.read().toMutableList()
) : BaseOwoScreen<FlowLayout>() {

    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    override fun build(rootComponent: FlowLayout?) {
        rootComponent?.apply {
            surface(Surface.VANILLA_TRANSLUCENT)
            horizontalAlignment(HorizontalAlignment.CENTER)
            verticalAlignment(VerticalAlignment.TOP)
            padding(Insets.of(3))
            val mainContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.content())
            val mainContainerScroll = Containers.verticalScroll(Sizing.content(), Sizing.fixed(height - 80), mainContainer)
            child(mainContainerScroll)
            for ((index, replacer) in replacerPatterns.withIndex()) {
                mainContainer.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .apply {
                        alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                        val pattern = Components.textBox(Sizing.fill(49), replacer.pattern)
                        pattern.setMaxLength(255)
                        if (replacerPatterns[index].pattern.length > 32) {
                            pattern.text = ""
                            pattern.write(replacerPatterns[index].pattern)
                        }
                        pattern.onChanged()
                            .subscribe(TextBoxComponent.OnChanged {
                                replacerPatterns[index] = ReplacerMessage(it, replacerPatterns[index].toReplace)
                            })
                        val toReplace = Components.textBox(Sizing.fill(49), replacer.toReplace)
                        toReplace.setMaxLength(255)
                        if (replacerPatterns[index].toReplace.length > 32) {
                            toReplace.text = ""
                            toReplace.write(replacerPatterns[index].toReplace)
                        }
                        toReplace.onChanged()
                            .subscribe(TextBoxComponent.OnChanged {
                                replacerPatterns[index] = ReplacerMessage(replacerPatterns[index].pattern, it)
                            })
                        children(listOf(pattern, toReplace))
                    }
                )
            }
            child(Components.label(Text.literal("Пустые значения удаляются. Большие буквы или маленькие - имеет значение. Длинный текст сохраняется, просто не отображается до нажатия.")).apply {
                maxWidth(width)
            })
            child(Components.button(Text.literal("Добавить")) {
                replacerPatterns.add(ReplacerMessage("", ""))
                rebuild()
            })
            child(Components.button(Text.literal("Сохранить")) {
                replaceSource.save(replacerPatterns.filter { it.pattern.isNotBlank() && it.toReplace.isNotBlank() })
            })
        }
    }

    private fun rebuild() {
        MinecraftClient.getInstance().setScreen(ReplacerMenu(replaceSource, replacerPatterns))
    }
}