package info.dbprefs.lib.dbflow.model

import com.raizlabs.android.dbflow.sql.language.Select

internal object PreferenceRepository {

    fun getAll(): MutableList<Preference> {
        return Select()
                .from(Preference::class.java)
                .where()
                .orderBy(Preference_Table.updated_at, false)
                .queryList()
    }

}