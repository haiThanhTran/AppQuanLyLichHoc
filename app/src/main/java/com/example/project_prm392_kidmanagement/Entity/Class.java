package com.example.project_prm392_kidmanagement.Entity;

public class Class {
    private String classId;
    private String className;
    private String schoolYear;
    private Teacher teacherId; // Giáo viên chủ nhiệm
    // Bỏ trường scheduleId ở đây

    public Class() {
    }

    // Sửa lại constructor
    public Class(String classId, String className, String schoolYear, Teacher teacherId) {
        this.classId = classId;
        this.className = className;
        this.schoolYear = schoolYear;
        this.teacherId = teacherId;
    }

    // Getters & Setters giữ nguyên (trừ của scheduleId)
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Teacher getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Teacher teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Class{" +
                "classId='" + classId + '\'' +
                ", className='" + className + '\'' +
                ", schoolYear='" + schoolYear + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}