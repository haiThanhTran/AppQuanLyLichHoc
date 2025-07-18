package com.example.project_prm392_kidmanagement;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
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
    private LinearLayout layoutTeacherInfo, layoutParentInfo;
    private EditText edtTeacherName, edtTeacherAddress, edtTeacherPhone, edtTeacherDob;
    private EditText edtParentName, edtParentAddress, edtParentPhone, edtParentDob;

    private AccountDao accountDao;
    private TeacherDao teacherDao;
    private ParentDao parentDao;

    private String mode;
    private int accountId = -1;
    private Account currentAccount;

 //   @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.admin_add_edit_account);
//
//        initDAOs();
//        initViews();
//        setupRoleSelectionListener();
//
//        mode = getIntent().getStringExtra("MODE");
//        if ("EDIT".equals(mode)) {
//            accountId = getIntent().getIntExtra("ACCOUNT_ID", -1);
//            loadAccountData(accountId);
//        }
//
//        btnSaveAccount.setOnClickListener(v -> {
//            if ("EDIT".equals(mode)) {
//                updateUser();
//            } else {
//                saveNewUser();
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_edit_account);

        initDAOs();
        initViews();
        setupRoleSelectionListener();

        // Xác định chế độ thêm hay sửa
        mode = getIntent().getStringExtra("MODE");
        if ("EDIT".equals(mode)) {
            tvTitle.setText("Sửa thông tin tài khoản");
            accountId = getIntent().getIntExtra("ACCOUNT_ID", -1);

            if (accountId != -1) {
                loadAccountData(accountId);  // Nạp dữ liệu vào form để chỉnh sửa
            } else {
                Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản cần sửa", Toast.LENGTH_SHORT).show();
                finish(); // Thoát nếu lỗi
            }
        } else {
            tvTitle.setText("Thêm người dùng mới");
        }

        btnSaveAccount.setOnClickListener(v -> {
            if ("EDIT".equals(mode)) {
                updateUser();
            } else {
                saveNewUser();
            }
        });
    }


    private void initDAOs() {
        accountDao = new AccountDao(this);
        teacherDao = new TeacherDao(this);
        parentDao = new ParentDao(this);
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
        layoutTeacherInfo = findViewById(R.id.layout_teacher_info);
        layoutParentInfo = findViewById(R.id.layout_parent_info);

        edtTeacherName = findViewById(R.id.edtTeacherName);
        edtTeacherAddress = findViewById(R.id.edtTeacherAddress);
        edtTeacherPhone = findViewById(R.id.edtTeacherPhone);
        edtTeacherDob = findViewById(R.id.edtTeacherDob);

        edtParentName = findViewById(R.id.edtParentName);
        edtParentAddress = findViewById(R.id.edtParentAddress);
        edtParentPhone = findViewById(R.id.edtParentPhone);
        edtParentDob = findViewById(R.id.edtParentDob);
    }

    private void setupRoleSelectionListener() {
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            layoutTeacherInfo.setVisibility(View.GONE);
            layoutParentInfo.setVisibility(View.GONE);
            if (checkedId == R.id.rbTeacher) {
                layoutTeacherInfo.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbParent) {
                layoutParentInfo.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadAccountData(int id) {
        currentAccount = accountDao.getById(id);
        if (currentAccount == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText("Chỉnh sửa tài khoản");
        etUsername.setText(currentAccount.getUsername());
        etPassword.setText(currentAccount.getPassword());
        etEmail.setText(currentAccount.getEmail());

        etUsername.setEnabled(false); // Không cho sửa username

        int role = currentAccount.getRole();
        if (role == 1) {
            rbAdmin.setChecked(true);
        } else if (role == 2) {
            rbTeacher.setChecked(true);
            layoutTeacherInfo.setVisibility(View.VISIBLE);
            Teacher teacher = currentAccount.getTeacherId();
            if (teacher != null) {
                edtTeacherName.setText(teacher.getFullName());
                edtTeacherAddress.setText(teacher.getAddress());
                edtTeacherPhone.setText(teacher.getPhone());
                edtTeacherDob.setText(teacher.getDob());
            }
        } else if (role == 0) {
            rbParent.setChecked(true);
            layoutParentInfo.setVisibility(View.VISIBLE);
            Parent parent = currentAccount.getParentId();
            if (parent != null) {
                edtParentName.setText(parent.getFullName());
                edtParentAddress.setText(parent.getAddress());
                edtParentPhone.setText(parent.getPhone());
                edtParentDob.setText(parent.getDob());
            }
        }
    }

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
        } else if (selectedRoleId == R.id.rbTeacher) {
            createTeacherAndAccount(username, password, email);
        } else if (selectedRoleId == R.id.rbParent) {
            createParentAndAccount(username, password, email);
        }
    }

    private void updateUser() {
        if (currentAccount == null) return;

        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int selectedRoleId = rgRole.getCheckedRadioButtonId();

        if (password.isEmpty() || email.isEmpty() || selectedRoleId == -1) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        currentAccount.setPassword(password);
        currentAccount.setEmail(email);

        if (selectedRoleId == R.id.rbAdmin) {
            currentAccount.setRole(1);
            currentAccount.setTeacherId(null);
            currentAccount.setParentId(null);
        } else if (selectedRoleId == R.id.rbTeacher) {
            String fullName = edtTeacherName.getText().toString().trim();
            String address = edtTeacherAddress.getText().toString().trim();
            String phone = edtTeacherPhone.getText().toString().trim();
            String dob = edtTeacherDob.getText().toString().trim();

            if (fullName.isEmpty() || address.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin giáo viên", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher teacher = currentAccount.getTeacherId();
            if (teacher == null) {
                String newId = "GV" + String.format("%03d", teacherDao.count() + 2);
                teacher = new Teacher(newId, fullName, address, phone, dob);
                teacherDao.insert(teacher);
            } else {
                teacher.setFullName(fullName);
                teacher.setAddress(address);
                teacher.setPhone(phone);
                teacher.setDob(dob);
                teacherDao.update(teacher);
            }
            currentAccount.setTeacherId(teacher);
            currentAccount.setParentId(null);
            currentAccount.setRole(2);
        } else if (selectedRoleId == R.id.rbParent) {
            String fullName = edtParentName.getText().toString().trim();
            String address = edtParentAddress.getText().toString().trim();
            String phone = edtParentPhone.getText().toString().trim();
            String dob = edtParentDob.getText().toString().trim();

            if (fullName.isEmpty() || address.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin phụ huynh", Toast.LENGTH_SHORT).show();
                return;
            }

            Parent parent = currentAccount.getParentId();
            if (parent == null) {
                String newId = "PH" + String.format("%03d", parentDao.count() + 2);
                parent = new Parent(newId, fullName, address, phone, dob);
                parentDao.insert(parent);
            } else {
                parent.setFullName(fullName);
                parent.setAddress(address);
                parent.setPhone(phone);
                parent.setDob(dob);
                parentDao.update(parent);
            }
            currentAccount.setParentId(parent);
            currentAccount.setTeacherId(null);
            currentAccount.setRole(0);
        }

        if (accountDao.update(currentAccount)) {
            Toast.makeText(this, "Cập nhật tài khoản thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    // Các hàm tạo mới giữ nguyên như trước:
    private void createAdminAccount(String username, String password, String email) {
        Account account = new Account(username, password, email, 1, null, null);
        if (accountDao.insert(account) != -1) {
            Toast.makeText(this, "Tạo tài khoản Admin thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createTeacherAndAccount(String username, String password, String email) {
        String id = "GV" + String.format("%03d", teacherDao.count() + 2);
        Teacher t = new Teacher(id, edtTeacherName.getText().toString(), edtTeacherAddress.getText().toString(), edtTeacherPhone.getText().toString(), edtTeacherDob.getText().toString());
        if (teacherDao.insert(t) != -1) {
            Account acc = new Account(username, password, email, 2, t, null);
            if (accountDao.insert(acc) != -1) {
                Toast.makeText(this, "Tạo tài khoản Giáo viên thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void createParentAndAccount(String username, String password, String email) {
        String id = "PH" + String.format("%03d", parentDao.count() + 2);
        Parent p = new Parent(id, edtParentName.getText().toString(), edtParentAddress.getText().toString(), edtParentPhone.getText().toString(), edtParentDob.getText().toString());
        if (parentDao.insert(p) != -1) {
            Account acc = new Account(username, password, email, 0, null, p);
            if (accountDao.insert(acc) != -1) {
                Toast.makeText(this, "Tạo tài khoản Phụ huynh thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
