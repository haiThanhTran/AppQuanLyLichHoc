package com.example.project_prm392_kidmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_prm392_kidmanagement.Entity.Shift;
import com.example.project_prm392_kidmanagement.R;
import java.util.List;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder> {

    private List<Shift> shiftList;
    private OnShiftInteractionListener listener;

    public ShiftAdapter(List<Shift> shiftList, OnShiftInteractionListener listener) {
        this.shiftList = shiftList;
        this.listener = listener;
    }

    public void updateData(List<Shift> newShifts) {
        this.shiftList = newShifts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        Shift shift = shiftList.get(position);
        holder.tvShiftName.setText(shift.getShiftName());

        if (shift.getSchedule() != null) {
            holder.tvActivityInfo.setText("📝 " + shift.getSchedule().getActivityName());
            if (shift.getSchedule().getTeacherId() != null) {
                holder.tvTeacherInfo.setText("👩‍🏫 GV: " + shift.getSchedule().getTeacherId().getFullName());
            } else {
                holder.tvTeacherInfo.setText("👩‍🏫 GV: (Không có)");
            }
            holder.btnSetActivity.setText("Sửa/Xóa");
        } else {
            holder.tvActivityInfo.setText("Chưa có hoạt động");
            holder.tvTeacherInfo.setText("");
            holder.btnSetActivity.setText("Thêm hoạt động");
        }

        holder.btnSetActivity.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetActivityClick(shift);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

    class ShiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvShiftName, tvActivityInfo, tvTeacherInfo;
        Button btnSetActivity;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShiftName = itemView.findViewById(R.id.tvShiftName);
            tvActivityInfo = itemView.findViewById(R.id.tvActivityInfo);
            tvTeacherInfo = itemView.findViewById(R.id.tvTeacherInfo);
            btnSetActivity = itemView.findViewById(R.id.btnSetActivity);
        }
    }

    public interface OnShiftInteractionListener {
        void onSetActivityClick(Shift shift);
    }
}