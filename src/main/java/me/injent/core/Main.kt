package me.injent.core

import me.injent.core.data.DataRepository
import me.injent.core.data.PlayerData
import me.injent.core.data.listeners.MainListener
import me.injent.core.utils.GlobalOptions
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var globalOptions: GlobalOptions
    private set

class Main : JavaPlugin() {

    companion object {
        var instance: Main? = null
            private set
    }

    override fun onEnable() {
        instance = this
        globalOptions = GlobalOptions(config)
        Bukkit.getPluginManager().run {
            registerEvents(PlayerData, this@Main)
            registerEvents(MainListener(), this@Main)
        }
        DataRepository.run {
            loadTeams()
            loadPlayerData()
        }
        logger.info("Data successfully loaded!")
    }

    fun enablePlugin(pluginName: String) {
        val pluginFile = File("${server.pluginsFolder}/$pluginName.jar")
        if (!pluginFile.exists()) return
        File("$dataFolder/session.lock").apply {
            if (!exists())
                createNewFile()
            writeText(pluginName)
        }
        val plugin = pluginLoader.loadPlugin(pluginFile)
        server.pluginManager.enablePlugin(plugin)
    }
}