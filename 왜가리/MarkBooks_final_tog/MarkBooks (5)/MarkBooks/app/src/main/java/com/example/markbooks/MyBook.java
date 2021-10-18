package com.example.markbooks;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyBook extends AppCompatActivity {

    // 툴바
    private DrawerLayout mDrawerLayout;

    // 파이어베이스
    DocumentReference dRef;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;
    String userID;

    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    ConstraintLayout expandableView, expandableView2;
    Button arrowBtn, arrowBtn2;
    CardView cardView, cardView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybook);
        this.InitializeLayout();

        expandableView = findViewById(R.id.expandableView);
        expandableView2 = findViewById(R.id.expandableView2);
        cardView = findViewById(R.id.cardView);
        cardView2 = findViewById(R.id.cardView2);
        arrowBtn = findViewById(R.id.arrowBtn);
        arrowBtn2 = findViewById(R.id.arrowBtn2);

        auth();


        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableView.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);
                    arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24);
                }
                else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                    arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24);
                }
            }
        });

        arrowBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableView2.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                    expandableView2.setVisibility(View.VISIBLE);
                    arrowBtn2.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24);
                }
                else {
                    TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                    expandableView2.setVisibility(View.GONE);
                    arrowBtn2.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24);
                }
            }
        });
    }

    //툴바
    private void InitializeLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_white); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.mybook){
                    mybook();
                } else if(id == R.id.highlight){
                    hlList();
                } else if (id == R.id.post) {
                    posting();
                }
                return true;
            }
        });
    }

    // 메뉴 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 메뉴에서 읽은 책 선택
    public void mybook() {
        Intent intent = new Intent(getApplicationContext(), MyBook.class);
        startActivity(intent);
    }

    // 메뉴에서 하이라이트 선택
    public void hlList() {
        Intent intent = new Intent(getApplicationContext(), Highlight.class);
        startActivity(intent);
    }

    public void posting() {
        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
        startActivity(intent);
    }

//    public void getRecord() {
//        ArrayList<Book> list = new ArrayList<>();
//
//        // data
//        fStore = FirebaseFirestore.getInstance();
//        fStore.collection("book")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
//                            String title = (String) ds.get("title");
//                            String author = (String) ds.get("author");
//                            list.add(new Book(title, author));
//                        }
//                    }
//                });
//    }

    // 툴바 누르면 메인화면으로
    public void clickButton(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //임시
    public void auth(){
        fAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = fAuth.getCurrentUser();
        userID = user != null ? user.getUid(): null;
        fStore.collection("user").document(userID).collection("favorite").document("title");
    }
}
