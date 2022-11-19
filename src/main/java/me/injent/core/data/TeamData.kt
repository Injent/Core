package me.injent.core.data

import com.google.gson.annotations.Expose
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.UUID

data class TeamData(
    val id: Int,
    var name: String,
    val tag: Char,
    val color: ChatColor = ChatColor.RESET,
    val registeredPlayers: List<String> = emptyList()
) {

    @Expose
    val players: List<UUID> = mutableListOf()
    @Expose
    val alivePlayers: List<UUID> = mutableListOf()
    var coins: Double = 0.0
    @Expose
    val bukkitTeam: Team
    val alive: Boolean
        get() = alivePlayers.isNotEmpty()
    val displayName: String
        get() = "$tag $color$name"

    companion object {
        private val data = HashMap<Int, TeamData>()

        operator fun get(id: Int) = data[id]
        operator fun iterator() = data.iterator()
        fun collection() = data.values
    }

    init {
        bukkitTeam = loadBukkitTeam()
        data[id] = this
    }

    fun joinTeam(player: Player) {
        players + player
        val customName = Component.text("$color$tag ${player.name}")
        player.displayName(customName)
        player.customName(customName)
        player.playerListName(customName)
        bukkitTeam.entries.plus(player.name)
    }

    fun leaveTeam(player: Player) {
        players - player.uniqueId
        alivePlayers - player.uniqueId
        bukkitTeam.entries.minus(player.name)
    }

    fun onQuitGame(player: Player) {
        players - player.uniqueId
        alivePlayers - player.uniqueId
    }

    fun bukkitPlayers(): List<Player> {
        val players: List<Player> = mutableListOf()
        for (uuid in this.players) {
            Bukkit.getPlayer(uuid)?.let {
                players + it
            }
        }
        return players
    }

    fun alivePlayers(): List<Player> {
        val alive: List<Player> = mutableListOf()
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.let {
                alive + it
            }
        }
        return alive
    }

    private fun loadBukkitTeam(): Team {
        var bukkitTeam = Bukkit.getScoreboardManager().mainScoreboard.getTeam(name)
        bukkitTeam?.unregister()
        bukkitTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(name)
        bukkitTeam.run {
            prefix(Component.text("${ChatColor.WHITE}$tag ${ChatColor.RESET}"))
            color(NamedTextColor.NAMES.value(this@TeamData.color.name.lowercase()))
            setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS)
            setAllowFriendlyFire(false)
        }
        registeredPlayers.forEach {
            bukkitTeam.addEntry(it)
        }
        return bukkitTeam
    }
}