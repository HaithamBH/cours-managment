package com.hichoma.coursManagement.models;

public class Session {
    private String Class;
    private String MatterName;
    private String Day;
    private String Hour;
    private String TeacherID;

    public String getClassName() {
        return Class;
    }

    public void setClassName(String aClass) {
        Class = aClass;
    }

    public String getMatterName() {
        return MatterName;
    }

    public void setMatterName(String name) {
        MatterName = name;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public String getTeacherID() {
        return TeacherID;
    }

    public void setTeacherID(String teacherID) {
        TeacherID = teacherID;
    }
}
