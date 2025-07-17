package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.DAO.ParentDao;
import com.example.project_prm392_kidmanagement.DAO.ScheduleDao;
import com.example.project_prm392_kidmanagement.DAO.StudentDao;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.Entity.Parent;
import com.example.project_prm392_kidmanagement.Entity.Schedule;
import com.example.project_prm392_kidmanagement.Entity.Student;
import com.example.project_prm392_kidmanagement.Entity.Teacher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ParentHomeManagerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvStudentName, tvClassInfo, tvTeacherName;
    private Button btnLogout;
    private LinearLayout scheduleContainer;

    private ParentDao parentDao;
    private StudentDao studentDao;
    private ClassDao classDao;
    private TeacherDao teacherDao;
    private ScheduleDao scheduleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_schedule);

        String parentId = getIntent().getStringExtra("parentId");
        if (parentId == null) {
            Toast.makeText(this, "Không tìm thấy mã phụ huynh", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setSupportActionBar(toolbar);
        initDAOs();
        loadAndDisplayData(parentId);
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvClassInfo = findViewById(R.id.tvClassInfo);
        tvTeacherName = findViewById(R.id.tvTeacherName);
        btnLogout = findViewById(R.id.btnLogout);
        scheduleContainer = findViewById(R.id.scheduleContainer);
    }

    private void initDAOs() {
        parentDao = new ParentDao(this);
        studentDao = new StudentDao(this);
        classDao = new ClassDao(this);
        teacherDao = new TeacherDao(this);
        scheduleDao = new ScheduleDao(this);
    }

    private void loadAndDisplayData(String parentId) {
        Parent parent = parentDao.getById(parentId);
        if (parent != null) {
            toolbar.setSubtitle("Phụ huynh: " + parent.getFullName());
        }

        List<Student> studentList = studentDao.getStudentsByParentId(parentId);
        if (studentList != null && !studentList.isEmpty()) {
            Student student = studentList.get(0); // Tạm thời lấy học sinh đầu tiên
            tvStudentName.setText("🧒 Bé: " + student.getFullName());

            if (student.getClassId() != null) {
                Class classroom = classDao.getById(student.getClassId().getClassId());
                if (classroom != null) {
                    tvClassInfo.setText("📚 Lớp: " + classroom.getClassName() + " – Năm học: " + classroom.getSchoolYear());

                    if (classroom.getTeacherId() != null) {
                        Teacher teacher = teacherDao.getById(classroom.getTeacherId().getTeacherId());
                        if (teacher != null) {
                            tvTeacherName.setText("👩‍🏫 GVCN: " + teacher.getFullName());
                        }
                    }

                    // Lấy và hiển thị TKB
                    List<Schedule> schedules = scheduleDao.getSchedulesByClassId(classroom.getClassId());
                    displaySchedules(schedules);
                }
            }
        }
    }

    private void displaySchedules(List<Schedule> schedules) {
        scheduleContainer.removeAllViews(); // Xóa TKB cũ

        if (schedules == null || schedules.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có lịch học cho tuần này.");
            scheduleContainer.addView(tvEmpty);
            return;
        }

        // Gom nhóm TKB theo ngày
        Map<String, List<Schedule>> groupedSchedules = schedules.stream()
                .sorted(Comparator.comparing(Schedule::getShift))
                .collect(Collectors.groupingBy(Schedule::getActivityDate));

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

        // Sắp xếp các ngày
        List<String> sortedDates = groupedSchedules.keySet().stream().sorted().collect(Collectors.toList());

        for (String dateStr : sortedDates) {
            // Hiển thị ngày
            String formattedDate = dateStr;
            try {
                Date date = inputFormat.parse(dateStr);
                formattedDate = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView tvDate = new TextView(this);
            tvDate.setText("🗓️ " + formattedDate);
            tvDate.setTextColor(Color.parseColor("#1A237E"));
            tvDate.setTextSize(16);
            tvDate.setTypeface(null, Typeface.BOLD);
            tvDate.setPadding(0, 24, 0, 8);
            scheduleContainer.addView(tvDate);

            // Hiển thị các hoạt động trong ngày
            List<Schedule> daySchedules = groupedSchedules.get(dateStr);
            if (daySchedules != null) {
                for (Schedule schedule : daySchedules) {
                    TextView tvActivity = new TextView(this);
                    String shiftText = getShiftInfo(schedule.getShift()); // Lấy thông tin ca học
                    String teacherName = (schedule.getTeacherId() != null) ? " (" + schedule.getTeacherId().getFullName() + ")" : "";
                    tvActivity.setText("🔹 " + shiftText + ": " + schedule.getActivityName() + teacherName);
                    tvActivity.setTextColor(Color.parseColor("#37474F"));
                    tvActivity.setTextSize(14);
                    tvActivity.setPadding(16, 0, 0, 16);
                    scheduleContainer.addView(tvActivity);
                }
            }
        }
    }

    private String getShiftInfo(int shift) {
        switch (shift) {
            case 1: return "Ca 1 (Sáng)";
            case 2: return "Ca 2 (Sáng)";
            case 3: return "Ca 3 (Chiều)";
            case 4: return "Ca 4 (Chiều)";
            default: return "Ca học";
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(view -> {
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AccountManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}