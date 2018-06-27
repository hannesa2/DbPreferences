package info.audio.analysis.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import info.audio.analysis.room.entity.Detection

@Database(entities = arrayOf(Detection::class), version = 1)
abstract//@TypeConverters({DateConverter.class, TaskStatusConverter.class})
class AppDatabase : RoomDatabase() {

    abstract fun detectionDao(): DetectionDao

    companion object {

        val ROOM_DATABASE_NAME = "app.db"
    }
}
