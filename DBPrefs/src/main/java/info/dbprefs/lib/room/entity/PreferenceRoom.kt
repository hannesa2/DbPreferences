package info.dbprefs.lib.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sharedpreference", indices = arrayOf(Index(value = arrayOf("keyOfValue"), unique = true)))
data class PreferenceRoom(@PrimaryKey
                          @ColumnInfo(name = "keyOfValue") var key: String = "",
                          @ColumnInfo(name = "value") var value: String = "",
                          @ColumnInfo(name = "validTill") var validTill: Long = Long.MAX_VALUE,
                          @ColumnInfo(name = "changed") var changed: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", Long.MAX_VALUE, System.currentTimeMillis())
}

