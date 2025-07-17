package com.example.project_prm392_kidmanagement.Entity;

public class Schedule {
    private int scheduleId;      // Đổi thành int để tự tăng
    private Class classId;       // Lớp học của tiết này
    private Teacher teacherId;   // Giáo viên dạy tiết này
    private String activityName; // Tên hoạt động
    private String activityDate; // Ngày diễn ra (ví dụ: "2025-07-18")
    private int shift;           // Ca học (1, 2, 3, 4)

    public Schedule() {
    }

    // Constructor đầy đủ để dễ sử dụng
    public Schedule(int scheduleId, Class classId, Teacher teacherId, String activityName, String activityDate, int shift) {
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.teacherId = teacherId;
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.shift = shift;
    }

    // Getters và Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Class getClassId() {
        return classId;
    }

    public void setClassId(Class classId) {
        this.classId = classId;
    }

    public Teacher getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Teacher teacherId) {
        this.teacherId = teacherId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }
}