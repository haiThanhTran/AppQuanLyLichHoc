package com.example.project_prm392_kidmanagement.Entity;

public class Student {
    private String studentId;
    private Parent parentId;
    private String fullName;
    private String address;
    private String dob;
    // private Class classId; // <-- XÓA DÒNG NÀY

    public Student() {
    }

    // Sửa lại constructor
    public Student(String studentId, Parent parentId, String fullName, String address, String dob) {
        this.studentId = studentId;
        this.parentId = parentId;
        this.fullName = fullName;
        this.address = address;
        this.dob = dob;
    }

    // Getters và Setters
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public Parent getParentId() {
        return parentId;
    }
    public void setParentId(Parent parentId) {
        this.parentId = parentId;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }

    // Xóa getter và setter của classId
}