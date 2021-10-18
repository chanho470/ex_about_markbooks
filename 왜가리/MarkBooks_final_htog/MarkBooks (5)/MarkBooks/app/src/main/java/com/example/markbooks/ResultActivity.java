package com.example.markbooks;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Book> resultList = new ArrayList<>();;
    private ArrayList<Book> result = new ArrayList<>();;
    private FirebaseFirestore fStore;
    private TextView textView;

    // 툴바
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        InitializeLayout();


        Intent dataIntent = getIntent();
        String keyword = dataIntent.getStringExtra("검색");
        String keywords = keyword.trim();
        textView = (TextView) findViewById(R.id.searchText);
        textView.setText(keywords);


        resultList.add(new Book("달러구트 꿈 백화점 1", "김작가", "소설"));
        resultList.add(new Book("꽃을 보듯 너를 본다", "김작가", "소설"));
        resultList.add(new Book("메타버스", "김작가", "소설"));
        resultList.add(new Book("미래의 부", "김작가", "소설"));
        resultList.add(new Book("불편한 편의점", "김작가", "소설"));
        resultList.add(new Book("마음챙김의 시", "김작가", "소설"));


//        ArrayList<Book> list = new ArrayList<>();
//        list = Book.createList();
//        for (Book item : list) {
//            String title = item.getTitle();
//            String author = item.getAuthor();
//            String genre = item.getGenre();
//            if (title.contains(keywords) || author.contains(keywords) || genre.contains(keywords)) {
//                resultList.add(item);
//                result.addAll(resultList);
//            }
//        }
//        resultList.get(0);

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
//                            String genre = (String) ds.get("genre");
//                            if (title.contains(keywords) || author.contains(keywords) || genre.contains(keywords)) {
//                                resultList.add(new Book(title, author, genre));
//                            }
//                        }
//                        System.out.println(resultList.get(0));
//                        System.out.println(resultList.get(1));
//                    }
//                });


        recyclerView = (RecyclerView) findViewById(R.id.resultRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ResultAdapter(getApplicationContext(), resultList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClicklistener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(ResultAdapter.MyViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(), BookInfo.class);
                intent.putExtra("검색어", resultList.get(position).getTitle());
                startActivity(intent);
            }
        });
    }

    //툴바
    private void InitializeLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

                if (id == R.id.mybook) {
                    mybook();
                } else if (id == R.id.highlight) {
                    hlList();
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

    public void clickButton(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
