package com.example.project_prm392_kidmanagement.Mapper;

import android.content.Context;
import android.database.Cursor;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.Entity.Teacher;

public class ClassMapper {
    public static Class fromCursor(Cursor cursor, Context context) {
        String classId = cursor.getString(cursor.getColumnIndexOrThrow("classId"));
        String className = cursor.getString(cursor.getColumnIndexOrThrow("className"));
        String schoolYear = cursor.getString(cursor.getColumnIndexOrThrow("schoolYear"));
        String teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId"));
        int isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow("isDeleted"));


        TeacherDao teacherDao = new TeacherDao(context);
        Teacher teacher = teacherDao.getById(teacherId);

        // Bỏ schedule đi
        return new Class(classId, isDeleted ,teacher, schoolYear, className);
    }
}