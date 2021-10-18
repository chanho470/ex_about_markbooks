package com.example.markbooks.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.markbooks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Post2Activity extends AppCompatActivity {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView mTitleText , mAuthorText,mPublishText ,mNameText , mDetailText;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post2);

        mTitleText = findViewById(R.id.post2_title);
        mAuthorText = findViewById(R.id.post2_author);
        mPublishText = findViewById(R.id.post2_publish);
        mNameText = findViewById(R.id.post2_name);
        mDetailText = findViewById(R.id.post2_detail);



        Intent getIntent = getIntent();
        id = getIntent.getStringExtra(FirebaseID.documentId);
        Log.e("item document id",id);
        mStore.collection(FirebaseID.post).document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().exists()) {
                                if (task.getResult() != null) {
                                    Map<String, Object> snap = task.getResult().getData();
                                    String title = String.valueOf(snap.get(FirebaseID.title));
                                    String author = String.valueOf(snap.get(FirebaseID.author));
                                    String publish = String.valueOf(snap.get(FirebaseID.publish));
                                    String name = String.valueOf(snap.get(FirebaseID.nickname));
                                    String detail = String.valueOf(snap.get(FirebaseID.detail));
                                    //String detail = String.valueOf(snap.get(FirebaseID.detail));
                                    mTitleText.setText(title);
                                    mAuthorText.setText(author);
                                    mPublishText.setText(publish);
                                    mNameText.setText(name);
                                    mDetailText.setText(detail);

                                }
                            } else {
                                Toast.makeText(Post2Activity.this,"삭제된 문서",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });




    }
}