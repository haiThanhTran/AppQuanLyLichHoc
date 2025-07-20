package com.example.project_prm392_kidmanagement;

import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.DAO.ParentDao;
import com.example.project_prm392_kidmanagement.DAO.StudentDao;
import com.example.project_prm392_kidmanagement.Entity.Parent;
import com.example.project_prm392_kidmanagement.Entity.Student;
import com.example.project_prm392_kidmanagement.adapter.StudentAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminStudentManagementActivity extends AppCompatActivity {

    private Spinner spinnerParent;
    private EditText etFullName, etAddress, etDob;
    private Button btnSubmit;
    private List<Parent> parentList = new ArrayList<>();
    private Parent selectedParent; // giữ lại parent đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_student_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerParent = findViewById(R.id.spinnerParent);
        etFullName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etAddress);
        etDob = findViewById(R.id.etDob);
        btnSubmit = findViewById(R.id.btnSubmit);

        ParentDao parentDao = new ParentDao(this);
        StudentDao studentDao = new StudentDao(this);

        parentList = parentDao.getAll();

        List<String> parentNames = new ArrayList<>();
        for (Parent p : parentList) {
            parentNames.add(p.getFullName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, parentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParent.setAdapter(adapter);

        spinnerParent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedParent = parentList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedParent = null;
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String dob = etDob.getText().toString().trim();

            if (selectedParent == null || fullName.isEmpty() || address.isEmpty() || dob.isEmpty()) {
                return;
            }

            Student newStudent = new Student(
                    UUID.randomUUID().toString(),
                    selectedParent,
                    fullName,
                    address,
                    dob
            );
            studentDao.insert(newStudent);
            finish();

        });
        upgrade(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            upgrade(this);
        }
    }

    public void upgrade(AppCompatActivity app) {
        StudentDao studentDao = new StudentDao(this);
        RecyclerView recyclerView = findViewById(R.id.listStudent);
        List<Student> students = studentDao.getAll();
        for (Student student : students) {
            Log.d("Student", student.getFullName());
        }
        StudentAdapter adapterStudent = new StudentAdapter(students,app);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterStudent);
    }
}
