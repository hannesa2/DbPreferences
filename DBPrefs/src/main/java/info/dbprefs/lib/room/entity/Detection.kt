package info.audio.analysis.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class Detection(@PrimaryKey(autoGenerate = true) var id: Long?,
                     @ColumnInfo(name = "timeFrom") var timeFrom: Long = 0,
                     @ColumnInfo(name = "timeTo") var timeTo: Long = 0,
                     @ColumnInfo(name = "sent") var sent: Boolean = false,
                     @ColumnInfo(name = "androidid") var androidid: String = "",
                     @Ignore @ColumnInfo(name = "deltaTime") var deltaTime: Long,
                     @Ignore @ColumnInfo(name = "open") var open: Boolean = timeTo.equals(0)

) {
    constructor() : this(null, 0, 0, false, "", 0)
}

