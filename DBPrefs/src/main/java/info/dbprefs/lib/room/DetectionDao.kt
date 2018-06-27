package info.audio.analysis.room

import android.arch.persistence.room.*
import info.audio.analysis.room.entity.Detection
import io.reactivex.Flowable

@Dao
interface DetectionDao {

    @get:Query("SELECT * FROM Detection")
    val all: List<Detection>

    @Query("SELECT * FROM Detection WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Detection>

    @get:Query("SELECT * FROM Detection WHERE timeFrom > 0 AND timeTo = 0 ORDER By timeFrom desc LIMIT 1")
    val currentDetection: Flowable<Detection>

    @get:Query("SELECT * FROM Detection WHERE timeFrom > 0 AND timeTo > 0")
    val allFinishedDetections: Flowable<List<Detection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg detections: Detection): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(detection: Detection): Long

    @Update
    fun updateAll(vararg detections: Detection)

    @Delete
    fun delete(detection: Detection)

}
