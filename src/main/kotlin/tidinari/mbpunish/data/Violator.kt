package tidinari.mbpunish.data

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import io.wispforest.owo.ui.event.FocusGained
import net.minecraft.text.Text

class Violator(name: String) {

    private val textBox = Components
        .textBox(Sizing.fill(25), name)

    fun display(root: FlowLayout) {
        root.child(
            Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(45))
                .apply { horizontalAlignment(HorizontalAlignment.CENTER) }
                .children(listOf(
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .apply { verticalAlignment(VerticalAlignment.CENTER) }
                        .child(Components.label(Text.literal("Ник: ")).margins(Insets.right(5)))
                        .child(textBox
                            .apply { focusGained().subscribe(FocusGained { setCursorToEnd() }) }
                        ),
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .apply { verticalAlignment(VerticalAlignment.CENTER) }
                        .children(
                            listOf(
                                UnsafeCommandExecutor("homes", "home %name%:").component(this),
                                UnsafeCommandExecutor("rg list", "rg list -p %name%").component(this),
                                UnsafeCommandExecutor("shist", "shist %name%").component(this),
                                UnsafeCommandExecutor("hist", "hist %name%").component(this),
                                UnsafeCommandExecutor("alts", "alts %name%").component(this),
                                UnsafeCommandExecutor("unmute", "unmute %name%").component(this),
                                UnsafeCommandExecutor("unwarn", "unwarn %name%").component(this),
                                UnsafeCommandExecutor("unban", "unban %name%").component(this),
                                UnsafeCommandExecutor("doninfo", "doninfo %name%").component(this),
                            )
                        )
                ))
        )
    }

    fun name() = textBox.text.trim()
}