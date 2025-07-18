package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.DAO.StudentDao;
import com.example.project_prm392_kidmanagement.DAO.StudentToClassDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.Entity.Student;
import com.example.project_prm392_kidmanagement.Entity.StudentToClass;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddStudentActivity extends AppCompatActivity {
    private LinearLayout checkboxContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addStudent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkboxContainer = findViewById(R.id.checkbox_container);
        StudentDao dao = new StudentDao(this);
        ClassDao classDao = new ClassDao(this);
        StudentToClassDao studentToClassDao = new StudentToClassDao(this);
        List<Student> studentList = dao.getAll();
        List<StudentToClass> studentToClassList = studentToClassDao.getAll();

        for (Student student : studentList) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(student.getFullName());
            checkBox.setTag(student.getStudentId());
            for (StudentToClass studentToClass : studentToClassList) {
                if(studentToClass.getStudentId().getStudentId().equals(student.getStudentId())) {
                    checkBox.setChecked(true);
                    break;
                }
            }
            checkboxContainer.addView(checkBox);
        }
        Intent intent = getIntent();
        String classId = intent.getStringExtra("CLASS_ID");
        Button add = findViewById(R.id.btnAddStudent);
        add.setOnClickListener(v -> {
            List<Integer> selectedStudentIds = new ArrayList<>();
            // Lặp qua tất cả các CheckBox trong container
            int childCount = checkboxContainer.getChildCount();
            for (int i = 0; i < childCount; i++) {
                CheckBox checkBox = (CheckBox) checkboxContainer.getChildAt(i);
                if (checkBox.isChecked()) {
                    String studentId = (String) checkBox.getTag();
                    Student student = dao.getById(studentId);
                    Class classroom = classDao.getById(classId);
                    Toast.makeText(this, student.getFullName() + classroom.getClassName(), Toast.LENGTH_SHORT).show();
                    try {
                        studentToClassDao.insert(new StudentToClass(student,classroom));
                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(this, "error : " +e.getMessage() , Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

}