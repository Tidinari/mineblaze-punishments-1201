package tidinari.mbpunish.reminder

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import tidinari.mbpunish.recognize.information.*
import tidinari.mbpunish.recognize.information.abstraction.MoreModerInfo
import tidinari.mbpunish.recognize.patterns.JailPattern
import tidinari.mbpunish.recognize.patterns.ModerCommandPattern


class ModerCommandReminder {
    // patterns
    private val moderCommandPattern = ModerCommandPattern()
    private val jailPattern = JailPattern()

    // timer
    private var timer = mutableMapOf<MoreModerInfo, Instant>()

    fun registerListener() {
        ClientReceiveMessageEvents.GAME.register(
                ClientReceiveMessageEvents.Game { message, _ ->
                    if (message.siblings.isEmpty()) return@Game
                    val siblings = message.siblings.last().siblings
                    try {
                        val info = if (moderCommandPattern.isMatches(siblings)) {
                            moderCommandPattern.parseMessage(siblings)
                        } else if (jailPattern.isMatches(siblings)) {
                            jailPattern.parseMessage(siblings)
                        } else {
                            return@Game
                        }
                        if (info is MoreModerInfo) {
                            if (info.reason.contains("Причина не указана")) {
                                timer[info] = Clock.System.now().plus(20, DateTimeUnit.SECOND)
                            }
                        }
                    } catch (_: IllegalArgumentException) {
                    }
                }
        )

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            if (client.player == null || timer.isEmpty()) {
                return@EndTick
            }
            val iterator = timer.iterator()
            while (iterator.hasNext()) {
                val (info, time) = iterator.next()
                if (time.minus(Clock.System.now()).isNegative()) {
                    val punishment = when (info) {
                        is JailInfo -> "заджаилил"
                        is MuteInfo -> "замутил"
                        is BanInfo -> "забанил"
                        is KickInfo -> "кикнул"
                        is WarnInfo -> "предупредил"
                        else -> "наказал"
                    }
                    client.world!!.playSound(null, client.player!!.blockPos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f)
                    val message = Text
                            .literal("§e§l[20 секунд]§f §c${info.punisher} §f${punishment} §e${info.victim}§f | §c${info.reason}")
                            .setStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pnm ${info.punisher} ${info.victim}"))
                                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Наказать §c${info.punisher} §fи ${info.victim}"))))
                    client.player!!.sendMessage(message, false)
                    iterator.remove()
                }
            }
        })
    }
}