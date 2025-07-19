package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.Entity.Class;

public class AdminClassDetailActivity extends AppCompatActivity {
    private TextView tvClassNameTitle;
    private Button btnManageInfo, btnManageStudents, btnManageSchedule;

    private ClassDao classDao;
    private Class currentClass;
    private String classId;

    public String getClassId() {
        classId = getIntent().getStringExtra("CLASS_ID");
        return classId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_class_detail);

       try {
           String id = getClassId();
           Toast.makeText(this, "Lớp ID: " + id, Toast.LENGTH_SHORT).show();
       }catch (Exception e) {
           Toast.makeText(this, "Không tìm thấy lớp", Toast.LENGTH_SHORT).show();
       }

        initViews();
        classDao = new ClassDao(this);
        loadData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load lại dữ liệuเผื่อ có thay đổi từ màn hình con
        loadData();
    }

    private void initViews() {
        tvClassNameTitle = findViewById(R.id.tvClassNameTitle);
//        btnManageInfo = findViewById(R.id.btnManageInfo);
        btnManageStudents = findViewById(R.id.btnManageStudents);
        btnManageSchedule = findViewById(R.id.btnManageSchedule);
    }

    private void loadData() {
        currentClass = classDao.getById(classId);
        if (currentClass != null) {
            tvClassNameTitle.setText("Quản lý Lớp: " + currentClass.getClassName());
            setTitle("Lớp " + currentClass.getClassName());
        }
    }

    private void setupListeners() {


        btnManageStudents.setOnClickListener(v -> {
            // TODO: Mở màn hình quản lý sĩ số lớp
            Toast.makeText(this, "Mở màn hình Quản lý sĩ số", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AddStudentActivity.class);
            intent.putExtra("CLASS_ID", classId);
            startActivity(intent);
        });

        btnManageSchedule.setOnClickListener(v -> {
            // Mở màn hình Set TKB
            Intent intent = new Intent(AdminClassDetailActivity.this, AdminSetScheduleActivity.class);
            intent.putExtra("CLASS_ID", classId);
            intent.putExtra("CLASS_NAME", currentClass.getClassName());
            startActivity(intent);
        });
    }
}