package com.example.project_prm392_kidmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392_kidmanagement.DAO.ClassDao;
import com.example.project_prm392_kidmanagement.Entity.Class;
import com.example.project_prm392_kidmanagement.adapter.ClassAdminAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements ClassAdminAdapter.OnClassListener {

    private TextView tvAdminWelcome;
    private RecyclerView rvClassList;
    private Button btnAddClass, btnLogout, btnManageAccounts, btnManageClasses;

    private ClassDao classDao;
    private ClassAdminAdapter adapter;
    private List<Class> classList;

    // REQUEST_CODE để nhận biết kết quả trả về từ activity thêm/sửa
    private static final int REQUEST_CODE_ADD_CLASS = 1;
    private static final int REQUEST_CODE_EDIT_CLASS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);

        initViews();
        classDao = new ClassDao(this);

        setupRecyclerView();
        loadClassData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại màn hình này
        loadClassData();
    }

    private void initViews() {
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        rvClassList = findViewById(R.id.rvClassList);
        btnAddClass = findViewById(R.id.btnAddClass);
        btnLogout = findViewById(R.id.btnLogout);
        btnManageAccounts = findViewById(R.id.btnManageAccounts);
        btnManageClasses = findViewById(R.id.btnManageClasses);

        String adminUsername = getIntent().getStringExtra("adminUsername");
        if (adminUsername != null) {
            tvAdminWelcome.setText("👑 Chào mừng, " + adminUsername + "!");
        }
    }

    private void setupRecyclerView() {
        classList = new ArrayList<>();
        adapter = new ClassAdminAdapter(classList, this);
        rvClassList.setLayoutManager(new LinearLayoutManager(this));
        rvClassList.setAdapter(adapter);
    }

    private void loadClassData() {
        classList = classDao.getAll(); // Giả sử ClassDao có hàm getAll()
        adapter.updateData(classList);
    }

    private void setupListeners() {
        btnAddClass.setOnClickListener(v -> {
            // TODO: Tạo Activity để thêm lớp (AdminAddClassActivity)
            Toast.makeText(this, "Chức năng Thêm lớp đang phát triển", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(AdminHomeActivity.this, AdminAddClassActivity.class);
            // startActivityForResult(intent, REQUEST_CODE_ADD_CLASS);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, AccountManagerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnManageAccounts.setOnClickListener(v -> {
            // Mở màn hình quản lý tài khoản
            Intent intent = new Intent(AdminHomeActivity.this, AdminAccountManagementActivity.class);
            startActivity(intent);
        });

        btnManageClasses.setOnClickListener(v -> {
            // Chỉ cần load lại dữ liệu
            loadClassData();
            Toast.makeText(this, "Đã làm mới danh sách lớp", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onEditClick(int position) {
        Class selectedClass = classList.get(position);
        Toast.makeText(this, "Sửa lớp: " + selectedClass.getClassName(), Toast.LENGTH_SHORT).show();
        // TODO: Tạo Activity để sửa lớp (AdminEditClassActivity)
        // Intent intent = new Intent(this, AdminEditClassActivity.class);
        // intent.putExtra("CLASS_ID", selectedClass.getClassId());
        // startActivityForResult(intent, REQUEST_CODE_EDIT_CLASS);
    }

    @Override
    public void onDeleteClick(int position) {
        Class selectedClass = classList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa lớp '" + selectedClass.getClassName() + "' không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean isDeleted = classDao.delete(selectedClass.getClassId()); // Giả sử có hàm delete bằng id
                    if (isDeleted) {
                        Toast.makeText(this, "Đã xóa lớp thành công", Toast.LENGTH_SHORT).show();
                        loadClassData(); // Load lại danh sách
                    } else {
                        Toast.makeText(this, "Xóa lớp thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Xử lý kết quả trả về từ các activity thêm/sửa
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD_CLASS || requestCode == REQUEST_CODE_EDIT_CLASS) {
                // Nếu thêm/sửa thành công, load lại danh sách
                loadClassData();
            }
        }
    }
}