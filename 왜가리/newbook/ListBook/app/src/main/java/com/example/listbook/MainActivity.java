package com.example.listbook;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView a, b, c, d, e, f, s_a;       // TextView 선언
    ImageView cover;                 // ImageView 선언 (책 표지)
    List<String> str = new ArrayList<>();
    int k = 0;                       // 횡스크롤뷰 이미지 number
    int[] Recommend = new int[10];
    final ImageButton[] img = new ImageButton[10];

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();


    private ConstraintLayout rootLayout;
    private ImageView pic;

    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        rootLayout = findViewById(R.id.root_layout);
        pic = findViewById(R.id.pic);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Text_View 선언
        a = findViewById(R.id.title);
        b = findViewById(R.id.author);
        c = findViewById(R.id.publisher);
        d = findViewById(R.id.genre);
        e = findViewById(R.id.year);
        f = findViewById(R.id.summary);
        cover = findViewById(R.id.pic);
        s_a = findViewById(R.id.smallTitle);

        //Recommend_View 선언
        for (int i=1; i<=6; i++) {
            Recommend[i] = getResources().getIdentifier("Recommend" + i, "id", getPackageName());
            img[i-1] = (ImageButton) findViewById(Recommend[i]);
        }

        // 임시 변수 선언 -> 책을 누르면 변수 입력
        //String Bookname = "달러구트 꿈 백화점 2";
        String Bookname = "미드나잇 라이브러리";
        //String Bookname = "오늘 밤, 세계에서 이 사랑이 사라진다 해도";
        //String Bookname = "꽃을 보듯 너를 본다";
        //String Bookname = "햇빛은 찬란하고 인생은 귀하니까요";

        //Storage에서 책표지 가져오기
        storageRef.child("Book img").child(Bookname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(cover);
            }
        });

        //Cloud Firestore에서 책 데이터 가져오기
        DocumentReference docRef = db.collection("book").document(Bookname);
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

                s_a.setText(title);
            }
        });
        Bitmap bitmap =((BitmapDrawable) pic.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener()
        {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                vibrantSwatch=palette.getDarkMutedSwatch();
                Palette.Swatch currnetSwatch = vibrantSwatch;
                rootLayout.setBackgroundColor(currnetSwatch.getRgb());
            }
        });

        // 횡 스크롤 뷰
        DocumentReference colRef =db.collection("book").document(Bookname);
        colRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String re_genre = (documentSnapshot.getString("genre"));
                db.collection("book").whereEqualTo("genre", re_genre).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            String pic = ds.get("pic").toString();
                            str.add(pic.substring(42));
                        }
                        for(int i=0;i<str.size(); i++){
                            storageRef.child("Book img").child(str.get(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getApplicationContext())
                                            .load(uri)
                                            .into(img[k]);
                                    k++;
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
