package me.injent.core.data

import me.injent.core.utils.JOIN_ICON
import me.injent.core.utils.QUIT_ICON
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

data class PlayerData(
    val uuid: UUID,
    val name: String
) {
    var teamId: Int = -1
    var coins = 0.0
    var localCoins = 0.0
    val persona: Persona = Persona()

    companion object: Listener {
        private val data = HashMap<UUID, PlayerData>()

        operator fun get(uuid: UUID) = data[uuid]
        operator fun get(player: Player) = PlayerData[player.uniqueId]
        operator fun set(uuid: UUID, value: PlayerData) {
            data[uuid] = value
        }
        fun collection() = data.values

        @EventHandler
        private fun onJoin(event: PlayerJoinEvent) {
            val player = event.player
            if (PlayerData[player.uniqueId] == null) {
                PlayerData(player.uniqueId, player.name).apply {
                    teamId = player.team?.id ?: -1
                    TeamData[teamId]?.joinTeam(player)
                }
                DataRepository.savePlayerData()
            }
            PlayerData[player.uniqueId]?.let {
                val joinMsg = Component.text("[$JOIN_ICON] ")
                    .append(player.teamDisplayName())
                    .append(Component.text(" ${it.persona.joinMsg}"))
                event.joinMessage(joinMsg)
            }
        }

        @EventHandler
        private fun onQuit(event: PlayerQuitEvent) {
            PlayerData[event.player.uniqueId]?.let {
                val quitMsg = Component.text("[$QUIT_ICON] ")
                    .append(event.player.teamDisplayName())
                    .append(Component.text(" ${it.persona.quitMsg}"))
                event.quitMessage(quitMsg)
                TeamData[it.teamId]?.onQuitGame(event.player)
            }
        }
    }

    init {
        data[uuid] = this
    }
}

fun Player.defaultState() {
    isInvulnerable = false
    gameMode = GameMode.ADVENTURE
    inventory.clear()
    saturation = 20f
    health = 20.0
    foodLevel = 20
    activePotionEffects.clear()
    setStatistic(Statistic.PLAYER_KILLS, 0)
    isInvisible = false
    allowFlight = false
}

fun Player.spectatorState() {
    isInvulnerable = true
    health = 20.0
    saturation = 20f
    foodLevel = 20
    isCollidable = false
    isInvisible = true
    inventory.clear()
    gameMode = GameMode.ADVENTURE
    activePotionEffects.clear()
    allowFlight = true
}

var Player.coins
    get() = PlayerData[this.uniqueId]!!.coins
    set(value) {
        PlayerData[this.uniqueId]!!.coins = value
    }

var Player.localCoins
    get() = PlayerData[this.uniqueId]!!.localCoins
    set(value) {
        PlayerData[this.uniqueId]!!.localCoins = value
    }

val Player.team: TeamData?
    get() {
        for ((key, value) in TeamData.iterator()) {
            if (value.registeredPlayers.contains(this.name))
                return TeamData[key]
        }
        return null
    }
