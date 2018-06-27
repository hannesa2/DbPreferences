package info.dbprefs.lib.dbflow

import com.raizlabs.android.dbflow.converter.TypeConverter
import java.util.*
import com.raizlabs.android.dbflow.annotation.TypeConverter as TypeConverterAnnotation

@TypeConverterAnnotation
internal class CalendarConverter : TypeConverter<Long, Calendar>() {

    override fun getDBValue(model: Calendar): Long? {
        return model.timeInMillis
    }

    override fun getModelValue(data: Long?): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = data!!
        return calendar
    }

}