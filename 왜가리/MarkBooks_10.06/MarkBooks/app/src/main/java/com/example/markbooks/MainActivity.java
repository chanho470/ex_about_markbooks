package com.example.markbooks;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.markbooks.common.Book;
import com.example.markbooks.common.BookInfoActivity;
import com.example.markbooks.common.MyBookActivity;
import com.example.markbooks.common.ResultActivity;
import com.example.markbooks.highlight.HighlightActivity;
import com.example.markbooks.login.LoginActivity;
import com.example.markbooks.post.PostingActivity;
import com.example.markbooks.search.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {

    // 툴바
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private View navHeaderView;
    private ArrayList<String> dataArr = new ArrayList<>();

    // 파이어베이스
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();
    String userID = fAuth.getCurrentUser().getUid();

    // 책 이미지 id
    int novelID, essayID, humanitiesID, economyID;
    final ImageButton[] novelimg = new ImageButton[10];
    final ImageButton[] essayimg = new ImageButton[10];
    final ImageButton[] humanitiesimg = new ImageButton[10];
    final ImageButton[] economyimg = new ImageButton[10];

    // 책 데이터
    ArrayList<String> bookTitle = new ArrayList<String>();
    final String[] re_genre = {"소설", "시/에세이", "인문", "비지니스/경제"};

    // 검색
    public LinkedHashSet<String> bookList = new LinkedHashSet<>();

    //View & Button 선언
    private TextView mTextView, Main_info;
    private ImageButton Main_pic, main_pic2, main_pic3, main_pic3_1, main_pic4, main_pic5;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 메인화면 책
        String Bookname = "미드나잇 라이브러리";
        Mainview(Bookname);
        MainRecommend();

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


        //NavigationItem 클릭 이벤트
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

    // 메인 홈 불러오기
    public void Mainview(String Bookname) {
        Main_pic = findViewById(R.id.imageButtonMain);
        Main_info = findViewById(R.id.textViewMain3);

        //라운딩
        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.round);
        Main_pic.setBackground(drawable);
        Main_pic.setClipToOutline(true);

        //사진
        storageRef.child("Book img").child(Bookname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(Main_pic);
            }
        });

        //책 정보
        fStore.collection("book").document(Bookname).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {
                String title = (ds.getString("title"));
                String author = (ds.getString("author"));
                Main_info.setText(title + " - " + author);
            }
        });

    }

    //추천 작 메인 홈에 불러오기
    public void MainRecommend() {
        main_pic2 = findViewById(R.id.imageButtonMain2);
        main_pic3 = findViewById(R.id.imageButtonMain3);
        main_pic3_1 = findViewById(R.id.imageButtonMain3_1);
        main_pic4 = findViewById(R.id.imageButtonMain4);
        main_pic5 = findViewById(R.id.imageButtonMain5);

        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.round);   // ImageButton 라운딩 처리
        main_pic2.setBackground(drawable);
        main_pic2.setClipToOutline(true);
        main_pic3.setBackground(drawable);
        main_pic3.setClipToOutline(true);
        main_pic3_1.setBackground(drawable);
        main_pic3_1.setClipToOutline(true);
        main_pic4.setBackground(drawable);
        main_pic4.setClipToOutline(true);
        main_pic5.setBackground(drawable);
        main_pic5.setClipToOutline(true);

        // BookInfo 연결
        storageRef.child("Book img").child("메타버스" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic2);
            }
        });
        storageRef.child("Book img").child("미래의 부" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic3);
            }
        });
        storageRef.child("Book img").child("복잡계 세상에서의 투자" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic3_1);
            }
        });
        storageRef.child("Book img").child("나태주, 시간의 쉼표" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic4);
            }
        });
        storageRef.child("Book img").child("꽃을 보듯 너를 본다" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic5);
            }
        });

        // 불안정성 코드
        /*
        String[] books = new String[]{"메타버스", "미래의 부","나태주, 시간의 쉼표", "꽃을 보듯 너를 본다"};
        int[] Main = new int[6];
        final ImageButton[] main_pic = new ImageButton[6];
        GradientDrawable drawable= (GradientDrawable) context.getDrawable(R.drawable.round);

        for (int i=2; i<=5; i++) {
            Main[i] = getResources().getIdentifier("imageButtonMain" + i, "id", getPackageName());
            main_pic[i] = findViewById(Main[i]);

            //라운딩
            main_pic[i].setBackground(drawable);
            main_pic[i].setClipToOutline(true);

            storageRef.child("Book img").child(books[i-2] + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(main_pic[n]);
                    n++;
                }
            });
        }
        */
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

    // 메뉴에서 요청게시판 선택
    public void posting() {
        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
        startActivity(intent);
    }

    public void logout() {
        fAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    // 책 표지 이미지 가져오기
    public void getImg() {
        for (int i = 0; i < 6; i++) {
            novelID = getResources().getIdentifier("novel" + (i + 1), "id", getPackageName());
            novelimg[i] = (ImageButton) findViewById(novelID);
            essayID = getResources().getIdentifier("essay" + (i + 1), "id", getPackageName());
            essayimg[i] = (ImageButton) findViewById(essayID);
            humanitiesID = getResources().getIdentifier("humanities" + (i + 1), "id", getPackageName());
            humanitiesimg[i] = (ImageButton) findViewById(humanitiesID);
            economyID = getResources().getIdentifier("economy" + (i + 1), "id", getPackageName());
            economyimg[i] = (ImageButton) findViewById(economyID);
        }

        // "소설", "시/에세이", "인문", "비지니스/경제"
        // novel essay humanities economy


        for (int i = 0; i < 4; i++) {
            processImg(re_genre[i], i);
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
                        int len = bookTitle.size();
                        for (int i = 0; i < len; i++) {
                            int finalI = i;
                            storageRef.child("Book img").child(bookTitle.get(i) + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                //이미지 로드 성공시
                                public void onSuccess(Uri uri) {
                                    Determine(uri,flag,finalI); // ImageButton에 이미지 삽입
                                }
                            });
                        }
                    }
                });
    }

    //장르 판별
    public void Determine(Uri uri, int flag, int finalI){
        if (re_genre[flag] == "소설") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(novelimg[finalI % 6]);
        } else if (re_genre[flag] == "시/에세이") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(essayimg[finalI % 6]);
        } else if (re_genre[flag] == "인문") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(humanitiesimg[finalI % 6]);
        } else if (re_genre[flag] == "비지니스/경제") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(economyimg[finalI % 6]);
        }
    }

    // 책 표지 이미지 클릭 이벤트 처리
    public void handleSelection(View view) {
        Intent intent;
        for (int i = 1; i <= 6; i++) {
            if (view.getId() == getResources().getIdentifier("novel" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("검색어", bookTitle.get(i - 1));
                startActivity(intent);
                break;
            } else if (view.getId() == getResources().getIdentifier("essay" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("검색어", bookTitle.get(i + 5));
                startActivity(intent);
                break;
            } else if (view.getId() == getResources().getIdentifier("humanities" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("검색어", bookTitle.get(i + 11));
                startActivity(intent);
                break;
            } else if (view.getId() == getResources().getIdentifier("economy" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("검색어", bookTitle.get(i + 17));
                startActivity(intent);
                break;
            }
        }
    }

    //메인화면 상단 책 클릭
    public void onClick1(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("검색어", "미드나잇 라이브러리");
        startActivity(intent);
    }
    public void onClick2(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("검색어", "메타버스");
        startActivity(intent);
    }
    public void onClick3(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("검색어", "미래의 부");
        startActivity(intent);
    }
    public void onClick3_1(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("검색어", "복잡계 세상에서의 투자");
        startActivity(intent);
    }
    public void onClick4(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("검색어", "나태주, 시간의 쉼표");
        startActivity(intent);
    }
    public void onClick5(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("검색어", "꽃을 보듯 너를 본다");
        startActivity(intent);
    }
    public void NonClick(View view) {
        Toast.makeText(getApplicationContext(), "아직 열람할 수 없습니다", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search menu action bar.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        // Get the search menu.
        MenuItem searchMenu = menu.findItem(R.id.action_search);
        // Get SearchView object.
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
        // Set hint
        searchView.setQueryHint("책 제목, 작가, 장르");
        // Get SearchView autocomplete object.
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        //searchAutoComplete.setAutofillHints("책 제목, 작가, 장르 검색");
        searchAutoComplete.setBackgroundColor(Color.TRANSPARENT);
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

        // Create a new ArrayAdapter and add data to search auto complete object.
        //String dataArr[] = {"달러구트 꿈 백화점 1" , "달러구트 꿈 백화점 2" , "나씨", "인문", "소설", "MicroNews", "Intel", "Intelligence"};

        dataArr = Book.createList();

        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(newsAdapter);
        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
                //Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("검색", queryString);
                startActivity(intent);
            }
        });
        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setMessage("Search keyword is " + query);
//                alertDialog.show();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("검색", query);
                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // Get the share menu item.
        //MenuItem shareMenuItem = menu.findItem(R.id.app_bar_menu_share);
        // Because it's actionProviderClass is ShareActionProvider, so after below settings
        // when click this menu item A sharable applications list will popup.
        // User can choose one application to share.
//        ShareActionProvider shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareMenuItem);
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("image/*");
//        shareActionProvider.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);
    }

}

