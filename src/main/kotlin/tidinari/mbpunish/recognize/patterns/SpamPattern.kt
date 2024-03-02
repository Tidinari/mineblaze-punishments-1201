package tidinari.mbpunish.recognize.patterns

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.abstraction.MessageInfo

class SpamPattern : MessagePattern {
    override fun isMatches(message: Text, siblings: List<Text>): Boolean {
        val message = siblings.joinToString("") { it.string }
        return message == "."
                || (!message.any { it.isLetterOrDigit() } && !siblings.any { it.style.clickEvent?.value?.isNotEmpty() == true || it.style.hoverEvent != null })
                || (siblings.size >= 2
                && ((siblings[0].string.startsWith("            ") && siblings[1].string.startsWith("Донат покупать на сайте "))
                || (siblings[0].string.startsWith("                  ") && siblings[1].string.startsWith("Донат навсегда и после вайпа сохраняется!"))
                || (siblings[0].string.startsWith("                   ") && siblings[1].string.startsWith("Получить КРУТОЙ вертолет бесплатно: "))
                || (siblings[0].string.startsWith("                    ") && siblings[1].string.startsWith("Добро пожаловать на сервер "))
                || (siblings[0].string.startsWith("| ") && siblings[1].string.startsWith("Покупка кейса производится на сайте › "))
                || (siblings[0].string.startsWith("| ") && siblings[1].string.startsWith("Покупка донат-кейсов производится на сайте › "))))
                || (siblings.size >= 4
                && ((siblings[0].string.startsWith("| ") && siblings[1].string.equals("Игрок ") && siblings[3].string.equals(" выиграл "))
                || (siblings[0].string.startsWith("| ") && siblings[1].string.equals("Игрок ") && siblings[3].string.equals(" выбил "))))
    }

    override fun parseMessage(message: Text, siblings: List<Text>): MessageInfo {
        throw NotImplementedException("Spam doesn't have anything to show")
    }
}