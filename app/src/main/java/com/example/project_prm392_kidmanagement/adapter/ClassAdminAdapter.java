package com.example.project_prm392_kidmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.DAO.StudentToClassDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.R;
import java.util.List;

public class ClassAdminAdapter extends RecyclerView.Adapter<ClassAdminAdapter.ClassViewHolder> {

    private List<Class> classList;
    private OnClassListener onClassListener;
    private StudentToClassDao studentToClassDao;

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
        // Khởi tạo studentToClassDao một lần duy nhất ở đây
        if (studentToClassDao == null) {
            studentToClassDao = new StudentToClassDao(parent.getContext());
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_admin, parent, false);
        return new ClassViewHolder(view, onClassListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class currentClass = classList.get(position);
        holder.tvClassName.setText("Tên lớp: " + currentClass.getClassName());

        if (currentClass.getTeacherId() != null && currentClass.getTeacherId().getFullName() != null) {
            holder.tvTeacherName.setText("GVCN: " + currentClass.getTeacherId().getFullName());
        } else {
            holder.tvTeacherName.setText("GVCN: Chưa có");
        }

        // Đếm sĩ số học sinh cho lớp này
        int studentCount = studentToClassDao.countStudentsInClass(currentClass.getClassId());
        holder.tvStudentCount.setText("Sĩ số: " + studentCount + " học sinh");
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    // Giao diện (interface) để xử lý sự kiện click
    public interface OnClassListener {
        void onClassClick(int position);  // Sự kiện khi click vào cả item
        void onDeleteClick(int position); // Sự kiện khi click vào nút Xóa
    }

    // ViewHolder
    class ClassViewHolder extends RecyclerView.ViewHolder {
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

            // Ẩn nút "Sửa" vì chúng ta sẽ click vào cả item để xem chi tiết
            btnEdit.setVisibility(View.GONE);

            // Bắt sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                if(onClassListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onClassListener.onClassClick(position);
                    }
                }
            });

            // Bắt sự kiện click cho nút Xóa
            btnDelete.setOnClickListener(v -> {
                if(onClassListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onClassListener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}