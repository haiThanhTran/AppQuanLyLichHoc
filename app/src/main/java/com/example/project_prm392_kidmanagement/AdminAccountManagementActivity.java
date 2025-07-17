package com.example.project_prm392_kidmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.DAO.AccountDao;
import com.example.project_prm392_kidmanagement.Entity.Account;
import com.example.project_prm392_kidmanagement.adapter.AccountAdminAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminAccountManagementActivity extends AppCompatActivity implements AccountAdminAdapter.OnAccountListener {

    private RecyclerView rvAccountList;
    private Button btnAddAccount;
    private SearchView searchView;
    private Spinner spinnerFilterRole;

    private AccountDao accountDao;
    private AccountAdminAdapter adapter;
    private List<Account> accountList;

    private String currentSearchQuery = "";
    private int currentRoleFilter = -1; // -1 for "All"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_account_management);
        setTitle("Quản lý Tài khoản");

        initViews();
        accountDao = new AccountDao(this);

        setupRecyclerView();
        setupFilterSpinner();
        setupSearchView();
        loadAccountData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccountData(); // Load lại dữ liệu mỗi khi quay lại
    }

    private void initViews() {
        rvAccountList = findViewById(R.id.rvAccountList);
        btnAddAccount = findViewById(R.id.btnAddAccount);
        searchView = findViewById(R.id.searchView);
        spinnerFilterRole = findViewById(R.id.spinnerFilterRole);
    }

    private void setupRecyclerView() {
        accountList = new ArrayList<>();
        adapter = new AccountAdminAdapter(this, accountList, this);
        rvAccountList.setLayoutManager(new LinearLayoutManager(this));
        rvAccountList.setAdapter(adapter);
    }

    private void setupFilterSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterRole.setAdapter(adapter);

        spinnerFilterRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position: 0=All, 1=Admin, 2=Teacher, 3=Parent
                if (position == 0) currentRoleFilter = -1; // All
                else if (position == 1) currentRoleFilter = 2; // Admin (mapping to our DAO logic)
                else if (position == 2) currentRoleFilter = 1; // Teacher
                else if (position == 3) currentRoleFilter = 0; // Parent

                loadAccountData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                loadAccountData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                loadAccountData();
                return true;
            }
        });
    }

    private void loadAccountData() {
        // Dùng hàm search và filter mới
        accountList = accountDao.searchAndFilter(currentSearchQuery, currentRoleFilter);
        adapter.updateData(accountList);
    }

    private void setupListeners() {
        btnAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAddEditAccountActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onEditClick(int position) {
        Account selectedAccount = accountList.get(position);
        Intent intent = new Intent(this, AdminAddEditAccountActivity.class);
        intent.putExtra("ACCOUNT_ID", selectedAccount.getAccountId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        Account selectedAccount = accountList.get(position);

        if (selectedAccount.getUsername().equals("admin")) {
            Toast.makeText(this, "Không thể xóa tài khoản Admin mặc định", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tài khoản '" + selectedAccount.getUsername() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean isDeleted = accountDao.delete(selectedAccount.getAccountId());
                    if (isDeleted) {
                        Toast.makeText(this, "Đã xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                        loadAccountData();
                    } else {
                        Toast.makeText(this, "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}