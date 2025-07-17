package com.example.project_prm392_kidmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.R;
import java.util.List;

public class ClassAdminAdapter extends RecyclerView.Adapter<ClassAdminAdapter.ClassViewHolder> {

    private List<Class> classList;
    private OnClassListener onClassListener;

    public ClassAdminAdapter(List<Class> classList, OnClassListener onClassListener) {
        this.classList = classList;
        this.onClassListener = onClassListener;
    }

    public void updateData(List<Class> newList) {
        this.classList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_admin, parent, false);
        return new ClassViewHolder(view, onClassListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class currentClass = classList.get(position);
        holder.tvClassName.setText("Tên lớp: " + currentClass.getClassName());

        // Hiển thị tên giáo viên nếu có
        if (currentClass.getTeacherId() != null && currentClass.getTeacherId().getFullName() != null) {
            holder.tvTeacherName.setText("GVCN: " + currentClass.getTeacherId().getFullName());
        } else {
            holder.tvTeacherName.setText("GVCN: Chưa có");
        }

        // TODO: Cần có logic để đếm sĩ số học sinh cho lớp này
        holder.tvStudentCount.setText("Sĩ số: (chưa có)");
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvClassName, tvTeacherName, tvStudentCount;
        Button btnEdit, btnDelete;
        OnClassListener onClassListener;

        public ClassViewHolder(@NonNull View itemView, OnClassListener onClassListener) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            this.onClassListener = onClassListener;

            btnEdit.setOnClickListener(this);

            btnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnEdit) {
                onClassListener.onEditClick(getAdapterPosition());
            } else if (v.getId() == R.id.btnDelete) {
                onClassListener.onDeleteClick(getAdapterPosition());
            }
        }
    }

    public interface OnClassListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}