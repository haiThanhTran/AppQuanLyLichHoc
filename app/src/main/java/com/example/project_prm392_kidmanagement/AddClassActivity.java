package com.example.project_prm392_kidmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.Entity.Teacher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addClass), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText name = findViewById(R.id.addClassName);
        Spinner spinnerTeacher = findViewById(R.id.spinnerTeacher);
        TeacherDao teacherDao = new TeacherDao(this);
        List<Teacher> list =  teacherDao.getAll();
        ArrayAdapter<Teacher> teacherArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list){
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(list.get(position).getFullName());
                return view;
            }
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(getItem(position).getFullName());
                return view;
            }
        };
        teacherArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherArrayAdapter);
        spinnerTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected: " + list.get(position).getTeacherId().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Must selected teacher", Toast.LENGTH_SHORT).show();
            }
        });
        Button button = findViewById(R.id.btnSaveClass);
        ClassDao classDao = new ClassDao(this);
        button.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String nameClass = name.getText().toString();
            String year = String.valueOf(new Date().getYear());
            String teacherId = list.get(spinnerTeacher.getSelectedItemPosition()).getTeacherId();
            Teacher teacher = teacherDao.getById(teacherId);
            try {
                classDao.insert(new Class(id,nameClass,year,teacher));
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finish();
        });
}
}