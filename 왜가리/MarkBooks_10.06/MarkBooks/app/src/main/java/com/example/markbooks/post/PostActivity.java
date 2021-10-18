package com.example.markbooks.post;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.markbooks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 사용자 가져오기
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String id1;
    private String nickname;
    private EditText mTitle,mAuthor,mPublish,mDetail;
    Button mRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        id1 = mAuth.getCurrentUser().getUid();

        mTitle = findViewById(R.id.post_title_edit);
        mAuthor = findViewById(R.id.post_author_edit);
        mPublish = findViewById(R.id.post_publish_edit);
        mDetail = findViewById(R.id.post_publish_detail);
        mRegister = findViewById(R.id.post_save_button);
        mRegister.setOnClickListener(this);
        // 스토어 에서 사용자 닉네임 가져오기
        if(mAuth.getCurrentUser() != null){
            mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult() != null){
                                nickname = (String) task.getResult().getData().get(FirebaseID.nickname);
                                System.out.println(nickname);
                            }
                        }
                    });
        }

    }

    @Override
    public void onClick(View view) {

        String title = mTitle.getText().toString();
        String author = mAuthor.getText().toString();
        String publish = mPublish.getText().toString();
        String detail    = mDetail.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mTitle.setError("title is Required.");
            return;
        }

        if (TextUtils.isEmpty(author)) {
            mAuthor.setError("author is Required.");
            return;
        }
        if (TextUtils.isEmpty(publish)) {
            mPublish.setError("Publish is Required.");
            return;
        }
        if (TextUtils.isEmpty(author) & TextUtils.isEmpty(title) & TextUtils.isEmpty(publish)) {
            mDetail.setError("You can't write here first");
            return;
        }

        // 파이어 베이스에 post 생성 , user 아래에도 생셩
        if(mAuth.getCurrentUser() != null){
            String postId = mStore.collection(FirebaseID.post).document().getId();
            LocalDateTime dateTime = LocalDateTime.now();
            Map<String,Object> data = new HashMap<>();
            data.put(FirebaseID.user,id1);
            data.put(FirebaseID.documentId,postId);
            data.put(FirebaseID.nickname ,nickname);
            data.put(FirebaseID.title ,mTitle.getText().toString());
            data.put(FirebaseID.author ,mAuthor.getText().toString());
            data.put(FirebaseID.publish ,mPublish.getText().toString());
            data.put(FirebaseID.detail ,mDetail.getText().toString());
            data.put(FirebaseID.timestamp,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime).toString());
            mStore.collection(FirebaseID.post).document(postId).set(data , SetOptions.merge());
            data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());//  시간별로 정렬
            finish();

        }

    }
}