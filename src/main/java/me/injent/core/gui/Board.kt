package me.injent.core.gui

import fr.mrmicky.fastboard.FastBoard
import me.injent.core.Main
import me.injent.core.data.*
import me.injent.core.globalOptions
import me.injent.core.utils.runTaskAsync
import org.bukkit.Statistic
import org.bukkit.entity.Player
import java.util.*


class Board() {

    private val name = ""
    private val lines = mutableMapOf<Int, String>()
    private val placeholders = mutableMapOf<String, String>()
    private val boards = mutableMapOf<UUID, FastBoard>()

    fun setLines(lines: List<String>) {
        this.lines.clear()
        for ((index, line) in lines.withIndex()) {
            this.lines[index] = line
        }
        updateAll()
    }

    fun update(player: Player) {
        if (!boards.containsKey(player.uniqueId))
            boards[player.uniqueId] = FastBoard(player)
        val l = mutableListOf<String>()
        lines.keys.forEach { key: Int ->
            l + replace(player, key)
        }
        boards[player.uniqueId]!!.updateLines(l)
    }

    fun updateAll() {
        runTaskAsync(Main.instance!!) {
            for (fastboard in boards.values) {
                fastboard.updateTitle(name)
                val list = mutableListOf<String>()
                lines.keys.forEach { key ->
                    list + replace(fastboard.player, key)
                }
                fastboard.updateLines(list)
            }
        }
    }

    private fun replace(player: Player, key: Int): String {
        val str = lines[key]!!
            .replace("{coins}", player.coins.toString())
            .replace("{local_coins}", player.localCoins.toString())
            .replace("{kills}", player.getStatistic(Statistic.PLAYER_KILLS).toString())
            .replace("{team_coins}", player.team?.coins?.toString() ?: "0")
            .replace("{games_played}", globalOptions.gamesPlayed.toString())
        val sb = StringBuilder()
        val strArray = str.toCharArray()
        var i = 0
        while (i < strArray.size - 1) {
            if (strArray[i] == '{') {
                i += 1
                val begin = i
                while (strArray[i] != '}') ++i
                sb.append(placeholders.getOrDefault(str.substring(begin, i++), "null"))
            } else {
                sb.append(strArray[i])
                ++i
            }
        }
        if (i < strArray.size) sb.append(strArray[i])
        return sb.toString()
    }

    fun removeBoard(player: Player) {
        boards[player.uniqueId]?.delete()
        boards - player.uniqueId
    }
}