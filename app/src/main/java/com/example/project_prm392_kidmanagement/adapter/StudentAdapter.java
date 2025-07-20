package com.example.project_prm392_kidmanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.DAO.StudentDao;
import com.example.project_prm392_kidmanagement.Entity.Student;
import com.example.project_prm392_kidmanagement.R;
import com.example.project_prm392_kidmanagement.UpgradeStudentActivity;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private AppCompatActivity appCompatActivity;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    public StudentAdapter(List<Student> studentList, AppCompatActivity appCompatActivity) {
        this.studentList = studentList;
        this.appCompatActivity = appCompatActivity;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvName.setText(student.getFullName());
        holder.tvDob.setText(student.getDob());
        holder.tvAddress.setText(student.getAddress());
        holder.tvParent.setText("Parent: " + student.getParentId().getFullName());
        StudentDao studentDao = new StudentDao(holder.itemView.getContext());

        holder.upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), UpgradeStudentActivity.class);
                intent.putExtra("studentId", student.getStudentId());
                holder.itemView.getContext().startActivity(intent);
                appCompatActivity.startActivityForResult(intent, 100);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentDao.delete(student.getStudentId());
                studentList.remove(student);
                notifyDataSetChanged();
            }
    });
    }
    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDob, tvAddress, tvParent;
        Button upgrade,delete;
        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvDob = itemView.findViewById(R.id.tvStudentDob);
            tvAddress = itemView.findViewById(R.id.tvStudentAddress);
            tvParent = itemView.findViewById(R.id.tvStudentParent);
            upgrade = itemView.findViewById(R.id.upgrade);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
