package com.example.project_prm392_kidmanagement.Mapper;

import android.content.Context;
import android.database.Cursor;
import com.example.project_prm392_kidmanagement.DAO.ParentDao;
import com.example.project_prm392_kidmanagement.DB.SqlDatabaseHelper;
import com.example.project_prm392_kidmanagement.Entity.Student;

public class StudentMapper {
    public static Student fromCursor(Cursor cursor, Context context) {
        Student student = new Student();
        student.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(SqlDatabaseHelper.COLUMN_STUDENT_ID)));
        student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(SqlDatabaseHelper.COLUMN_STUDENT_NAME)));
        student.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(SqlDatabaseHelper.COLUMN_STUDENT_ADDRESS)));
        student.setDob(cursor.getString(cursor.getColumnIndexOrThrow(SqlDatabaseHelper.COLUMN_STUDENT_DOB)));

        // Lấy thông tin Parent
        String parentId = cursor.getString(cursor.getColumnIndexOrThrow(SqlDatabaseHelper.COLUMN_PARENT_ID));
        if (parentId != null) {
            ParentDao parentDao = new ParentDao(context);
            student.setParentId(parentDao.getById(parentId));
        }

        // KHÔNG ĐỌC CỘT classId NỮA
        // String classId = cursor.getString(cursor.getColumnIndexOrThrow(SqlDatabaseHelper.COLUMN_CLASS_ID)); // <-- XÓA DÒNG NÀY
        // ...

        return student;
    }
}