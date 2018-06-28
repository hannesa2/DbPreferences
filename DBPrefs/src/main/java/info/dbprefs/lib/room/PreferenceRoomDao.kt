package info.dbprefs.lib.room

import android.arch.persistence.room.*
import info.dbprefs.lib.room.entity.PreferenceRoom
import io.reactivex.Flowable

@Dao
interface PreferenceRoomDao {

    @get:Query("SELECT * FROM sharedpreference")
    val all: List<PreferenceRoom>

    @Query("SELECT * FROM sharedpreference WHERE keyOfValue = :key")
    fun getValueFlowable(key: String): Flowable<PreferenceRoom>

    @Query("SELECT * FROM sharedpreference WHERE keyOfValue = :key")
    fun getValue(key: String): PreferenceRoom

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg arrayOfPreferenceRooms: PreferenceRoom): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preferenceRoom: PreferenceRoom): Long

    @Update
    fun updateAll(vararg arrayOfPreferenceRooms: PreferenceRoom)

    @Delete
    fun delete(preferenceRoom: PreferenceRoom)

    @Query("SELECT COUNT(keyOfValue) FROM sharedpreference WHERE keyOfValue = :key")
    fun countKey(key: String): Int

    @Query("DELETE FROM sharedpreference WHERE keyOfValue = :key")
    fun deleteByKey(key: String)

    @Transaction
    @Query("DELETE FROM sharedpreference")
    fun deleteAll()

}
