package me.injent.core.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.configuration.Configuration

class GlobalOptions(
    config: Configuration
) {
    val gameState: GameState = GameState.Lobby
    val eventName: String?
    val gamesPlayed = 0
    val chatWidth = 230
    val modifier = 0.0

    init {
        val options = config.getConfigurationSection("options")
            ?: throw NoSuchElementException("Section options doesn't exist")
        eventName = options.getString("event_name")
    }
}

sealed class GameState(
    val sound: Sound
) {
    object Lobby : GameState(
        sound = Sound.sound(Key.key("contest:ui/lobby"), Sound.Source.MASTER, 1f, 1f)
    )
}