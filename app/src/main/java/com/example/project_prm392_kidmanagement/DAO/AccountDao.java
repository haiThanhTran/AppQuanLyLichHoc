package com.example.project_prm392_kidmanagement.DAO;

import static com.example.project_prm392_kidmanagement.DB.SqlDatabaseHelper.TABLE_ACCOUNT;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.project_prm392_kidmanagement.DB.SqlDatabaseHelper;
import com.example.project_prm392_kidmanagement.Entity.Account;
import com.example.project_prm392_kidmanagement.Mapper.AccountMapper;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    private final SqlDatabaseHelper dbHelper;
    private final Context context;

    public AccountDao(Context context) {
        dbHelper = new SqlDatabaseHelper(context);
        this.context = context;
    }

    public List<Account> searchAndFilter(String query, int roleFilter) {
        List<Account> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = null;
        ArrayList<String> selectionArgs = new ArrayList<>();
        if (query != null && !query.isEmpty()) {
            selection = "(username LIKE ? OR email LIKE ?)";
            selectionArgs.add("%" + query + "%");
            selectionArgs.add("%" + query + "%");
        }
        if (roleFilter != -1) {
            if (selection == null) {
                selection = "";
            } else {
                selection += " AND ";
            }
            if (roleFilter == 2) { // Admin
                selection += "role = ? AND teacherId IS NULL";
                selectionArgs.add("1");
            } else if (roleFilter == 1) { // Teacher
                selection += "role = ? AND teacherId IS NOT NULL";
                selectionArgs.add("1");
            } else { // Parent (role 0)
                selection += "role = ?";
                selectionArgs.add(String.valueOf(roleFilter));
            }
        }
        Cursor cursor = db.query(TABLE_ACCOUNT, null, selection, selectionArgs.toArray(new String[0]), null, null, "username ASC");
        while (cursor.moveToNext()) {
            list.add(AccountMapper.fromCursor(cursor, context));
        }
        cursor.close();
        return list;
    }

    public long insert(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", account.getUsername());
        values.put("password", account.getPassword());
        values.put("email", account.getEmail());
        values.put("role", account.isRole());
        values.put("teacherId", account.getTeacherId() != null ? account.getTeacherId().getTeacherId() : null);
        values.put("parentId", account.getParentId() != null ? account.getParentId().getParentId() : null);
        return db.insert(TABLE_ACCOUNT, null, values);
    }

    public boolean update(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", account.getPassword());
        values.put("email", account.getEmail());
        values.put("role", account.isRole());
        values.put("teacherId", account.getTeacherId() != null ? account.getTeacherId().getTeacherId() : null);
        values.put("parentId", account.getParentId() != null ? account.getParentId().getParentId() : null);
        int rowsAffected = db.update(TABLE_ACCOUNT, values, "accountId = ?", new String[]{String.valueOf(account.getAccountId())});
        return rowsAffected > 0;
    }

    public boolean delete(int accountId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_ACCOUNT, "accountId = ?", new String[]{String.valueOf(accountId)});
        return rowsDeleted > 0;
    }

    public Account getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNT, null, "accountId = ?", new String[]{String.valueOf(id)}, null, null, null);
        Account account = null;
        if (cursor.moveToFirst()) {
            account = AccountMapper.fromCursor(cursor, context);
        }
        cursor.close();
        return account;
    }

    public Account getByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNT, null, "username = ?", new String[]{username}, null, null, null);
        Account account = null;
        if (cursor.moveToFirst()) {
            account = AccountMapper.fromCursor(cursor, context);
        }
        cursor.close();
        return account;
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, null, null);
    }

    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNT, null, null, null, null, null, "username ASC");
        while (cursor.moveToNext()) {
            list.add(AccountMapper.fromCursor(cursor, context));
        }
        cursor.close();
        return list;
    }

    public boolean validate(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNT, null, "username = ? AND password = ?", new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}