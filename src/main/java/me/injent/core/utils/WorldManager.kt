package me.injent.core.utils

import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.function.pattern.RandomPattern
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.world.block.BlockState
import me.injent.core.Main
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import org.bukkit.util.FileUtil
import java.io.File
import java.io.IOException
import kotlin.math.cos
import kotlin.math.sin

class WorldManager(
    worldFolder: File,
    path: String,
    private val onLoadInitiated: () -> Unit
) {
    private val sourceWorldFolder: File
    private var activeWorldFolder: File? = null
    private var world: World? = null

    init {
        sourceWorldFolder = File(worldFolder, path)
    }

    fun createBorder(center: Location, radius: Float, particlesCount: Int, color: Color?) {
        for (d in 0..particlesCount) {
            val particleLoc = center.clone()
            particleLoc.x = center.x + cos(d.toDouble()) * radius
            particleLoc.z = center.z + sin(d.toDouble()) * radius
            center.world.spawnParticle(
                Particle.REDSTONE,
                particleLoc,
                1,
                0.0, 0.0, 0.0,
                0.0,
                DustOptions(color!!, 1f), true
            )
        }
    }

    fun setGlowing(player: Player, receiver: Player?, glow: Boolean) {
//        val protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()
//        val packet: PacketContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA)
//        packet.getIntegers().write(0, player.entityId) //Set packet's entity id
//        val watcher = WrappedDataWatcher() //Create data watcher, the Entity Metadata packet requires this
//        val serializer: WrappedDataWatcher.Serializer =
//            WrappedDataWatcher.Registry.get(Byte::class.java) //Found this through Google, needed for some stupid reason
//        watcher.setEntity(player) //Set the new data watcher's target
//        watcher.setObject(0, 0x40.toByte()) //Set status to glowing, found on protocol page
//        packet.getWatchableCollectionModifier()
//            .write(0, watcher.getWatchableObjects()) //Make the packet's datawatcher the one we created
//        try {
//            protocolManager.sendServerPacket(receiver, packet)
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//        }
    }

    fun fillBlocks(pos1: Location, pos2: Location, material: Material) {
        val world: com.sk89q.worldedit.world.World = BukkitAdapter.adapt(pos1.world)
        val selection =
            CuboidRegion(world, BlockVector3.at(pos1.x, pos1.y, pos1.z), BlockVector3.at(pos2.x, pos2.y, pos2.z))
        try {
            WorldEdit.getInstance().newEditSession(world).use { editSession ->
                val pattern = RandomPattern()
                val air: BlockState = BukkitAdapter.adapt(material.createBlockData())
                pattern.add(air, 1.0)
                editSession.setBlocks(selection, pattern)
            }
        } catch (e: MaxChangedBlocksException) {
            e.printStackTrace()
        }
    }

    fun load(): Boolean {
        if (isLoaded()) return true
        activeWorldFolder = File(
            Bukkit.getWorldContainer().parentFile,
            sourceWorldFolder.name + "_active_" + System.currentTimeMillis()
        )
        runTaskAsync(Main.instance!!) {
            try {
                FileUtil.copy(sourceWorldFolder, activeWorldFolder!!)
                runTaskSync(Main.instance!!) {
                    onLoadInitiated()
                }
            } catch (e: IOException) {
                Bukkit.getLogger().severe("Failed to load LevelMap from source folder " + sourceWorldFolder.name)
                e.printStackTrace()
            }
        }
        return isLoaded()
    }

    fun unload() {
        if (world != null) Bukkit.unloadWorld(world!!, false)
        activeWorldFolder?.let {
            runTaskAsync(Main.instance!!) {
                it.delete()
                world = null
                activeWorldFolder = null
            }
        }
    }

    fun restoreFromSource(): Boolean {
        unload()
        return load()
    }

    fun isLoaded(): Boolean {
        return getWorld() != null
    }

    fun getWorld(): World? {
        return world
    }
}