package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Thêm import này
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Teacher;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherHomeManagerActivity extends AppCompatActivity {

    private TextView tvTeacherName, tvTeacherClass;
    private Button btnViewTimetable, btnManageSchedule, btnManageClass, btnLogout;
    // Không cần khai báo teacherDao ở đây nữa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_home);

        // Lấy dữ liệu từ Intent
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        String teacherId = getIntent().getStringExtra("teacherId");

        // Kiểm tra điều kiện đầu vào
        if (!isAdmin && teacherId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin đăng nhập.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Ánh xạ các view
        tvTeacherName = findViewById(R.id.tvTeacherName);
        tvTeacherClass = findViewById(R.id.tvTeacherClass);
        btnViewTimetable = findViewById(R.id.btnViewTimetable);
        btnManageSchedule = findViewById(R.id.btnManageSchedule);
        // btnManageClass = findViewById(R.id.btnManageClass);
        btnLogout = findViewById(R.id.btnLogout);

        // Phân luồng logic để thiết lập giao diện
        if (isAdmin) {
            // --- THIẾT LẬP GIAO DIỆN CHO ADMIN ---
            tvTeacherName.setText("👑 Quản trị viên: admin");
            tvTeacherClass.setText("Quyền truy cập: Toàn hệ thống");

            // Ví dụ: Admin có thể không có TKB cá nhân nhưng có thể quản lý các TKB khác
            // btnViewTimetable.setText("Quản lý TKB các lớp");

        } else {
            // --- THIẾT LẬP GIAO DIỆN CHO TEACHER ---
            // Chỉ khởi tạo và truy vấn Dao khi chắc chắn đây là Teacher
            TeacherDao teacherDao = new TeacherDao(this);
            Teacher teacher = teacherDao.getById(teacherId);

            if (teacher != null) {
                tvTeacherName.setText("👩‍🏫 Giáo viên: " + teacher.getFullName());
                tvTeacherClass.setText("📚 Lớp phụ trách: Lá 2"); // Tạm thời, sau này có thể lấy từ DB
            } else {
                // Xử lý trường hợp không tìm thấy teacher trong DB dù có teacherId
                tvTeacherName.setText("👩‍🏫 Giáo viên: Lỗi");
                tvTeacherClass.setText("Không tìm thấy thông tin giáo viên trong hệ thống.");
                Toast.makeText(this, "Lỗi: Không tìm thấy thông tin cho mã giáo viên " + teacherId, Toast.LENGTH_LONG).show();
            }
        }

        // Thiết lập các sự kiện click
        btnViewTimetable.setOnClickListener(view -> {
            // Có thể thêm logic kiểm tra isAdmin ở đây nếu chức năng của Admin khác
            if (isAdmin) {
                Toast.makeText(this, "Chức năng xem TKB cho Admin đang phát triển", Toast.LENGTH_SHORT).show();
                // Ví dụ: có thể mở một activity khác
                // Intent intent = new Intent(this, AdminManageAllSchedulesActivity.class);
                // startActivity(intent);
            } else {
                Intent intent = new Intent(this, TeacherViewTimeTableManagerActivity.class);
                intent.putExtra("teacherId", teacherId);
                startActivity(intent);
            }
        });

        btnManageSchedule.setOnClickListener(view -> {
            Intent intent = new Intent(this, TeacherScheduleManagerActivity.class);
            // Cả admin và teacher đều có thể cần quản lý lịch trình, nên gửi cả 2 thông tin
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("teacherId", teacherId);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AccountManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}