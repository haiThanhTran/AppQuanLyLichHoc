package com.example.project_prm392_kidmanagement.Entity;

public class Shift {
    private int shiftNumber; // 1, 2, 3, 4
    private String shiftName; // "Ca 1: 07:30 - 09:00"
    private Schedule schedule; // Tiết học hiện tại của ca này, có thể null

    public Shift(int shiftNumber, String shiftName) {
        this.shiftNumber = shiftNumber;
        this.shiftName = shiftName;
        this.schedule = null; // Khởi tạo là chưa có tiết học
    }

    // Getters and Setters
    public int getShiftNumber() {
        return shiftNumber;
    }

    public void setShiftNumber(int shiftNumber) {
        this.shiftNumber = shiftNumber;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}