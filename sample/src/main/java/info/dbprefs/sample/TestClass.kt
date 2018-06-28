package info.dbprefs.sample

class TestClass(val memberA: String, val memberB: String) {

    override fun toString(): String {
        return memberA+" "+memberB
    }
}