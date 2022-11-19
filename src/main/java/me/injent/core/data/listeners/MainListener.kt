package me.injent.core.data.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.injent.core.Main
import me.injent.core.globalOptions
import me.injent.core.gui.editJoinMsg
import me.injent.core.gui.editQuitMsg
import me.injent.core.gui.openPersonaMenu
import me.injent.core.utils.GameState
import me.injent.core.utils.MODEL_DATA_JOIN_MSG
import me.injent.core.utils.MODEL_DATA_PERSONA
import me.injent.core.utils.MODEL_DATA_QUIT_MSG
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.WorldInitEvent

class MainListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onAnvilTakeResult(event: PrepareAnvilEvent) {
        val anvil = event.inventory
        val holder = anvil.holder as Player?
        if (holder != null && anvil.result != null) holder.sendMessage(anvil.result!!.itemMeta.displayName()!!)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onWorldInit(event: WorldInitEvent) {
        event.world.keepSpawnInMemory = false
    }

    @EventHandler
    private fun onChat(event: AsyncChatEvent) {
        event.viewers().forEach {
            it.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f))
        }
    }

    @EventHandler
    private fun onItemClick(event: PlayerInteractEvent) {
        if (globalOptions.gameState !is GameState.Lobby) return
        when (event.player.inventory.itemInMainHand.itemMeta.customModelData) {
            MODEL_DATA_PERSONA -> event.player.openPersonaMenu(Main.instance!!)

        }
    }

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player || globalOptions.gameState !is GameState.Lobby) return
        if (event.click == ClickType.DOUBLE_CLICK || event.click == ClickType.SHIFT_LEFT) return
        event.isCancelled = true
        val player = event.whoClicked as Player
        val item = event.inventory.getItem(event.rawSlot)
        item?.let {
            if (!it.itemMeta.hasCustomModelData()) return
            player.closeInventory()
            when (it.itemMeta.customModelData) {
                MODEL_DATA_JOIN_MSG -> player.editJoinMsg(Main.instance!!)
                MODEL_DATA_QUIT_MSG -> player.editQuitMsg(Main.instance!!)
                else -> return
            }
        }
    }
}