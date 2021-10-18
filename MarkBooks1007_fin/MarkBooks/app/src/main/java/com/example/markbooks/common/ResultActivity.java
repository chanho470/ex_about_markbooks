package com.example.markbooks.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.highlight.HighlightActivity;
import com.example.markbooks.MainActivity;
import com.example.markbooks.login.LoginActivity;
import com.example.markbooks.post.PostingActivity;
import com.example.markbooks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class ResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private TextView textView;
    private String userID = fAuth.getCurrentUser().getUid();
    private TextView total;

    // 툴바
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private View navHeaderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        InitializeLayout();


        Intent dataIntent = getIntent();
        String keyword = dataIntent.getStringExtra("검색");
        String keywords = keyword.trim();
        textView = (TextView) findViewById(R.id.textView3);
        textView.setText(keywords);
        //textView2 = (TextView) findViewById(R.id.countText);

        recyclerView = (RecyclerView) findViewById(R.id.resultRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //VERTICAL 위아래  HORIZONTAL 좌우
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ResultAdapter(getApplicationContext());

        total = findViewById(R.id.countText);
        final int[] cnt = {0};



        // data
        fStore.collection("book")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            //String pic = (String) ds.get("pic");
                            String title = (String) ds.get("title");
                            String author = (String) ds.get("author");
                            String genre = (String) ds.get("genre");

                            if (title.contains(keywords) || author.contains(keywords) || genre.contains(keywords)) {
                                //resultList.add(new Book(title, author, genre));
                                cnt[0]++; //검색개수 구하기
                                adapter.addItem(new Book(title, author, genre));
                            }
                        }


                        total.setText(String.valueOf(cnt[0]));
                        recyclerView.setAdapter(adapter);
                    }
                });


        adapter.setOnItemClickListener(new ResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ResultAdapter.MyViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("검색어", adapter.getItem(position).getTitle());
                startActivity(intent);
            }
        });

//        textView2.setText(Integer.toString(adapter.getItemCount()));
//        System.out.println(Integer.toString(adapter.getItemCount()));
    }

    //툴바
    private void InitializeLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_green); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeaderView = navigationView.getHeaderView(0);
        TextView nav_header_id_text = (TextView) navHeaderView.findViewById(R.id.nickName);

        DocumentReference docRef = fStore.collection("user").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nm = (String) document.get("nickname");
                        nav_header_id_text.setText(nm);
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.mybook) {
                    mybook();
                } else if (id == R.id.highlight) {
                    hlList();
                } else if (id == R.id.post) {
                    posting();
                } else if (id == R.id.logout) {
                    logout();
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
        Intent intent = new Intent(getApplicationContext(), MyBookActivity.class);
        startActivity(intent);
    }

    // 메뉴에서 하이라이트 선택
    public void hlList() {
        Intent intent = new Intent(getApplicationContext(), HighlightActivity.class);
        startActivity(intent);
    }

    public void posting() {
        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
        startActivity(intent);
    }

    public void logout() {
        fAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void clickButton(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}