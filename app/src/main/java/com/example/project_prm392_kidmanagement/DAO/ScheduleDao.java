package com.example.project_prm392_kidmanagement.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project_prm392_kidmanagement.DB.SqlDatabaseHelper;
import com.example.project_prm392_kidmanagement.Entity.Schedule;
import com.example.project_prm392_kidmanagement.Mapper.ScheduleMapper;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDao {
    private final SqlDatabaseHelper dbHelper;
    private Context context;

    public ScheduleDao(Context context) {
        this.context = context;
        dbHelper = new SqlDatabaseHelper(context);
    }

    public long insert(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.COLUMN_CLASS_ID, schedule.getClassId().getClassId());
        values.put(SqlDatabaseHelper.COLUMN_TEACHER_ID, schedule.getTeacherId() != null ? schedule.getTeacherId().getTeacherId() : null);
        values.put(SqlDatabaseHelper.COLUMN_ACTIVITY_NAME, schedule.getActivityName());
        values.put(SqlDatabaseHelper.COLUMN_ACTIVITY_DATE, schedule.getActivityDate());
        values.put(SqlDatabaseHelper.COLUMN_SHIFT, schedule.getShift());
        long result = db.insert(SqlDatabaseHelper.TABLE_SCHEDULE, null, values);
        db.close();
        return result;
    }

    // Lấy TKB cho 1 lớp vào 1 ngày cụ thể
    public List<Schedule> getScheduleForClassByDate(String classId, String date) {
        List<Schedule> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SqlDatabaseHelper.TABLE_SCHEDULE, null,
                SqlDatabaseHelper.COLUMN_CLASS_ID + " = ? AND " + SqlDatabaseHelper.COLUMN_ACTIVITY_DATE + " = ?",
                new String[]{classId, date},
                null, null, SqlDatabaseHelper.COLUMN_SHIFT + " ASC");

        while (cursor.moveToNext()) {
            list.add(ScheduleMapper.fromCursor(cursor, context));
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean delete(int scheduleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(SqlDatabaseHelper.TABLE_SCHEDULE, SqlDatabaseHelper.COLUMN_SCHEDULE_ID + " = ?",
                new String[]{String.valueOf(scheduleId)});
        db.close();
        return rows > 0;
    }
    public List<Schedule> getSchedulesByClassId(String classId) {
        List<Schedule> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(SqlDatabaseHelper.TABLE_SCHEDULE, null,
                    SqlDatabaseHelper.COLUMN_CLASS_ID + " = ?",
                    new String[]{classId},
                    null, null,
                    SqlDatabaseHelper.COLUMN_ACTIVITY_DATE + " ASC, " + SqlDatabaseHelper.COLUMN_SHIFT + " ASC");

            while (cursor.moveToNext()) {
                list.add(ScheduleMapper.fromCursor(cursor, context));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }
}