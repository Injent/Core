package me.injent.core.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

fun runTask(plugin: Plugin, toRun: BukkitRunnable.() -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            this.toRun()
        }
    }.runTask(plugin)
}

fun runTaskSync(plugin: Plugin, toRun: Runnable) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) { toRun.run() }
}

/**
 * Run a task after [delay] of ticks
 */
fun runTaskLater(plugin: Plugin, delay: Long, toRun: BukkitRunnable.() -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            this.toRun()
        }
    }.runTaskLater(plugin, delay)
}

/**
 * Repeat a task every [repeat] ticks after [delay] ticks
 */
fun runTaskTimer(plugin: Plugin, delay: Long, repeat: Long, toRun: BukkitRunnable.() -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            this.toRun()
        }
    }.runTaskTimer(plugin, delay, repeat)
}

/**
 * Run a task on the async thread
 */
fun runTaskAsync(plugin: Plugin, toRun: BukkitRunnable.() -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            this.toRun()
        }
    }.runTaskAsynchronously(plugin)
}

/**
 * Run a task after [delay] ticks on the async thread
 */
fun runTaskLaterAsync(plugin: Plugin, delay: Long, toRun: BukkitRunnable.() -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            this.toRun()
        }
    }.runTaskLaterAsynchronously(plugin, delay)
}

/**
 * Repeat a task every [repeat] ticks after [delay] ticks on the async thread
 */
fun runTaskTimerAsync(plugin: Plugin, delay: Long, repeat: Long, toRun: BukkitRunnable.() -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            this.toRun()
        }
    }.runTaskTimerAsynchronously(plugin, delay, repeat)
}

fun runTimer(plugin: Plugin, sec: Int, min: Int, onEnd: () -> Unit, onTimeUpdate: (sec: Int, min: Int) -> Unit) {
    var s = sec + 1
    var m = min
    var timerTaskId = 0
    val cancel = { Bukkit.getScheduler().cancelTask(timerTaskId) }
    timerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
        plugin,
        {
            s--
            if (s < 0 && m >= 1) {
                m--
                s = 59
            }
            if (s == 0 && m == 0) {
                onEnd()
                cancel()
                return@scheduleSyncRepeatingTask
            }
            onTimeUpdate(sec, min)
        },
        0L,
        1000L
    )
}