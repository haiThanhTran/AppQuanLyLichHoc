package com.example.project_prm392_kidmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_prm392_kidmanagement.DAO.AccountDao;
import com.example.project_prm392_kidmanagement.Entity.Account;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

public class AccountManagerActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView btnSignUp, forgotPassword;

    private AccountDao accountDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        forgotPassword = findViewById(R.id.forgotPassword);

        accountDao = new AccountDao(this);

        // --- ĐOẠN CODE ĐĂNG NHẬP ĐÃ SỬA LẠI ---
        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra đăng nhập
            if (accountDao.validate(username, password)) {
                // Lấy thông tin đầy đủ của tài khoản
                Account account = accountDao.getByUsername(username);

                if (account == null) {
                    // Trường hợp hiếm gặp: validate đúng nhưng không lấy được user
                    Toast.makeText(this, "Lỗi: Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                // Phân luồng dựa trên vai trò (role)
                int role = account.isRole(); // 0 = Parent, 1 = Teacher/Admin

                Intent intent;

                if (role == 1) { // Đây là vai trò của Teacher hoặc Admin
                    // Cần kiểm tra xem có teacherId không để phân biệt Teacher và Admin
                    if (account.getTeacherId() != null) {
                        // Đây là Teacher
                        intent = new Intent(this, TeacherHomeManagerActivity.class);
                        String teacherId = account.getTeacherId().getTeacherId();
                        intent.putExtra("teacherId", teacherId);
                        Log.d("LOGIN_DEBUG", "Redirecting to TeacherHome. Teacher ID: " + teacherId);

                    } else {
                        // ĐÂY LÀ ADMIN, CHUYỂN ĐẾN MÀN HÌNH ADMIN MỚI
                        intent = new Intent(this, AdminHomeActivity.class);
                        intent.putExtra("adminUsername", account.getUsername()); // Gửi username để chào mừng
                    }

                } else { // role == 0, đây là Parent
                    if (account.getParentId() != null) {
                        intent = new Intent(this, ParentHomeManagerActivity.class);
                        String parentId = account.getParentId().getParentId();
                        intent.putExtra("parentId", parentId); // Gửi ID dạng String cho nhất quán
                        Log.d("LOGIN_DEBUG", "Redirecting to ParentHome. Parent ID: " + parentId);
                    } else {
                        // Trường hợp lỗi: role là parent nhưng không có parentId
                        Toast.makeText(this, "Lỗi: Tài khoản phụ huynh không hợp lệ", Toast.LENGTH_SHORT).show();
                        return; // Không chuyển trang
                    }
                }

                startActivity(intent);
                finish(); // Đóng màn hình đăng nhập

            } else {
                Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(view -> {
            Toast.makeText(this, "Chức năng đăng ký chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        forgotPassword.setOnClickListener(view -> {
            Toast.makeText(this, "Vui lòng liên hệ giáo viên hoặc quản trị viên để lấy lại mật khẩu", Toast.LENGTH_SHORT).show();
        });
    }
}