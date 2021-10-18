package com.example.markbooks.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markbooks.MainActivity;
import com.example.markbooks.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        forgotTextLink = findViewById(R.id.forgotPassword);

        mLoginBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                mPassword.setError("Password Must be >= 6 Characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // authenticate the user
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Success !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    //Toast.makeText(Login.this,"로그인 실패",Toast.LENGTH_LONG).show();
                }
            });
        });

        mCreateBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        forgotTextLink.setOnClickListener(v -> {
            EditText resetMail = new EditText(v.getContext());
            resetMail.setSingleLine();
            AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(v.getContext());
            passwordRestDialog.setTitle("비밀번호를 초기화 하시겠습니까?");
            passwordRestDialog.setMessage("비밀번호 재설정 링크를 받을 이메일을 입력해주세요.");
            passwordRestDialog.setView(resetMail);

            passwordRestDialog.setPositiveButton(Html.fromHtml("<font color='#000000'>네</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //이메일 재설정

                    String mail = resetMail.getText().toString();
                    if (mail.isEmpty() || mail == null) {
                        Toast.makeText(LoginActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused -> Toast.makeText(LoginActivity.this, "링크를 전송했습니다.", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "링크를 전송하지 못했습니다." + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
            passwordRestDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>아니오</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {

                }
            });

            passwordRestDialog.create().show();
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }
}