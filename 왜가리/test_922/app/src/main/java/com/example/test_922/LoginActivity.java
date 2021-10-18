package com.example.test_922;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();

    private EditText mEmail ,mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail=findViewById(R.id.login_email);
        mPassword=findViewById(R.id.login_password);
        findViewById(R.id.login_signup).setOnClickListener(this);
        findViewById(R.id.login_success).setOnClickListener(this);
    }
/*
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    } // 자동 로그인
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_signup:
                startActivity(new Intent(this,SignupActivity.class)); // 넘기기
                break;

            case R.id.login_success:
                mAuth.signInWithEmailAndPassword(mEmail.getText().toString() ,mPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser(); // 로그인 성공
                                if(user != null) {
                                    //Toast.makeText(LoginActivity.this,"로그인 성공"+user.getUid(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                }
                            }else{
                                Toast.makeText(LoginActivity.this,"Login error", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }
}