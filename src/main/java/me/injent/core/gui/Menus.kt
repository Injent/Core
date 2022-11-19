package me.injent.core.gui

import me.injent.core.data.DataRepository
import me.injent.core.data.PlayerData
import me.injent.core.data.TeamData
import me.injent.core.data.team
import me.injent.core.utils.MODEL_DATA_JOIN_MSG
import me.injent.core.utils.MODEL_DATA_QUIT_MSG
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.io.IOException

fun Player.openPersonaMenu(plugin: Plugin) {
    val inventory = Bukkit.createInventory(this, 27, Component.text("container.persona")).apply {
        val join = ItemStack(Material.PAPER)
        join.editMeta { meta ->
            meta.setCustomModelData(MODEL_DATA_JOIN_MSG)
            meta.displayName(Component.text("item.join_msg"))
        }
        val quit = ItemStack(Material.PAPER)
        quit.editMeta { meta ->
            meta.setCustomModelData(MODEL_DATA_QUIT_MSG)
            meta.displayName(Component.text("item.quit_msg"))
        }
        setItem(0, join)
        setItem(1, quit)
    }
    this.openInventory(inventory)
}

fun Player.editJoinMsg(plugin: Plugin) {
    PlayerData[this.uniqueId]?.persona?.let { persona ->
        val paper = ItemStack(Material.PAPER)
        paper.editMeta {
            it.displayName(Component.text(persona.joinMsg))
        }
        AnvilGUI.Builder()
            .plugin(plugin)
            .onClose { player ->
                player.sendMessage(Component.translatable("tellraw.settings.saved"))
                player.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f))
            }
            .title(plugin.config.getString("lang.edit_join_msg"))
            .itemLeft(paper)
            .onComplete { _, text ->
                PlayerData[this.uniqueId]?.persona?.joinMsg = text
                try {
                    DataRepository.savePlayerData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@onComplete AnvilGUI.Response.close()
            }
            .open(this)
    }
}

fun Player.editQuitMsg(plugin: Plugin) {
    PlayerData[this.uniqueId]?.persona?.let { persona ->
        val paper = ItemStack(Material.PAPER)
        paper.editMeta {
            it.displayName(Component.text(persona.quitMsg))
        }
        AnvilGUI.Builder()
            .plugin(plugin)
            .onClose { player ->
                player.sendMessage(Component.translatable("tellraw.settings.saved"))
                player.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f))
            }
            .title(plugin.config.getString("lang.edit_quit_msg"))
            .itemLeft(paper)
            .onComplete { _, text ->
                PlayerData[this.uniqueId]?.persona?.quitMsg = text
                try {
                    DataRepository.savePlayerData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@onComplete AnvilGUI.Response.close()
            }
            .open(this)
    }
}

fun Player.editTeamName(plugin: Plugin) {
    this.team?.let { team ->
        val oldTeamName = team.name
        val paper = ItemStack(Material.PAPER)
        paper.editMeta {
            it.displayName(Component.text(team.name))
        }

        AnvilGUI.Builder()
            .plugin(plugin)
            .onClose {
                if (oldTeamName == team.name) return@onClose
                team.bukkitPlayers().forEach { _ ->
                    sendMessage(
                        Component.translatable("tellraw.settings.changed_team_name")
                            .args(displayName(), Component.text(team.displayName())))
                    playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f))
                }
            }
            .title(plugin.config.getString("lang.edit_team_name"))
            .itemLeft(paper)
            .onComplete { _, text ->
                if (!text.matches(Regex.fromLiteral("^[a-zA-Z ]*$"))) {
                    sendMessage(Component.translatable("tellraw.settings.contains_illegal_chars"))
                    playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1f))
                    return@onComplete AnvilGUI.Response.close()
                }
                if (text.length < 3) {
                    sendMessage(Component.translatable("tellraw.settings.short_name"))
                    playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1f))
                }
                TeamData[team.id]?.name = text
                try {
                    DataRepository.saveTeams()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return@onComplete AnvilGUI.Response.close()
            }
            .open(this)
    }
}