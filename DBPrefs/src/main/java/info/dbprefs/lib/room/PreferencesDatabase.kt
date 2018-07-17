package info.dbprefs.lib.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import info.dbprefs.lib.room.entity.PreferenceRoom

@Database(entities = arrayOf(PreferenceRoom::class), version = 1)
abstract//@TypeConverters({DateConverter.class})
class PreferencesDatabase : RoomDatabase() {

    abstract fun preferenceDao(): PreferenceRoomDao

    companion object {

        val ROOM_DATABASE_NAME = "dbPrefs.db"
    }
}
