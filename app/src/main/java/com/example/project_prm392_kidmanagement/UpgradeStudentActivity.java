package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392_kidmanagement.DAO.ParentDao;
import com.example.project_prm392_kidmanagement.DAO.StudentDao;
import com.example.project_prm392_kidmanagement.Entity.Parent;

import java.util.ArrayList;
import java.util.List;

public class UpgradeStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upgrade_student2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        StudentDao studentDao = new StudentDao(this);
        ParentDao parentDao = new ParentDao(this);
        List<Parent> parentList = parentDao.getAll();
        List<String> parentNames = new ArrayList<>();
        var student = studentDao.getById(intent.getStringExtra("studentId"));
        EditText fullName = findViewById(R.id.etFullName);
        EditText address = findViewById(R.id.etAddress);
        EditText dob = findViewById(R.id.etDob);
        if (student != null) {
            fullName.setText(student.getFullName());
            address.setText(student.getAddress());
            dob.setText(student.getDob());
        }
        Spinner parent = findViewById(R.id.spinnerParent);
        for (Parent p : parentList) {
            parentNames.add(p.getFullName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, parentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parent.setAdapter(adapter);
        int selectedIndex = 0;
        for (int i = 0; i < parentList.size(); i++) {
            if (parentList.get(i).getParentId().equals(student.getParentId().getParentId())) {
                selectedIndex = i;
                break;
            }
        }
        parent.setSelection(selectedIndex);
        Button sumbit = findViewById(R.id.btnSubmit);
        sumbit.setOnClickListener(v -> {
            student.setFullName(fullName.getText().toString());
            student.setAddress(address.getText().toString());
            student.setDob(dob.getText().toString());
            student.setParentId(parentList.get(parent.getSelectedItemPosition()));
            studentDao.update(student);
            finish();
        });
        }
}