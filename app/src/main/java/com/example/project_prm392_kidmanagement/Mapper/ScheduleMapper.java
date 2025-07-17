package com.example.project_prm392_kidmanagement.Mapper;

import android.content.Context;
import android.database.Cursor;

import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.Entity.Schedule;
import com.example.project_prm392_kidmanagement.Entity.Teacher;

public class ScheduleMapper {
    public static Schedule fromCursor(Cursor cursor, Context context) {
        int scheduleId = cursor.getInt(cursor.getColumnIndexOrThrow("scheduleId"));
        String classId = cursor.getString(cursor.getColumnIndexOrThrow("classId"));
        String teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId"));
        String activityName = cursor.getString(cursor.getColumnIndexOrThrow("activityName"));
        String activityDate = cursor.getString(cursor.getColumnIndexOrThrow("activityDate"));
        int shift = cursor.getInt(cursor.getColumnIndexOrThrow("shift"));

        // Lấy đối tượng Class và Teacher đầy đủ từ ID
        ClassDao classDao = new ClassDao(context);
        Class classroom = classDao.getById(classId);

        TeacherDao teacherDao = new TeacherDao(context);
        Teacher teacher = teacherDao.getById(teacherId);

        return new Schedule(scheduleId, classroom, teacher, activityName, activityDate, shift);
    }
}