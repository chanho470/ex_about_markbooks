package com.example.markbooks;

import static android.content.ContentValues.TAG;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    // 툴바
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    // 파이어베이스
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();
    String userID;

    // 책 이미지 id
    int novelID, essayID, humanitiesID, economyID;
    final ImageButton[] novelimg = new ImageButton[10];
    final ImageButton[] essayimg = new ImageButton[10];
    final ImageButton[] humanitiesimg = new ImageButton[10];
    final ImageButton[] economyimg = new ImageButton[10];

    // 책 데이터
    ArrayList<String> bookTitle = new ArrayList<String>();;
    final String[] re_genre = {"소설", "시/에세이", "인문", "비지니스/경제"};

    // 검색
    public LinkedHashSet<String> bookList = new LinkedHashSet<>();


    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.InitializeLayout(); // 툴바

        mTextView = findViewById(R.id.resultText1);

        getImg(); // 책 이미지 로드

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

                if (id == R.id.read_books) {
                    readBooks();
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
        switch (item.getItemId()) {
            case android.R.id.home: { // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 검색 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // 버튼 클릭 시 searchview 길이 꽉차게 늘려주기
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // 힌트 추가
        searchView.setQueryHint("책 제목, 작가, 장르");

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            //SUGGEST_COLUMN_INTENT_EXTRA_DATA를 통해 보내진 데이터는
            //SearchManager.EXTRA_DATA_KEY로 조회할 수 있습니다.
            String url = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
            mTextView.setText(url);
        }

        // 리스너
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 결과 DB에 저장
                // Toast.makeText(getApplicationContext(), "입력완료", Toast.LENGTH_SHORT).show();
                //SearchView search = searchView;

                Intent intent = getIntent();
                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                    String msg = intent.getStringExtra(SearchManager.QUERY);
                    search(msg);
                }
                String msg = String.valueOf(searchView.getQuery());
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                fAuth = FirebaseAuth.getInstance();
                userID = fAuth.getCurrentUser().getUid();


                Map<String, Object> search = new HashMap<>();
                search.put("record", msg);
                fStore.collection("Users").document(userID).collection("search").add(search);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 새로운 쿼리의 결과 뿌리기
                Intent intent = getIntent();
                if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                    //SUGGEST_COLUMN_INTENT_EXTRA_DATA를 통해 보내진 데이터는
                    //SearchManager.EXTRA_DATA_KEY로 조회할 수 있습니다.
                    String url = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
                    mTextView.setText(url);
                }
                return false;
            }
        });

        return true;
    }

    // 검색
    void search(String query) {
        fStore.collection("book")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            String title = (String) ds.get("title");
                            String author = (String) ds.get("author");
                            String genre = (String) ds.get("genre");
                            bookList.add(title);
                            bookList.add(author);
                            bookList.add(genre);
                        }
                    }
                });

        String[] bookArr = new String[bookList.size()];
        bookArr = bookList.toArray(bookArr);

        //String[] companyNames = getResources().getStringArray(R.array.company_names);
        for (String b : bookArr) {
            if (b.toLowerCase().contains(query.toLowerCase())) {
                mTextView.setText(b);
                return;
            } else {
                mTextView.setText("Search failed.");
            }
        }
    }

    // 메뉴에서 읽은 책 선택
    public void readBooks() {
        Intent intent = new Intent(getApplicationContext(), ReadBooks.class);
        startActivity(intent);
    }

    // 메뉴에서 하이라이트 선택
    public void hlList() {
        Intent intent = new Intent(getApplicationContext(), Highlight.class);
        startActivity(intent);
    }

    // 책 표지 이미지 가져오기
    public void getImg() {
        for (int i=0; i<6; i++) {
            novelID = getResources().getIdentifier("novel" + (i+1), "id", getPackageName());
            novelimg[i] = (ImageButton) findViewById(novelID);
            essayID = getResources().getIdentifier("essay" + (i+1), "id", getPackageName());
            essayimg[i] = (ImageButton) findViewById(essayID);
            humanitiesID = getResources().getIdentifier("humanities" + (i+1), "id", getPackageName());
            humanitiesimg[i] = (ImageButton) findViewById(humanitiesID);
            economyID = getResources().getIdentifier("economy" + (i+1), "id", getPackageName());
            economyimg[i] = (ImageButton) findViewById(economyID);
        }

        // "소설", "시/에세이", "인문", "비지니스/경제"
        // novel essay humanities economy
        for (int i=0; i<4; i++){
            processImg(re_genre[i], i);
            //System.out.println(re_genre[i]);
        }
    }

    // 장르별로 책 표지 가져오도록 처리
    public void processImg(String gr, int flag) {
        fStore.collection("book").whereEqualTo("genre", gr)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            String title = (String) ds.get("title");
                            bookTitle.add(title);
                        }
                        //System.out.println("장르 : " + re_genre[flag]);

                        for (int i = 0; i < bookTitle.size(); i++) {
                            //System.out.println("장르 : " + re_genre[flag] + i + "번 " + bookTitle.get(i));

                            int finalI = i;
                            storageRef.child("Book img").child(bookTitle.get(i) + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                //이미지 로드 성공시
                                public void onSuccess(Uri uri) {
                                    if (re_genre[flag] == "소설") {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(novelimg[finalI % 6]);
                                    }
                                    if (re_genre[flag] == "시/에세이") {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(essayimg[finalI % 6]);
                                    }
                                    if (re_genre[flag] == "인문") {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(humanitiesimg[finalI % 6]);
                                    }
                                    if (re_genre[flag] == "비지니스/경제") {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(economyimg[finalI % 6]);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    // 책 표지 이미지 클릭 이벤트 처리
    public void handleSelection(View view) {
        Intent intent;
        for (int i=1; i<=6; i++) {
            if (view.getId() == getResources().getIdentifier("novel" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfo.class);
                intent.putExtra("검색어", bookTitle.get(i-1));
                startActivity(intent);
                break;
            }
        }
        for (int i=1; i<=6; i++) {
            if (view.getId() == getResources().getIdentifier("essay" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfo.class);
                intent.putExtra("검색어", bookTitle.get(i+5));
                startActivity(intent);
                break;
            }
        }
        for (int i=1; i<=6; i++) {
            if (view.getId() == getResources().getIdentifier("humanities" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfo.class);
                intent.putExtra("검색어", bookTitle.get(i+11));
                startActivity(intent);
                break;
            }
        }
        for (int i=1; i<=6; i++) {
            if (view.getId() == getResources().getIdentifier("economy" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfo.class);
                intent.putExtra("검색어", bookTitle.get(i+17));
                startActivity(intent);
                break;
            }
        }



//        Intent intent;
//        switch (view.getId()) {
//            case R.id.novel1:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(0));
//                startActivity(intent);
//                break;
//            case R.id.novel2:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(1));
//                startActivity(intent);
//                break;
//            case R.id.novel3:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(2));
//                startActivity(intent);
//                break;
//            case R.id.novel4:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(3));
//                startActivity(intent);
//                break;
//            case R.id.novel5:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(4));
//                startActivity(intent);
//                break;
//            case R.id.novel6:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(5));
//                startActivity(intent);
//                break;
//            case R.id.essay1:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(6));
//                startActivity(intent);
//                break;
//            case R.id.essay2:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(7));
//                startActivity(intent);
//                break;
//            case R.id.essay3:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(8));
//                startActivity(intent);
//                break;
//            case R.id.essay4:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(9));
//                startActivity(intent);
//                break;
//            case R.id.essay5:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(10));
//                startActivity(intent);
//                break;
//            case R.id.essay6:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(11));
//                startActivity(intent);
//                break;
//            case R.id.humanities1:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(12));
//                startActivity(intent);
//                break;
//            case R.id.humanities2:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(13));
//                startActivity(intent);
//                break;
//            case R.id.humanities3:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(14));
//                startActivity(intent);
//                break;
//            case R.id.humanities4:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(15));
//                startActivity(intent);
//                break;
//            case R.id.humanities5:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(16));
//                startActivity(intent);
//                break;
//            case R.id.humanities6:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(17));
//                startActivity(intent);
//                break;
//            case R.id.economy1:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(18));
//                startActivity(intent);
//                break;
//            case R.id.economy2:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(19));
//                startActivity(intent);
//                break;
//            case R.id.economy3:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(20));
//                startActivity(intent);
//                break;
//            case R.id.economy4:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(21));
//                startActivity(intent);
//                break;
//            case R.id.economy5:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(22));
//                startActivity(intent);
//                break;
//            case R.id.economy6:
//                intent = new Intent(getApplicationContext(), BookInfo.class);
//                intent.putExtra("검색어", bookTitle.get(23));
//                startActivity(intent);
//                break;
//        }
    }
}