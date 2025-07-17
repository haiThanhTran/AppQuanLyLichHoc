package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_prm392_kidmanagement.DAO.AccountDao;
import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.DAO.ParentDao;
import com.example.project_prm392_kidmanagement.DAO.ScheduleDao;
import com.example.project_prm392_kidmanagement.DAO.StudentDao;
import com.example.project_prm392_kidmanagement.DAO.StudentToClassDao;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Account;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.Entity.Parent;
import com.example.project_prm392_kidmanagement.Entity.Schedule;
import com.example.project_prm392_kidmanagement.Entity.Student;
import com.example.project_prm392_kidmanagement.Entity.Teacher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Chỉ để test, không nên giữ lại trong bản release
        // insertSampleDataIfNeeded();

        // Chuyển thẳng đến màn hình đăng nhập
        startActivity(new Intent(this, AccountManagerActivity.class));
        finish();
    }

    // Hàm này chỉ nên dùng để test, kiểm tra xem dữ liệu đã có chưa
    private void insertSampleDataIfNeeded() {
        AccountDao accountDao = new AccountDao(this);
        // Nếu bảng tài khoản trống thì mới chèn dữ liệu
        if (accountDao.getAll().isEmpty()) {
            TeacherDao teacherDao = new TeacherDao(this);
            ParentDao parentDao = new ParentDao(this);
            ClassDao classDao = new ClassDao(this);
            ScheduleDao scheduleDao = new ScheduleDao(this);
            StudentDao studentDao = new StudentDao(this);

            // 1. Thêm giáo viên
            Teacher teacher = new Teacher("GV001", "Nguyễn Xuân Mai", "Hà Nội", "0909123456", "1980-04-12");
            teacherDao.insert(teacher);

            // 2. Thêm phụ huynh
            Parent parent = new Parent("PH001", "Bùi Trung Hiếu", "Yên Bái", "0912345678", "1985-08-30");
            parentDao.insert(parent);

            // 3. Thêm tài khoản
            Account adminAcc = new Account(0, "admin", "admin", "admin@school.com", 1, null, null);
            Account teacherAcc = new Account(0, "teacher", "123456", "teacher.mai@school.com", 1, teacher, null);
            Account parentAcc = new Account(0, "parent", "123456", "parent.hieu@email.com", 0, null, parent);
            accountDao.insert(adminAcc);
            accountDao.insert(teacherAcc);
            accountDao.insert(parentAcc);

            // 4. Thêm lớp học
            Class class1 = new Class("CL01", "Lá 2", "2024-2025", teacher);
            classDao.insert(class1);

            // 5. Thêm học sinh
            Student student = new Student("STU01", parent, "Bùi Gia Huy", "Yên Bái", "2020-01-15", class1);
            studentDao.insert(student);

            // 6. Thêm lịch học cho lớp
            Schedule schedule1 = new Schedule(0, class1, teacher, "Học toán", "2025-07-18", 1);
            Schedule schedule2 = new Schedule(0, class1, teacher, "Vẽ tranh", "2025-07-18", 2);
            scheduleDao.insert(schedule1);
            scheduleDao.insert(schedule2);

            Toast.makeText(this, "Đã chèn dữ liệu mẫu!", Toast.LENGTH_LONG).show();
        }
    }
}