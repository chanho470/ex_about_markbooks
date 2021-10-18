package com.example.listbook;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView a, b, c, d, e, f;       // TextView 선언
    ImageView cover;            // ImageView 선언 (책 표지)
    ImageView A, B, C, D, E, F;      // ImageView 선언 (Recommend 표지)
    List<String> str = new ArrayList<>();

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // View 선언
        a = findViewById(R.id.title);
        b = findViewById(R.id.author);
        c = findViewById(R.id.publisher);
        d = findViewById(R.id.genre);
        e = findViewById(R.id.year);
        f = findViewById(R.id.summary);
        cover = findViewById(R.id.pic);

        A = findViewById(R.id.Recommend1);
        B = findViewById(R.id.Recommend2);
        C = findViewById(R.id.Recommend3);
        D = findViewById(R.id.Recommend4);
        E = findViewById(R.id.Recommend5);
        F = findViewById(R.id.Recommend6);

        // 임시 변수 선언 -> 책을 누르면 변수 입력
        //String name = "달러구트 꿈 백화점 2";
        String name = "오늘 밤, 세계에서 이 사랑이 사라진다 해도";

        //Storage에서 책표지 가져오기
        storageRef.child("Book img").child(name + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(cover);
            }
        });

        //Cloud Firestore에서 책 데이터 가져오기
        DocumentReference docRef = db.collection("book").document(name);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {    //ds: documentSnapshot
                String title = (ds.getString("title"));
                String author = (ds.getString("author"));
                String publisher = (ds.getString("publisher"));
                String year = (ds.getString("year"));
                String genre = (ds.getString("genre"));
                String summary = (ds.getString("summary"));


                a.setText(title);
                b.setText(author);
                c.setText("출판사: " + publisher);
                d.setText("장르:  " + genre);
                e.setText("출간일: " + year);
                f.setText("<줄거리>\n" + summary);
            }
        });


        db.collection("book").whereEqualTo("genre", "소설").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {

                    String pic = ds.get("pic").toString();
                    str.add(pic.substring(42));
                }
                storageRef.child("Book img").child(str.get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(A);
                    }
                });

                storageRef.child("Book img").child(str.get(1)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(B);
                    }
                });

                storageRef.child("Book img").child(str.get(2)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(C);
                    }
                });
                storageRef.child("Book img").child(str.get(3)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(D);
                    }
                });
                storageRef.child("Book img").child(str.get(4)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(E);
                    }
                });
                storageRef.child("Book img").child(str.get(5)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(F);
                    }
                });
            }
        });
    }
}
