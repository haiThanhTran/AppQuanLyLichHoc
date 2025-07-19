package com.example.project_prm392_kidmanagement.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Kid_database";
    // TĂNG VERSION LÊN để kích hoạt onUpgrade và tạo lại database đúng cấu trúc
    public static final int DATABASE_VERSION = 12; // Đã tăng lên 7

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

    public static final String TABLE_SCHEDULE = "schedules";
    public static final String COLUMN_SCHEDULE_ID = "scheduleId";
    public static final String COLUMN_ACTIVITY_NAME = "activityName";
    public static final String COLUMN_ACTIVITY_DATE = "activityDate";
    public static final String COLUMN_SHIFT = "shift";


    // --- Các câu lệnh CREATE TABLE ---
    private static final String CREATE_TEACHER_TABLE = "CREATE TABLE " + TABLE_TEACHER + " (teacherId TEXT PRIMARY KEY, fullName TEXT, address TEXT, phone TEXT, dob TEXT);";
    private static final String CREATE_PARENT_TABLE = "CREATE TABLE " + TABLE_PARENT + " (parentId TEXT PRIMARY KEY, fullName TEXT, address TEXT, phone TEXT, dob TEXT);";

    // Bỏ khóa ngoại scheduleId khỏi bảng Class
    private static final String CREATE_CLASS_TABLE =
            "CREATE TABLE " + TABLE_CLASS + " (" +
                    COLUMN_CLASS_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_CLASS_NAME + " TEXT, " +
                    COLUMN_SCHOOL_YEAR + " TEXT, " +
                    COLUMN_TEACHER_ID + " TEXT, " +
                    "isDeleted" + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY (" + COLUMN_TEACHER_ID + ") REFERENCES " + TABLE_TEACHER + "(" + COLUMN_TEACHER_ID + "));";

    // Bỏ khóa ngoại classId khỏi bảng Student (sẽ quản lý qua bảng studentToClass)
    private static final String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + " (studentId TEXT PRIMARY KEY, parentId TEXT, fullName TEXT, address TEXT, dob TEXT, FOREIGN KEY (parentId) REFERENCES " + TABLE_PARENT + "(parentId));";

    private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + " (accountId INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, email TEXT UNIQUE NOT NULL, role INTEGER NOT NULL, teacherId TEXT, parentId TEXT, FOREIGN KEY (teacherId) REFERENCES " + TABLE_TEACHER + "(teacherId), FOREIGN KEY (parentId) REFERENCES " + TABLE_PARENT + "(parentId));";

    private static final String CREATE_STUDENT_TO_CLASS_TABLE = "CREATE TABLE " + TABLE_STUDENT_TO_CLASS + " (studentClassID INTEGER PRIMARY KEY AUTOINCREMENT, studentId TEXT NOT NULL, classId TEXT NOT NULL, FOREIGN KEY (studentId) REFERENCES " + TABLE_STUDENT + "(studentId), FOREIGN KEY (classId) REFERENCES " + TABLE_CLASS + "(classId));";

    private static final String CREATE_SCHEDULE_TABLE =
            "CREATE TABLE " + TABLE_SCHEDULE + " (" +
                    COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CLASS_ID + " TEXT NOT NULL, " +
                    COLUMN_TEACHER_ID + " TEXT, " +
                    COLUMN_ACTIVITY_NAME + " TEXT NOT NULL, " +
                    COLUMN_ACTIVITY_DATE + " TEXT NOT NULL, " +
                    COLUMN_SHIFT + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_CLASS_ID + ") REFERENCES " + TABLE_CLASS + "(" + COLUMN_CLASS_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_TEACHER_ID + ") REFERENCES " + TABLE_TEACHER + "(" + COLUMN_TEACHER_ID + "));";


    public SqlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo tất cả các bảng theo thứ tự đúng
        db.execSQL(CREATE_TEACHER_TABLE);
        db.execSQL(CREATE_PARENT_TABLE);
        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_SCHEDULE_TABLE);
        db.execSQL(CREATE_STUDENT_TO_CLASS_TABLE);

        // Chèn dữ liệu mẫu
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng theo thứ tự ngược lại để tránh lỗi khóa ngoại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_TO_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);

        // Tạo lại
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // === BƯỚC 1: TẠO HỒ SƠ GIÁO VIÊN VÀ PHỤ HUYNH ===
        // Giáo viên
        ContentValues teacher1 = new ContentValues();
        teacher1.put(COLUMN_TEACHER_ID, "GV001");
        teacher1.put(COLUMN_TEACHER_NAME, "Nguyễn Thị Mai");
        teacher1.put(COLUMN_TEACHER_ADDRESS, "123 Đường Cầu Giấy, Hà Nội");
        teacher1.put(COLUMN_TEACHER_PHONE, "0911223344");
        teacher1.put(COLUMN_TEACHER_DOB, "1990-05-15");
        db.insert(TABLE_TEACHER, null, teacher1);

        ContentValues teacher2 = new ContentValues();
        teacher2.put(COLUMN_TEACHER_ID, "GV002");
        teacher2.put(COLUMN_TEACHER_NAME, "Lê Văn Hùng");
        teacher2.put(COLUMN_TEACHER_ADDRESS, "456 Đường Láng, Hà Nội");
        teacher2.put(COLUMN_TEACHER_PHONE, "0922334455");
        teacher2.put(COLUMN_TEACHER_DOB, "1988-10-20");
        db.insert(TABLE_TEACHER, null, teacher2);

        // Phụ huynh
        ContentValues parent1 = new ContentValues();
        parent1.put(COLUMN_PARENT_ID, "PH001");
        parent1.put(COLUMN_PARENT_NAME, "Trần Văn An");
        parent1.put(COLUMN_PARENT_ADDRESS, "789 Đường Kim Mã, Hà Nội");
        parent1.put(COLUMN_PARENT_PHONE, "0933445566");
        parent1.put(COLUMN_PARENT_DOB, "1985-01-01");
        db.insert(TABLE_PARENT, null, parent1);

        ContentValues parent2 = new ContentValues();
        parent2.put(COLUMN_PARENT_ID, "PH002");
        parent2.put(COLUMN_PARENT_NAME, "Phạm Thị Bình");
        parent2.put(COLUMN_PARENT_ADDRESS, "101 Đường Nguyễn Chí Thanh, Hà Nội");
        parent2.put(COLUMN_PARENT_PHONE, "0944556677");
        parent2.put(COLUMN_PARENT_DOB, "1987-02-02");
        db.insert(TABLE_PARENT, null, parent2);


        // === BƯỚC 2: TẠO CÁC TÀI KHOẢN VÀ LIÊN KẾT ===
        ContentValues adminAccount = new ContentValues();
        adminAccount.put(COLUMN_USERNAME, "admin");
        adminAccount.put(COLUMN_PASSWORD, "admin");
        adminAccount.put(COLUMN_EMAIL, "admin@school.edu.vn");
        adminAccount.put(COLUMN_ROLE, 1); // Admin
        db.insert(TABLE_ACCOUNT, null, adminAccount);

        ContentValues teacherAccount = new ContentValues();
        teacherAccount.put(COLUMN_USERNAME, "teacher");
        teacherAccount.put(COLUMN_PASSWORD, "teacher");
        teacherAccount.put(COLUMN_EMAIL, "teacher.mai@school.edu.vn");
        teacherAccount.put(COLUMN_ROLE, 1); // Teacher
        teacherAccount.put(COLUMN_TEACHER_ID, "GV001");
        db.insert(TABLE_ACCOUNT, null, teacherAccount);

        ContentValues parentAccount = new ContentValues();
        parentAccount.put(COLUMN_USERNAME, "parent");
        parentAccount.put(COLUMN_PASSWORD, "parent");
        parentAccount.put(COLUMN_EMAIL, "parent.an@email.com");
        parentAccount.put(COLUMN_ROLE, 0); // Parent
        parentAccount.put(COLUMN_PARENT_ID, "PH001");
        db.insert(TABLE_ACCOUNT, null, parentAccount);


        // === BƯỚC 3: TẠO HỌC SINH VÀ LIÊN KẾT VỚI PHỤ HUYNH ===
        ContentValues student1 = new ContentValues();
        student1.put(COLUMN_STUDENT_ID, "HS001");
        student1.put(COLUMN_STUDENT_NAME, "Trần Minh Quân");
        student1.put(COLUMN_STUDENT_DOB, "2020-03-10");
        student1.put(COLUMN_PARENT_ID, "PH001"); // Con của ông An
        db.insert(TABLE_STUDENT, null, student1);

        ContentValues student2 = new ContentValues();
        student2.put(COLUMN_STUDENT_ID, "HS002");
        student2.put(COLUMN_STUDENT_NAME, "Trần Bảo Châu");
        student2.put(COLUMN_STUDENT_DOB, "2021-07-22");
        student2.put(COLUMN_PARENT_ID, "PH001"); // Con của ông An
        db.insert(TABLE_STUDENT, null, student2);

        ContentValues student3 = new ContentValues();
        student3.put(COLUMN_STUDENT_ID, "HS003");
        student3.put(COLUMN_STUDENT_NAME, "Phạm Gia Hân");
        student3.put(COLUMN_STUDENT_DOB, "2020-05-18");
        student3.put(COLUMN_PARENT_ID, "PH002"); // Con của bà Bình
        db.insert(TABLE_STUDENT, null, student3);


        // === BƯỚC 4: TẠO LỚP HỌC VÀ PHÂN CÔNG GVCN ===
        ContentValues class1 = new ContentValues();
        class1.put(COLUMN_CLASS_ID, "L01");
        class1.put(COLUMN_CLASS_NAME, "Lớp Lá 1");
        class1.put(COLUMN_SCHOOL_YEAR, "2024-2025");
        class1.put(COLUMN_TEACHER_ID, "GV001"); // Cô Mai chủ nhiệm
        db.insert(TABLE_CLASS, null, class1);

        ContentValues class2 = new ContentValues();
        class2.put(COLUMN_CLASS_ID, "M01");
        class2.put(COLUMN_CLASS_NAME, "Lớp Mầm 2");
        class2.put(COLUMN_SCHOOL_YEAR, "2024-2025");
        class2.put(COLUMN_TEACHER_ID, "GV002"); // Thầy Hùng chủ nhiệm
        db.insert(TABLE_CLASS, null, class2);


        // === BƯỚC 5: XẾP HỌC SINH VÀO LỚP ===
        ContentValues s2c1 = new ContentValues();
        s2c1.put(COLUMN_STUDENT_ID, "HS001"); // Bé Quân
        s2c1.put(COLUMN_CLASS_ID, "L01");    // vào lớp Lá 1
        db.insert(TABLE_STUDENT_TO_CLASS, null, s2c1);

        ContentValues s2c2 = new ContentValues();
        s2c2.put(COLUMN_STUDENT_ID, "HS003"); // Bé Hân
        s2c2.put(COLUMN_CLASS_ID, "L01");    // vào lớp Lá 1
        db.insert(TABLE_STUDENT_TO_CLASS, null, s2c2);

        ContentValues s2c3 = new ContentValues();
        s2c3.put(COLUMN_STUDENT_ID, "HS002"); // Bé Châu
        s2c3.put(COLUMN_CLASS_ID, "M01");    // vào lớp Mầm 2
        db.insert(TABLE_STUDENT_TO_CLASS, null, s2c3);


        // === BƯỚC 6: TẠO THỜI KHÓA BIỂU MẪU CHO CÁC LỚP ===
        // TKB cho Lớp Lá 1 (L01) - Thứ Hai (2025-07-21)
        ContentValues schedule1 = new ContentValues();
        schedule1.put(COLUMN_CLASS_ID, "L01");
        schedule1.put(COLUMN_ACTIVITY_NAME, "Chào buổi sáng - Thể dục");
        schedule1.put(COLUMN_ACTIVITY_DATE, "2025-07-21");
        schedule1.put(COLUMN_SHIFT, 1); // Ca 1
        schedule1.put(COLUMN_TEACHER_ID, "GV001");
        db.insert(TABLE_SCHEDULE, null, schedule1);

        ContentValues schedule2 = new ContentValues();
        schedule2.put(COLUMN_CLASS_ID, "L01");
        schedule2.put(COLUMN_ACTIVITY_NAME, "Học toán: Đếm số 1-10");
        schedule2.put(COLUMN_ACTIVITY_DATE, "2025-07-21");
        schedule2.put(COLUMN_SHIFT, 2); // Ca 2
        schedule2.put(COLUMN_TEACHER_ID, "GV001");
        db.insert(TABLE_SCHEDULE, null, schedule2);

        // TKB cho Lớp Mầm 2 (M01) - Thứ Hai (2025-07-21)
        ContentValues schedule3 = new ContentValues();
        schedule3.put(COLUMN_CLASS_ID, "M01");
        schedule3.put(COLUMN_ACTIVITY_NAME, "Vận động: Ném bóng vào rổ");
        schedule3.put(COLUMN_ACTIVITY_DATE, "2025-07-21");
        schedule3.put(COLUMN_SHIFT, 1); // Ca 1
        schedule3.put(COLUMN_TEACHER_ID, "GV002");
        db.insert(TABLE_SCHEDULE, null, schedule3);
    }
}