package tidinari.mbpunish.data

import io.wispforest.owo.ui.core.Component

interface CommandExecutor {
    /**
     * For UI.
     * @return Component to render. Primarily button.
     */
    fun component(violator: Violator): Component

    /**
     * Executes command
     */
    fun execute(violator: Violator)
}