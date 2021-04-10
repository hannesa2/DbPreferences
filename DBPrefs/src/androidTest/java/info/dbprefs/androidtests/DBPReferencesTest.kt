package info.dbprefs.androidtests

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import info.dbprefs.androidtests.data.TestClass
import info.dbprefs.androidtests.data.TestConfigKeys
import info.dbprefs.androidtests.typeadapters.Student
import info.dbprefs.androidtests.typeadapters.StudentAdapter
import info.dbprefs.lib.DbPreferences
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DBPReferencesTest {

    private lateinit var dbPrefs: DbPreferences

    @Before
    fun setup() {
        DbPreferences.init(InstrumentationRegistry.getInstrumentation().context, "secret")
        dbPrefs = DbPreferences()
        dbPrefs.clearAll()
    }

    @After
    fun tearDown() {
        dbPrefs.close()
    }

    @Test
    fun testClearAllExcept() {
        add3Items()
        assertEquals(3, dbPrefs.count())
        dbPrefs.clearAllExcept(TestConfigKeys.KEY_STRING, TestConfigKeys.KEY_OBJECT)
        assertEquals(2, dbPrefs.count())

        add3Items()
        dbPrefs.clearAllExcept(TestConfigKeys.KEY_OBJECT)
        assertEquals(1, dbPrefs.count())
    }

    private fun add3Items() {
        dbPrefs.clearAll()
        dbPrefs.put(TestConfigKeys.KEY_STRING, "AA")
        dbPrefs.put(TestConfigKeys.KEY_INTEGER, 9)
        val testClass = TestClass("cc", "dd")
        dbPrefs.put(TestConfigKeys.KEY_OBJECT, testClass)
    }

    @Test
    fun testCustomGson() {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(Student::class.java, StudentAdapter())
        val gson = builder.create()

        dbPrefs = DbPreferences(gson)

        val jsonString = "{\"name\":\"BugsBunny\",\"rollNo\":1}"

        val student = gson.fromJson(jsonString, Student::class.java)
        dbPrefs.put("student", student)

        // read
        val studentRead: Student? = dbPrefs.get("student", Student::class.java)

        assertEquals(student, studentRead)

        assertEquals(jsonString, gson.toJson(studentRead))
    }

    @Test
    fun testString() {
        var value: String? = dbPrefs.get(TestConfigKeys.KEY_STRING, String::class.java)
        Assert.assertNull(value)

        // check for default value
        value = dbPrefs.get(TestConfigKeys.KEY_STRING, String::class.java, "BB")
        assertEquals(value, "BB")

        // read
        dbPrefs.put(TestConfigKeys.KEY_STRING, "AA")
        value = dbPrefs.get(TestConfigKeys.KEY_STRING, String::class.java)
        assertEquals(value, "AA")
    }

    @Test
    fun testDefaultValueReturned() {
        val value: String? = dbPrefs.get("notexistingString", String::class.java, "defaultValue")
        assertEquals("defaultValue", value)
    }

    @Test
    fun testInteger() {
        var value: Int? = dbPrefs.get(TestConfigKeys.KEY_INTEGER, Integer::class.java)
        Assert.assertNull(value)

        // check for default value
        value = dbPrefs.get(TestConfigKeys.KEY_INTEGER, Integer::class.java, 7)
        assertEquals(value, 7)

        // read
        dbPrefs.put(TestConfigKeys.KEY_INTEGER, 9)
        value = dbPrefs.get(TestConfigKeys.KEY_INTEGER, Integer::class.java)
        assertEquals(value, 9)
    }

    @Test
    fun testClass() {
        var testClass = TestClass("aa", "bb")

        var value: TestClass? = dbPrefs.get(TestConfigKeys.KEY_OBJECT, TestClass::class.java)
        Assert.assertNull(value)

        // check for default value
        value = dbPrefs.get(TestConfigKeys.KEY_OBJECT, TestClass::class.java, testClass)
        assertEquals(value, testClass)

        // read
        testClass = TestClass("cc", "dd")
        dbPrefs.put(TestConfigKeys.KEY_OBJECT, testClass)
        value = dbPrefs.get(TestConfigKeys.KEY_OBJECT, TestClass::class.java)
        assertEquals(value.toString(), testClass.toString())
    }

    @Test
    fun testList() {
        val listSource: ArrayList<TestClass> = ArrayList()
        listSource.add(TestClass("A", "aa"))
        listSource.add(TestClass("B", "bb"))

        val listType = object : TypeToken<ArrayList<TestClass>>() {
        }.type

        var value: ArrayList<TestClass>? = dbPrefs.get(TestConfigKeys.KEY_LIST, listType)
        Assert.assertNull(value)

        // check for default value
        value = dbPrefs.get(TestConfigKeys.KEY_LIST, listType, listSource)
        assertEquals(2, value.size)

        // read
        dbPrefs.put(TestConfigKeys.KEY_LIST, listSource)
        value = dbPrefs.get(TestConfigKeys.KEY_LIST, listType)
        assertEquals(2, value?.size)
    }

    @Test
    fun testContainRemove() {
        val testClass = TestClass("aa", "bb")
        val listSource: ArrayList<TestClass> = ArrayList()
        listSource.add(TestClass("A", "aa"))
        listSource.add(TestClass("B", "bb"))

        dbPrefs.clearAll()
        // all must be deleted
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_LIST))
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_STRING))
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_INTEGER))
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_OBJECT))

        dbPrefs.put(TestConfigKeys.KEY_OBJECT, testClass)
        dbPrefs.put(TestConfigKeys.KEY_INTEGER, 9)
        dbPrefs.put(TestConfigKeys.KEY_STRING, "aa")
        dbPrefs.put(TestConfigKeys.KEY_LIST, listSource)

        assertEquals(true, dbPrefs.contains(TestConfigKeys.KEY_LIST))
        assertEquals(true, dbPrefs.contains(TestConfigKeys.KEY_STRING))
        assertEquals(true, dbPrefs.contains(TestConfigKeys.KEY_INTEGER))
        assertEquals(true, dbPrefs.contains(TestConfigKeys.KEY_OBJECT))

        dbPrefs.remove(TestConfigKeys.KEY_OBJECT)
        dbPrefs.remove(TestConfigKeys.KEY_INTEGER)
        dbPrefs.remove(TestConfigKeys.KEY_STRING)
        dbPrefs.remove(TestConfigKeys.KEY_LIST)

        // all must be deleted
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_LIST))
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_STRING))
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_INTEGER))
        assertEquals(false, dbPrefs.contains(TestConfigKeys.KEY_OBJECT))
    }

    @Suppress("UNUSED_VARIABLE")
    @Test
    @Ignore("It's not stable")
    fun testSpeed() {

        var start = System.currentTimeMillis()
        for (item: Int in 1..2000) {
            dbPrefs.put(TestConfigKeys.KEY_STRING, "value$item")
        }
        Assert.assertTrue((System.currentTimeMillis() - start) < 20000)  // ARM needs more time, x86 works with 9000

        start = System.currentTimeMillis()
        for (item: Int in 1..2000) {
            var value: String? = dbPrefs.get(TestConfigKeys.KEY_STRING, String::class.java)
        }
        Assert.assertTrue((System.currentTimeMillis() - start) < 10000)  // ARM needs more time, x86 works with 2000
    }
}