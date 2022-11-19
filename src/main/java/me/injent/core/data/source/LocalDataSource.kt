package me.injent.core.data.source

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import me.injent.core.Main
import me.injent.core.data.PlayerData
import me.injent.core.data.TeamData
import java.io.*
import java.lang.reflect.Type

object LocalDataSource {

    fun saveData(fileType: Files, data: Any) {
        val writer = FileWriter(File("${Main.instance!!.dataFolder.absolutePath}/${fileType.path}.json"), false)
        Gson().toJson(data, writer)
        writer.flush()
        writer.close()
    }

    fun <T> getData(fileType: Files): T {
        File("${Main.instance!!.dataFolder.absolutePath}/${fileType.path}.json").let {
            if (!it.exists()) {
                it.createNewFile()
            }
            val reader = JsonReader(FileReader(it))
            val data = Gson().fromJson<T>(reader, fileType.typeOfT)
            reader.close()
            return data
        }
    }
}

sealed class Files(
    val name: String,
    val path: String,
    val typeOfT: Type
) {
    object Players : Files(
        name = "players",
        path = "players",
        typeOfT = Array<PlayerData>::class.java
    )

    object Teams : Files(
        name = "teams",
        path = "teams",
        typeOfT = Array<TeamData>::class.java
    )
}