package info.dbprefs.androidtests.data

class TestClass(private val memberA: String, private val memberB: String) {

    override fun toString(): String {
        return "$memberA $memberB"
    }
}