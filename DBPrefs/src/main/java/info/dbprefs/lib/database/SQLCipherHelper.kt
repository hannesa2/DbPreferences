package info.dbprefs.lib.database

import com.raizlabs.android.dbflow.config.DatabaseDefinition
import com.raizlabs.android.dbflow.sqlcipher.SQLCipherOpenHelper
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener

internal class SQLCipherHelper(databaseDefinition: DatabaseDefinition, listener: DatabaseHelperListener?, private val password: String) : SQLCipherOpenHelper(databaseDefinition, listener) {

    override fun getCipherSecret(): String = password
}
