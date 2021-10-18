package com.example.test_922;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Post2Activity extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private TextView mTitleText , mContentsText ,mNameText;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post2);

        mTitleText = findViewById(R.id.post2_title);
        mContentsText = findViewById(R.id.post2_contents);
        mNameText = findViewById(R.id.post2_name);

        Intent getIntent = getIntent();
        id = getIntent.getStringExtra(FirebaseID.documentId);
        Log.e("item document id",id);
        mStore.collection(FirebaseID.post).document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()) {
                                if (task.getResult() != null) {
                                    Map<String, Object> snap = task.getResult().getData();
                                    String title = String.valueOf(snap.get(FirebaseID.title));
                                    String contents = String.valueOf(snap.get(FirebaseID.contents));
                                    String name = String.valueOf(snap.get(FirebaseID.nicname));
                                    mTitleText.setText(title);
                                    mContentsText.setText(contents);
                                    mNameText.setText(name);
                                }
                            }else{
                                Toast.makeText(Post2Activity.this,"삭제된 문서",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}