package com.example.project_prm392_kidmanagement.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Kid_database";
    // TĂNG VERSION LÊN để kích hoạt onUpgrade và tạo lại database đúng cấu trúc
    public static final int DATABASE_VERSION = 5;

    private final Context context;

    public SqlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // --- Khai báo các bảng và cột ---
    public static final String TABLE_TEACHER = "teachers";
    public static final String COLUMN_TEACHER_ID = "teacherId";
    public static final String COLUMN_TEACHER_NAME = "fullName";
    public static final String COLUMN_TEACHER_ADDRESS = "address";
    public static final String COLUMN_TEACHER_PHONE = "phone";
    public static final String COLUMN_TEACHER_DOB = "dob";

    public static final String TABLE_PARENT = "parents";
    public static final String COLUMN_PARENT_ID = "parentId";
    public static final String COLUMN_PARENT_NAME = "fullName";
    public static final String COLUMN_PARENT_ADDRESS = "address";
    public static final String COLUMN_PARENT_PHONE = "phone";
    public static final String COLUMN_PARENT_DOB = "dob";

    public static final String TABLE_CLASS = "classes";
    public static final String COLUMN_CLASS_ID = "classId";
    public static final String COLUMN_CLASS_NAME = "className";
    public static final String COLUMN_SCHOOL_YEAR = "schoolYear";

    public static final String TABLE_STUDENT = "students";
    public static final String COLUMN_STUDENT_ID = "studentId";
    public static final String COLUMN_STUDENT_NAME = "fullName";
    public static final String COLUMN_STUDENT_ADDRESS = "address";
    public static final String COLUMN_STUDENT_DOB = "dob";

    public static final String TABLE_ACCOUNT = "accounts";
    public static final String COLUMN_ACCOUNT_ID = "accountId";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ROLE = "role";

    public static final String TABLE_STUDENT_TO_CLASS = "studentToClass";
    public static final String COLUMN_STUDENT_TO_CLASS_ID = "studentClassID";

    // ---- BẢNG SCHEDULE ĐƯỢC THIẾT KẾ LẠI ----
    public static final String TABLE_SCHEDULE = "schedules";
    public static final String COLUMN_SCHEDULE_ID = "scheduleId";
    public static final String COLUMN_ACTIVITY_NAME = "activityName";
    public static final String COLUMN_ACTIVITY_DATE = "activityDate";
    public static final String COLUMN_SHIFT = "shift";


    // --- Các câu lệnh CREATE TABLE ---
    private static final String CREATE_TEACHER_TABLE = "CREATE TABLE " + TABLE_TEACHER + " (teacherId TEXT PRIMARY KEY, fullName TEXT, address TEXT, phone TEXT, dob TEXT);";
    private static final String CREATE_PARENT_TABLE = "CREATE TABLE " + TABLE_PARENT + " (parentId TEXT PRIMARY KEY, fullName TEXT, address TEXT, phone TEXT, dob TEXT);";

    private static final String CREATE_CLASS_TABLE =
            "CREATE TABLE " + TABLE_CLASS + " (" +
                    COLUMN_CLASS_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_CLASS_NAME + " TEXT, " +
                    COLUMN_SCHOOL_YEAR + " TEXT, " +
                    COLUMN_TEACHER_ID + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_TEACHER_ID + ") REFERENCES " + TABLE_TEACHER + "(" + COLUMN_TEACHER_ID + "));";

    private static final String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + " (studentId TEXT PRIMARY KEY, parentId TEXT, fullName TEXT, address TEXT, dob TEXT, classId TEXT, FOREIGN KEY (parentId) REFERENCES " + TABLE_PARENT + "(parentId), FOREIGN KEY (classId) REFERENCES " + TABLE_CLASS + "(classId));";

    private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + " (accountId INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, email TEXT UNIQUE NOT NULL, role INTEGER NOT NULL, teacherId TEXT, parentId TEXT, FOREIGN KEY (teacherId) REFERENCES " + TABLE_TEACHER + "(teacherId), FOREIGN KEY (parentId) REFERENCES " + TABLE_PARENT + "(parentId));";

    private static final String CREATE_STUDENT_TO_CLASS_TABLE = "CREATE TABLE " + TABLE_STUDENT_TO_CLASS + " (studentClassID TEXT PRIMARY KEY, studentId TEXT, classId TEXT, FOREIGN KEY (studentId) REFERENCES " + TABLE_STUDENT + "(studentId), FOREIGN KEY (classId) REFERENCES " + TABLE_CLASS + "(classId));";

    private static final String CREATE_SCHEDULE_TABLE =
            "CREATE TABLE " + TABLE_SCHEDULE + " (" +
                    COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CLASS_ID + " TEXT NOT NULL, " +
                    COLUMN_TEACHER_ID + " TEXT NOT NULL, " +
                    COLUMN_ACTIVITY_NAME + " TEXT NOT NULL, " +
                    COLUMN_ACTIVITY_DATE + " TEXT NOT NULL, " +
                    COLUMN_SHIFT + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_CLASS_ID + ") REFERENCES " + TABLE_CLASS + "(" + COLUMN_CLASS_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_TEACHER_ID + ") REFERENCES " + TABLE_TEACHER + "(" + COLUMN_TEACHER_ID + "));";


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TEACHER_TABLE);
        db.execSQL(CREATE_PARENT_TABLE);
        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_SCHEDULE_TABLE);
        db.execSQL(CREATE_STUDENT_TO_CLASS_TABLE);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "schedulesToClass");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_TO_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);

        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // --- BƯỚC 1: TẠO DỮ LIỆU CHO TEACHER VÀ PARENT TRƯỚC ---
        ContentValues teacherValues1 = new ContentValues();
        teacherValues1.put(COLUMN_TEACHER_ID, "GV001");
        teacherValues1.put(COLUMN_TEACHER_NAME, "Nguyễn Thị A");
        teacherValues1.put(COLUMN_TEACHER_ADDRESS, "123 Đường ABC, Quận 1, TPHCM");
        teacherValues1.put(COLUMN_TEACHER_PHONE, "0901234567");
        teacherValues1.put(COLUMN_TEACHER_DOB, "1990-05-15");
        db.insert(TABLE_TEACHER, null, teacherValues1);

        ContentValues parentValues1 = new ContentValues();
        parentValues1.put(COLUMN_PARENT_ID, "PH001");
        parentValues1.put(COLUMN_PARENT_NAME, "Trần Văn B");
        parentValues1.put(COLUMN_PARENT_ADDRESS, "456 Đường XYZ, Quận 2, TPHCM");
        parentValues1.put(COLUMN_PARENT_PHONE, "0987654321");
        parentValues1.put(COLUMN_PARENT_DOB, "1985-10-20");
        db.insert(TABLE_PARENT, null, parentValues1);

        // --- BƯỚC 2: TẠO CÁC TÀI KHOẢN ---
        ContentValues adminAccount = new ContentValues();
        adminAccount.put(COLUMN_USERNAME, "admin");
        adminAccount.put(COLUMN_PASSWORD, "admin");
        adminAccount.put(COLUMN_EMAIL, "admin@school.edu.vn");
        adminAccount.put(COLUMN_ROLE, 1);
        db.insert(TABLE_ACCOUNT, null, adminAccount);

        ContentValues teacherAccount = new ContentValues();
        teacherAccount.put(COLUMN_USERNAME, "teacher");
        teacherAccount.put(COLUMN_PASSWORD, "teacher");
        teacherAccount.put(COLUMN_EMAIL, "teacher.a@school.edu.vn");
        teacherAccount.put(COLUMN_ROLE, 1);
        teacherAccount.put(COLUMN_TEACHER_ID, "GV001");
        db.insert(TABLE_ACCOUNT, null, teacherAccount);

        ContentValues parentAccount = new ContentValues();
        parentAccount.put(COLUMN_USERNAME, "parent");
        parentAccount.put(COLUMN_PASSWORD, "parent");
        parentAccount.put(COLUMN_EMAIL, "parent.b@email.com");
        parentAccount.put(COLUMN_ROLE, 0);
        parentAccount.put(COLUMN_PARENT_ID, "PH001");
        db.insert(TABLE_ACCOUNT, null, parentAccount);
    }
}