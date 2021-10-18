package com.example.test_922;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 사용자 가져오기
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private String nicname;
    private EditText mTitle,mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mTitle = findViewById(R.id.post_title_edit);
        mContent = findViewById(R.id.post_contents_edit);

        findViewById(R.id.post_save_button).setOnClickListener(this);
        // 스토어 에서 가져오는 방법
        if(mAuth.getCurrentUser() != null){
            mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                nicname = (String) task.getResult().getData().get(FirebaseID.nicname);
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if(mAuth.getCurrentUser() != null){
            String postId = mStore.collection(FirebaseID.post).document().getId();
            Map<String,Object> data = new HashMap<>();
            data.put(FirebaseID.documentId,postId);
            data.put(FirebaseID.nicname ,nicname);
            data.put(FirebaseID.title ,mTitle.getText().toString());
            data.put(FirebaseID.contents ,mContent.getText().toString());
            data.put(FirebaseID.timestamp, FieldValue.serverTimestamp()); //  시간별로 정렬
            mStore.collection(FirebaseID.post).document(postId).set(data , SetOptions.merge());
            finish();
        }
    }
}