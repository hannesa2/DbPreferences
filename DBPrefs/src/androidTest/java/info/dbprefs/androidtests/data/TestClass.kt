package info.dbprefs.androidtests.data

class TestClass(val memberA: String, val memberB: String) {

    override fun toString(): String {
        return memberA+" "+memberB
    }
}