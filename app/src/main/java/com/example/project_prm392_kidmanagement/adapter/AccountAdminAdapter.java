package com.example.project_prm392_kidmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.Entity.Account;
import com.example.project_prm392_kidmanagement.R;
import java.util.List;

public class AccountAdminAdapter extends RecyclerView.Adapter<AccountAdminAdapter.AccountViewHolder> {

    private List<Account> accountList;
    private OnAccountListener onAccountListener;
    private Context context;

    public AccountAdminAdapter(Context context, List<Account> accountList, OnAccountListener onAccountListener) {
        this.context = context;
        this.accountList = accountList;
        this.onAccountListener = onAccountListener;
    }

    public void updateData(List<Account> newList) {
        this.accountList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_admin, parent, false);
        return new AccountViewHolder(view, onAccountListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.tvUsername.setText("Username: " + account.getUsername());
        holder.tvEmail.setText("Email: " + account.getEmail());

        // Set role và background tương ứng
        int role = account.isRole();
        if (role == 1 && account.getTeacherId() != null) {
            holder.tvRole.setText("Teacher");
            holder.tvRole.setBackground(ContextCompat.getDrawable(context, R.drawable.role_background_teacher));
            holder.tvLinkedTo.setText("Liên kết: GV " + account.getTeacherId().getTeacherId());
        } else if (role == 1 && account.getTeacherId() == null) {
            holder.tvRole.setText("Admin");
            holder.tvRole.setBackground(ContextCompat.getDrawable(context, R.drawable.role_background_admin));
            holder.tvLinkedTo.setText("Liên kết: (Không có)");
        } else { // role == 0 or 2
            holder.tvRole.setText("Parent");
            holder.tvRole.setBackground(ContextCompat.getDrawable(context, R.drawable.role_background_parent));
            if (account.getParentId() != null) {
                holder.tvLinkedTo.setText("Liên kết: PH " + account.getParentId().getParentId());
            } else {
                holder.tvLinkedTo.setText("Liên kết: (Chưa có)");
            }
        }
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvUsername, tvEmail, tvRole, tvLinkedTo;
        Button btnEditAccount, btnDeleteAccount;
        OnAccountListener onAccountListener;

        public AccountViewHolder(@NonNull View itemView, OnAccountListener onAccountListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvLinkedTo = itemView.findViewById(R.id.tvLinkedTo);
            btnEditAccount = itemView.findViewById(R.id.btnEditAccount);
            btnDeleteAccount = itemView.findViewById(R.id.btnDeleteAccount);
            this.onAccountListener = onAccountListener;

            btnEditAccount.setOnClickListener(this);
            btnDeleteAccount.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.btnEditAccount) {
                    onAccountListener.onEditClick(position);
                } else if (v.getId() == R.id.btnDeleteAccount) {
                    onAccountListener.onDeleteClick(position);
                }
            }
        }
    }

    public interface OnAccountListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}