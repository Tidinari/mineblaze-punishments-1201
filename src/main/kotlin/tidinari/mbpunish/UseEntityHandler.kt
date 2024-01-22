package tidinari.mbpunish

import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import tidinari.mbpunish.screens.PunishmentMenu
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource

class UseEntityHandler(private val rulesSource: RulesSource, private val settingsSource: SettingsSource): UseEntityCallback {
    override fun interact(player: PlayerEntity?, world: World?, hand: Hand?, entity: Entity?, hitResult: EntityHitResult?): ActionResult {
        if (entity is PlayerEntity) {
            if (!settingsSource.read().useEntityAction) return ActionResult.PASS
            if (entity.name.string.isEmpty()) return ActionResult.PASS
            player?.sendMessage(Text.literal("Ты взаимодействовал с ${entity.name.string}"))
            MinecraftClient.getInstance().setScreen(PunishmentMenu(entity.name.string, rulesSource, settingsSource))
            return ActionResult.SUCCESS
        }
        
        return ActionResult.PASS
    }

}

