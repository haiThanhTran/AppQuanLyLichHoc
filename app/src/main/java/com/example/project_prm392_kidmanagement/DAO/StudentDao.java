package com.example.project_prm392_kidmanagement.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project_prm392_kidmanagement.DB.SqlDatabaseHelper;
import com.example.project_prm392_kidmanagement.Entity.Student;
import com.example.project_prm392_kidmanagement.Mapper.StudentMapper;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private final SqlDatabaseHelper dbHelper;
    private final Context context;

    public StudentDao(Context context) {
        this.context = context;
        this.dbHelper = new SqlDatabaseHelper(context);
    }

    public long insert(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_ID, student.getStudentId());
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_NAME, student.getFullName());
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_ADDRESS, student.getAddress());
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_DOB, student.getDob());
        values.put(SqlDatabaseHelper.COLUMN_PARENT_ID, student.getParentId() != null ? student.getParentId().getParentId() : null);
        // values.put("classId", ...); // <-- XÓA DÒNG NÀY

        long result = db.insert(SqlDatabaseHelper.TABLE_STUDENT, null, values);
        db.close();
        return result;
    }

    public boolean update(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_NAME, student.getFullName());
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_ADDRESS, student.getAddress());
        values.put(SqlDatabaseHelper.COLUMN_STUDENT_DOB, student.getDob());
        values.put(SqlDatabaseHelper.COLUMN_PARENT_ID, student.getParentId() != null ? student.getParentId().getParentId() : null);
        // values.put("classId", ...); // <-- XÓA DÒNG NÀY

        int rowsAffected = db.update(
                SqlDatabaseHelper.TABLE_STUDENT,
                values,
                SqlDatabaseHelper.COLUMN_STUDENT_ID + " = ?",
                new String[]{student.getStudentId()}
        );
        db.close();
        return rowsAffected > 0;
    }

    public boolean delete(String studentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Cần xóa cả liên kết trong bảng studentToClass trước khi xóa student
        db.delete(SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS, SqlDatabaseHelper.COLUMN_STUDENT_ID + " = ?", new String[]{studentId});

        int rows = db.delete(
                SqlDatabaseHelper.TABLE_STUDENT,
                SqlDatabaseHelper.COLUMN_STUDENT_ID + " = ?",
                new String[]{studentId}
        );
        db.close();
        return rows > 0;
    }

    public Student getById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Student student = null;
        try {
            cursor = db.query(
                    SqlDatabaseHelper.TABLE_STUDENT,
                    null,
                    SqlDatabaseHelper.COLUMN_STUDENT_ID + " = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                student = StudentMapper.fromCursor(cursor, context);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return student;
    }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    SqlDatabaseHelper.TABLE_STUDENT,
                    null,
                    null,
                    null,
                    null,
                    null,
                    SqlDatabaseHelper.COLUMN_STUDENT_NAME + " ASC"
            );
            while (cursor.moveToNext()) {
                Student student = StudentMapper.fromCursor(cursor, context);
                list.add(student);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public List<Student> getStudentsByParentId(String parentId) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    SqlDatabaseHelper.TABLE_STUDENT,
                    null,
                    SqlDatabaseHelper.COLUMN_PARENT_ID + " = ?",
                    new String[]{parentId},
                    null,
                    null,
                    SqlDatabaseHelper.COLUMN_STUDENT_NAME + " ASC"
            );
            while (cursor.moveToNext()) {
                Student student = StudentMapper.fromCursor(cursor, context);
                students.add(student);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return students;
    }
}