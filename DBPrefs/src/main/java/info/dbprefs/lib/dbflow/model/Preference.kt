package info.dbprefs.lib.dbflow.model

import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel

import info.dbprefs.lib.dbflow.DBFlowDatabase
import java.util.*

@Table(database = DBFlowDatabase::class,
        indexGroups = arrayOf(IndexGroup(number = 1, name = "keyIndex"))
)
internal class Preference : BaseModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0 // package-private recommended, not required

    @Column
    @Unique
    @Index(indexGroups = intArrayOf(1))
    var key: String? = null

    @Column(defaultValue = "")
    var value: String? = null

    @Column
    var validTill: Long? = null

    @Column(name = "updated_at")
    var updatedAt: Calendar = Calendar.getInstance()

}