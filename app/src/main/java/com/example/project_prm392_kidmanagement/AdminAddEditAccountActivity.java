package com.example.project_prm392_kidmanagement;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_prm392_kidmanagement.DAO.AccountDao;
import com.example.project_prm392_kidmanagement.DAO.ParentDao;
import com.example.project_prm392_kidmanagement.DAO.TeacherDao;
import com.example.project_prm392_kidmanagement.Entity.Account;
import com.example.project_prm392_kidmanagement.Entity.Parent;
import com.example.project_prm392_kidmanagement.Entity.Teacher;
import com.google.android.material.textfield.TextInputEditText;

public class AdminAddEditAccountActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextInputEditText etUsername, etPassword, etEmail;
    private RadioGroup rgRole;
    private RadioButton rbAdmin, rbTeacher, rbParent;
    private Button btnSaveAccount;

    private LinearLayout layoutProfileInfo;
    private TextInputEditText etFullName, etAddress, etPhone, etDob;

    private AccountDao accountDao;
    private TeacherDao teacherDao;
    private ParentDao parentDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_edit_account);

        // Thứ tự này rất quan trọng
        initDAOs();
        initViews();
        setupRoleSelectionListener();

        // Đặt listener cho nút lưu ở cuối
        btnSaveAccount.setOnClickListener(v -> saveNewUser());
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        rgRole = findViewById(R.id.rgRole);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbTeacher = findViewById(R.id.rbTeacher);
        rbParent = findViewById(R.id.rbParent);
        btnSaveAccount = findViewById(R.id.btnSaveAccount);

        layoutProfileInfo = findViewById(R.id.layoutProfileInfo);
        etFullName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
    }

    private void initDAOs() {
        accountDao = new AccountDao(this);
        teacherDao = new TeacherDao(this);
        parentDao = new ParentDao(this);
    }

    private void setupRoleSelectionListener() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            // *** GIẢI PHÁP PHÒNG THỦ: KIỂM TRA NULL TRƯỚC KHI SỬ DỤNG ***
            if (layoutProfileInfo == null) {
                // Nếu layoutProfileInfo chưa được khởi tạo, không làm gì cả.
                // Điều này ngăn ngừa crash khi listener được gọi quá sớm.
                return;
            }

            // So sánh với ID tài nguyên, đây là cách làm đúng
            if (checkedId == R.id.rbTeacher || checkedId == R.id.rbParent) {
                layoutProfileInfo.setVisibility(View.VISIBLE);
            } else { // Admin hoặc không có gì được chọn
                layoutProfileInfo.setVisibility(View.GONE);
            }
        });
    }

    // Các hàm saveNewUser và các hàm create... giữ nguyên như cũ
    private void saveNewUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int selectedRoleId = rgRole.getCheckedRadioButtonId();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || selectedRoleId == -1) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (accountDao.getByUsername(username) != null) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRoleId == R.id.rbAdmin) {
            createAdminAccount(username, password, email);
        } else {
            // CHỈ LẤY DỮ LIỆU KHI LAYOUT ĐANG HIỂN THỊ
            if (layoutProfileInfo.getVisibility() != View.VISIBLE) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hồ sơ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy dữ liệu từ các trường NHIỆM VỤ AN TOÀN
            String fullName = etFullName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String dob = etDob.getText().toString().trim();

            if (fullName.isEmpty() || address.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hồ sơ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedRoleId == R.id.rbTeacher) {
                createTeacherAndAccount(username, password, email, fullName, address, phone, dob);
            } else {
                createParentAndAccount(username, password, email, fullName, address, phone, dob);
            }
        }
    }

    private void createAdminAccount(String username, String password, String email) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.setRole(1); // Role Admin
        account.setTeacherId(null);
        account.setParentId(null);

        long result = accountDao.insert(account);
        if (result != -1) {
            Toast.makeText(this, "Tạo tài khoản Admin thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Đặt kết quả thành công để activity trước có thể load lại
            finish();
        } else {
            Toast.makeText(this, "Tạo tài khoản Admin thất bại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createTeacherAndAccount(String username, String password, String email, String fullName, String address, String phone, String dob) {
        // Tạo ID mới cho giáo viên
        String newTeacherId = "GV" + String.format("%03d", teacherDao.count() + 1);

        Teacher newTeacher = new Teacher(newTeacherId, fullName, address, phone, dob);
        long teacherResult = teacherDao.insert(newTeacher);

        if (teacherResult == -1) {
            Toast.makeText(this, "Tạo hồ sơ giáo viên thất bại.", Toast.LENGTH_SHORT).show();
            return;
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.setRole(1); // Role Teacher
        account.setTeacherId(newTeacher);
        account.setParentId(null);

        long accountResult = accountDao.insert(account);
        if (accountResult != -1) {
            Toast.makeText(this, "Tạo người dùng Giáo viên thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Tạo tài khoản cho giáo viên thất bại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createParentAndAccount(String username, String password, String email, String fullName, String address, String phone, String dob) {
        // Tạo ID mới cho phụ huynh
        String newParentId = "PH" + String.format("%03d", parentDao.count() + 1);

        Parent newParent = new Parent(newParentId, fullName, address, phone, dob);
        long parentResult = parentDao.insert(newParent);

        if (parentResult == -1) {
            Toast.makeText(this, "Tạo hồ sơ phụ huynh thất bại.", Toast.LENGTH_SHORT).show();
            return;
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.setRole(0); // Role Parent
        account.setParentId(newParent);
        account.setTeacherId(null);

        long accountResult = accountDao.insert(account);
        if (accountResult != -1) {
            Toast.makeText(this, "Tạo người dùng Phụ huynh thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Tạo tài khoản cho phụ huynh thất bại.", Toast.LENGTH_SHORT).show();
        }
    }
}