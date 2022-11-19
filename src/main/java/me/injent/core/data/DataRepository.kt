package me.injent.core.data

import me.injent.core.Main
import me.injent.core.data.source.Files
import me.injent.core.data.source.LocalDataSource
import me.injent.core.utils.SECTION_TEAMS
import me.injent.core.utils.runTaskAsync
import org.bukkit.ChatColor

object DataRepository {

    /**
     * Loads teams data from config file of [Main] plugin
     */
    fun loadTeams() {
        runTaskAsync(Main.instance!!) {
            Main.instance?.config?.let {
                it.getConfigurationSection(SECTION_TEAMS)?.getKeys(false)?.forEach { team ->
                    val id = team.toInt()
                    val name = it.getString("$SECTION_TEAMS.$team.name")!!
                    val tag = it.getString("$SECTION_TEAMS.$team.tag")!![0]
                    val color = ChatColor.valueOf(it.getString("$SECTION_TEAMS.$team.color")!!.uppercase())
                    val players = it.getStringList("$SECTION_TEAMS.$team.players")
                    TeamData(id, name, tag, color, players)
                }
            }
        }
    }

    /**
     * Saves json file for open statistics
     */
    fun saveTeams() {
        runTaskAsync(Main.instance!!) {
            LocalDataSource.saveData(Files.Teams, TeamData.collection())
        }
    }

    /**
     * Loads players data from json file
     */
    fun loadPlayerData() {
        runTaskAsync(Main.instance!!) {
            LocalDataSource.getData<Array<PlayerData>>(Files.Players).forEach {
                PlayerData[it.uuid] = it
            }
        }
    }

    /**
     * Saves players data to json file
     */
    fun savePlayerData() {
        runTaskAsync(Main.instance!!) {
            LocalDataSource.saveData(Files.Players, PlayerData.collection())
        }
    }
}