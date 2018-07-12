package info.dbprefs.androidtests.typeadapters;

import java.util.Objects;

//taken from https://www.tutorialspoint.com/gson/gson_custom_adapters.htm
public class Student {
    private int rollNo;
    private String name;

    public int getRollNo() {
        return rollNo;
    }

    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Student[ name = " + name + ", roll no: " + rollNo + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        Student student = (Student) o;
        return rollNo == student.rollNo &&
                Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rollNo, name);
    }
}