package info.dbprefs.lib.room

import androidx.room.Database
import androidx.room.RoomDatabase

import info.dbprefs.lib.room.entity.PreferenceRoom

@Database(entities = [PreferenceRoom::class], version = 1)
abstract class PreferencesDatabase : RoomDatabase() {

    abstract fun preferenceDao(): PreferenceRoomDao

    companion object {

        const val ROOM_DATABASE_NAME = "dbPrefs.db"
    }
}
