package com.example.project_prm392_kidmanagement.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project_prm392_kidmanagement.DB.SqlDatabaseHelper;
import com.example.project_prm392_kidmanagement.Entity.StudentToClass;
import com.example.project_prm392_kidmanagement.Mapper.StudentToClassMapper;

import java.util.ArrayList;
import java.util.List;

public class StudentToClassDao {
    private final SqlDatabaseHelper dbHelper;
    private final Context context;

    public StudentToClassDao(Context context) {
        dbHelper = new SqlDatabaseHelper(context);
        this.context = context; // THÊM DÒNG NÀY
    }

    public long insert(StudentToClass stc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentClassID", stc.getStudentClassID());
        values.put("studentId", stc.getStudentId().getStudentId());
        values.put("classId", stc.getClassId().getClassId());

        return db.insert(SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS, null, values);
    }

    public boolean update(StudentToClass stc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentId", stc.getStudentId().getStudentId());
        values.put("classId", stc.getClassId().getClassId());

        int rows = db.update(
                SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS,
                values,
                "studentClassID = ?",
                new String[]{String.valueOf(stc.getStudentClassID())}
        );
        return rows > 0;
    }

    public boolean delete(int studentClassID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS,
                "studentClassID = ?",
                new String[]{String.valueOf(studentClassID)}
        );
        return rows > 0;
    }

    public StudentToClass getById(int studentClassID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS,
                null,
                "studentClassID = ?",
                new String[]{String.valueOf(studentClassID)},
                null, null, null
        );

        StudentToClass result = null;
        if (cursor.moveToFirst()) {
            result = StudentToClassMapper.fromCursor(cursor, context); // SỬA Ở ĐÂY
        }

        cursor.close();
        return result;
    }

    public List<StudentToClass> getAll() {
        List<StudentToClass> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS,
                null,
                null,
                null,
                null,
                null,
                "studentId ASC"
        );

        while (cursor.moveToNext()) {
            StudentToClass stc = StudentToClassMapper.fromCursor(cursor, context); // SỬA Ở ĐÂY
            list.add(stc);
        }

        cursor.close();
        return list;
    }
    public int countStudentsInClass(String classId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + SqlDatabaseHelper.TABLE_STUDENT_TO_CLASS +
                    " WHERE " + SqlDatabaseHelper.COLUMN_CLASS_ID + " = ?", new String[]{classId});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return count;
    }
}
