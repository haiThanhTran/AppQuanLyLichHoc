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
    private final Context context;

    public ScheduleDao(Context context) {
        dbHelper = new SqlDatabaseHelper(context);
        this.context = context;
    }

    public long insert(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("classId", schedule.getClassId().getClassId());
        values.put("teacherId", schedule.getTeacherId().getTeacherId());
        values.put("activityName", schedule.getActivityName());
        values.put("activityDate", schedule.getActivityDate());
        values.put("shift", schedule.getShift());
        return db.insert(SqlDatabaseHelper.TABLE_SCHEDULE, null, values);
    }

    public boolean update(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("classId", schedule.getClassId().getClassId());
        values.put("teacherId", schedule.getTeacherId().getTeacherId());
        values.put("activityName", schedule.getActivityName());
        values.put("activityDate", schedule.getActivityDate());
        values.put("shift", schedule.getShift());
        int rows = db.update(SqlDatabaseHelper.TABLE_SCHEDULE, values, "scheduleId = ?",
                new String[]{String.valueOf(schedule.getScheduleId())});
        return rows > 0;
    }

    public boolean delete(int scheduleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(SqlDatabaseHelper.TABLE_SCHEDULE, "scheduleId = ?",
                new String[]{String.valueOf(scheduleId)});
        return rows > 0;
    }

    // Thêm các hàm truy vấn mới
    public List<Schedule> getSchedulesByClassId(String classId) {
        List<Schedule> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SqlDatabaseHelper.TABLE_SCHEDULE, null, "classId = ?",
                new String[]{classId}, null, null, "activityDate ASC, shift ASC");

        while (cursor.moveToNext()) {
            list.add(ScheduleMapper.fromCursor(cursor, context));
        }
        cursor.close();
        return list;
    }
}