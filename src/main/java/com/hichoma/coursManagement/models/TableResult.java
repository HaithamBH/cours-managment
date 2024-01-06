package com.hichoma.coursManagement.models;

public class TableResult {
    private String ID;
    private String ClassName;
    private String MatterName;
    private String Day;
    private String Hour;
    private String Teacher;
    private String Contact;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String aClass) {
        ClassName = aClass;
    }

    public String getMatterName() {
        return MatterName;
    }

    public void setMatterName(String matter) {
        MatterName = matter;
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

    public String getTeacher() {
        return Teacher;
    }

    public void setTeacher(String teacher) {
        Teacher = teacher;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }
}

