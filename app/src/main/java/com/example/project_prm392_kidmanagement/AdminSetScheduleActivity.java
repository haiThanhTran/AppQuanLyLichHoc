package com.example.project_prm392_kidmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.DAO.ScheduleDao;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Schedule;
import com.example.project_prm392_kidmanagement.Entity.Shift;
import com.example.project_prm392_kidmanagement.Entity.Teacher;
import com.example.project_prm392_kidmanagement.adapter.ShiftAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminSetScheduleActivity extends AppCompatActivity implements ShiftAdapter.OnShiftInteractionListener {
    private TextView tvScheduleTitle;
    private EditText etSelectedDate;
    private Button btnPickDate;
    private RecyclerView rvShifts;

    private String classId, className, selectedDate;
    private List<Shift> shiftList;
    private ShiftAdapter adapter;

    private ScheduleDao scheduleDao;
    private TeacherDao teacherDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_set_schedule);

        classId = getIntent().getStringExtra("CLASS_ID");
        className = getIntent().getStringExtra("CLASS_NAME");
        if (classId == null || className == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin lớp học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initDAOs();

        tvScheduleTitle.setText("TKB Lớp: " + className);
        setTitle("TKB Lớp: " + className);

        // --- SỬA LẠI LOGIC KHỞI TẠO ---
        setupRecyclerView();

        etSelectedDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        selectedDate = etSelectedDate.getText().toString();

        btnPickDate.setOnClickListener(v -> showDatePickerDialog());
        etSelectedDate.setOnClickListener(v -> showDatePickerDialog());

        loadScheduleForDate();
    }

    private void initViews() {
        tvScheduleTitle = findViewById(R.id.tvScheduleTitle);
        etSelectedDate = findViewById(R.id.etSelectedDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        rvShifts = findViewById(R.id.rvShifts);
    }

    private void initDAOs() {
        scheduleDao = new ScheduleDao(this);
        teacherDao = new TeacherDao(this);
    }

    private void setupRecyclerView() {
        rvShifts.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo list rỗng và adapter MỘT LẦN DUY NHẤT
        shiftList = new ArrayList<>();
        adapter = new ShiftAdapter(shiftList, this);
        rvShifts.setAdapter(adapter);
    }

    private void loadScheduleForDate() {
        // Tạo một danh sách ca học trống tạm thời
        List<Shift> emptyShifts = createEmptyShifts();
        // Lấy lịch học thực tế từ database
        List<Schedule> dailySchedules = scheduleDao.getScheduleForClassByDate(classId, selectedDate);

        // Gán lịch học thực tế vào đúng ca
        for (Schedule s : dailySchedules) {
            int shiftIndex = s.getShift() - 1;
            if (shiftIndex >= 0 && shiftIndex < emptyShifts.size()) {
                emptyShifts.get(shiftIndex).setSchedule(s);
            }
        }

        // Cập nhật dữ liệu cho adapter
        shiftList.clear();
        shiftList.addAll(emptyShifts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSetActivityClick(Shift shift) {
        showAddEditDialog(shift);
    }

    private void showAddEditDialog(final Shift shift) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule_entry, null);
        builder.setView(dialogView);

        TextInputEditText etActivityName = dialogView.findViewById(R.id.etActivityName);
        Spinner spinnerTeacher = dialogView.findViewById(R.id.spinnerTeacher);

        List<Teacher> teachers = teacherDao.getAll();
        List<String> teacherDisplayList = new ArrayList<>();
        teacherDisplayList.add("Không chọn giáo viên");
        for (Teacher t : teachers) {
            teacherDisplayList.add(t.getTeacherId() + " - " + t.getFullName());
        }
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherDisplayList);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        if (shift.getSchedule() != null) {
            etActivityName.setText(shift.getSchedule().getActivityName());
            if (shift.getSchedule().getTeacherId() != null) {
                for (int i = 0; i < teachers.size(); i++) {
                    if (teachers.get(i).getTeacherId().equals(shift.getSchedule().getTeacherId().getTeacherId())) {
                        spinnerTeacher.setSelection(i + 1);
                        break;
                    }
                }
            }
            builder.setNeutralButton("Xóa", (dialog, which) -> {
                scheduleDao.delete(shift.getSchedule().getScheduleId());
                Toast.makeText(this, "Đã xóa tiết học", Toast.LENGTH_SHORT).show();
                loadScheduleForDate();
            });
        }

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String activityName = etActivityName.getText().toString().trim();
            if (activityName.isEmpty()) {
                Toast.makeText(this, "Tên hoạt động không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher selectedTeacher = null;
            if (spinnerTeacher.getSelectedItemPosition() > 0) {
                selectedTeacher = teachers.get(spinnerTeacher.getSelectedItemPosition() - 1);
            }

            // Dùng lại schedule cũ nếu là sửa, hoặc tạo mới nếu là thêm
            Schedule scheduleToSave = shift.getSchedule() != null ? shift.getSchedule() : new Schedule();
            // Lấy classId từ biến toàn cục
            com.example.project_prm392_kidmanagement.Entity.Class currentClass = new com.example.project_prm392_kidmanagement.Entity.Class();
            currentClass.setClassId(classId);
            scheduleToSave.setClassId(currentClass);

            scheduleToSave.setActivityName(activityName);
            scheduleToSave.setTeacherId(selectedTeacher);
            scheduleToSave.setActivityDate(selectedDate);
            scheduleToSave.setShift(shift.getShiftNumber());

            if (shift.getSchedule() != null) {
                // TODO: Viết hàm update trong ScheduleDao
                // boolean success = scheduleDao.update(scheduleToSave);
                Toast.makeText(this, "Chức năng sửa đang phát triển", Toast.LENGTH_SHORT).show();
            } else {
                long result = scheduleDao.insert(scheduleToSave);
                if (result != -1) {
                    Toast.makeText(this, "Đã thêm tiết học", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm tiết học thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            loadScheduleForDate();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year1, monthOfYear, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sdf.format(selectedCal.getTime());
                    etSelectedDate.setText(selectedDate);
                    loadScheduleForDate();
                }, year, month, day);
        datePickerDialog.show();
    }

    private List<Shift> createEmptyShifts() {
        List<Shift> list = new ArrayList<>();
        list.add(new Shift(1, "Ca 1 (Sáng 1)"));
        list.add(new Shift(2, "Ca 2 (Sáng 2)"));
        list.add(new Shift(3, "Ca 3 (Chiều 1)"));
        list.add(new Shift(4, "Ca 4 (Chiều 2)"));
        return list;
    }
}