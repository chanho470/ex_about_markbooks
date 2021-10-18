package com.example.test_923;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mNicname, mEmailText ,mPasswordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mNicname=findViewById(R.id.sign_nicname);
        mEmailText=findViewById(R.id.sign_email);
        mPasswordText=findViewById(R.id.sign_password);
        findViewById(R.id.sign_success).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mAuth.createUserWithEmailAndPassword(mEmailText.getText().toString() ,mPasswordText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser(); // 로그인 성공
                        if(user != null) {
                            Map<String, Object> userMap = new HashMap<>(); // 파이어 스토어에 집어 넣기

                            userMap.put(FirebaseID.documentId, user.getUid()); //아이디
                            userMap.put(FirebaseID.nicname, mNicname.getText().toString());
                            userMap.put(FirebaseID.email,mEmailText.getText().toString()); // 이메일
                            userMap.put(FirebaseID.password,mPasswordText.getText().toString()); // 페스워드
                            mStore.collection(FirebaseID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                            finish();
                        }
                    }else{
                        Toast.makeText(SignupActivity.this,"sign up error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}