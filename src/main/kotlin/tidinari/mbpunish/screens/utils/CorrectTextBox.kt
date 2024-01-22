package tidinari.mbpunish.screens.utils

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Sizing

class CorrectTextBox(private val horizontalSizing: Sizing, private var textBoxText: String = "") {

    private var textBox = Components.textBox(horizontalSizing, textBoxText).apply {
        setMaxLength(512)
        if (textBoxText.length > 32) {
            text = ""
            write(textBoxText)
        }
    }

    fun construct(textBoxText: String) {
        this.textBoxText = textBoxText
        textBox = Components.textBox(horizontalSizing, textBoxText).apply {
            setMaxLength(512)
            if (textBoxText.length > 32) {
                this.text = ""
                write(textBoxText)
            }
        }
    }

    fun text(): String {
        return textBox.text
    }

    fun applyStyle(style: (TextBoxComponent) -> Unit): CorrectTextBox {
        style(textBox)
        return this
    }

    fun display(root: FlowLayout) {
        textBox.onChanged().subscribe(TextBoxComponent.OnChanged { textBoxText = it })
        root.child(textBox)
    }
}