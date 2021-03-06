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
import android.widget.ScrollView;
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

    // ????????????
    private ScrollView mScrollView;
    // ??????
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private View navHeaderView;
    private ArrayList<String> dataArr = new ArrayList<>();

    // ??????????????????
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    private StorageReference storageRef = storage.getReference();
    private String userID = fAuth.getCurrentUser().getUid();

    // ??? ????????? id
    int novelID, essayID, humanitiesID, economyID;
    final ImageButton[] novelimg = new ImageButton[10];
    final ImageButton[] essayimg = new ImageButton[10];
    final ImageButton[] humanitiesimg = new ImageButton[10];
    final ImageButton[] economyimg = new ImageButton[10];

    // ??? ?????????
    ArrayList<String> bookTitle = new ArrayList<String>();
    final String[] re_genre = {"??????", "???/?????????", "??????", "????????????/??????"};

    // ??????
    public LinkedHashSet<String> bookList = new LinkedHashSet<>();

    //View & Button ??????
    private TextView mTextView, Main_info;
    private ImageButton Main_pic, main_pic2, main_pic3, main_pic3_1, main_pic4, main_pic5;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScrollView = findViewById(R.id.Scroll_View);

        // ???????????? ???
        String Bookname = "???????????? ??? ????????? 2";
        Mainview(Bookname);
        MainRecommend();

        this.InitializeLayout(); // ??????

        getImg(); // ??? ????????? ??????
    }

    // ??????
    private void InitializeLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // ?????? title ?????????
        actionBar.setDisplayHomeAsUpEnabled(true); // ???????????? ?????? ?????????
        actionBar.setHomeAsUpIndicator(R.drawable.menu_green); //???????????? ?????? ????????? ??????

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
                    } else {
                    }
                } else {
                }
            }
        });

        //NavigationItem ?????? ?????????
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

    // ?????? ??? ????????????
    public void Mainview(String Bookname) {
        Main_pic = findViewById(R.id.imageButtonMain);
        Main_info = findViewById(R.id.textViewMain3);

        //?????????
        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.round);
        Main_pic.setBackground(drawable);
        Main_pic.setClipToOutline(true);

        //??????
        storageRef.child("Book img").child(Bookname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(Main_pic);
            }
        });

        //??? ??????
        fStore.collection("book").document(Bookname).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {
                String title = (ds.getString("title"));
                String author = (ds.getString("author"));
                Main_info.setText("''?????? ????????? ????????????.'''");
            }
        });

    }

    //?????? ??? ?????? ?????? ????????????
    public void MainRecommend() {
        main_pic2 = findViewById(R.id.imageButtonMain2);
        main_pic3 = findViewById(R.id.imageButtonMain3);
        main_pic3_1 = findViewById(R.id.imageButtonMain3_1);
        main_pic4 = findViewById(R.id.imageButtonMain4);
        main_pic5 = findViewById(R.id.imageButtonMain5);

        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.round);   // ImageButton ????????? ??????
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

        // BookInfo ??????
        storageRef.child("Book img").child("????????????" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic2);
            }
        });
        storageRef.child("Book img").child("????????? ???" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic3);
            }
        });
        storageRef.child("Book img").child("????????? ??????????????? ??????" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic3_1);
            }
        });
        storageRef.child("Book img").child("?????????, ????????? ??????" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic4);
            }
        });
        storageRef.child("Book img").child("?????? ?????? ?????? ??????" + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(main_pic5);
            }
        });
    }

    // ?????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // ?????? ?????? ?????? ????????? ???
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // ???????????? ?????? ??? ??????
    public void mybook() {
        Intent intent = new Intent(getApplicationContext(), MyBookActivity.class);
        startActivity(intent);
    }

    // ???????????? ??????????????? ??????
    public void hlList() {
        Intent intent = new Intent(getApplicationContext(), HighlightActivity.class);
        startActivity(intent);
    }

    // ???????????? ?????? ????????? ??????
    public void posting() {
        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
        startActivity(intent);
    }

    // ????????????
    public void logout() {
        fAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    // ??? ?????? ????????? ????????????
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

        // "??????", "???/?????????", "??????", "????????????/??????"
        // novel essay humanities economy

        for (int i = 0; i < 4; i++) {
            processImg(re_genre[i], i);
        }
    }

    // ???????????? ??? ?????? ??????????????? ??????
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
                                //????????? ?????? ?????????
                                public void onSuccess(Uri uri) {
                                    Determine(uri,flag,finalI); // ImageButton??? ????????? ??????
                                }
                            });
                        }
                    }
                });
    }

    //?????? ??????
    public void Determine(Uri uri, int flag, int finalI){
        if (re_genre[flag] == "??????") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(novelimg[finalI % 6]);
        } else if (re_genre[flag] == "???/?????????") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(essayimg[finalI % 6]);
        } else if (re_genre[flag] == "??????") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(humanitiesimg[finalI % 6]);
        } else if (re_genre[flag] == "????????????/??????") {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(economyimg[finalI % 6]);
        }
    }

    // ??? ?????? ????????? ?????? ????????? ??????
    public void handleSelection(View view) {
        Intent intent;
        for (int i = 1; i <= 6; i++) {
            if (view.getId() == getResources().getIdentifier("novel" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("?????????", bookTitle.get(i - 1));
                startActivity(intent);
                break;
            } else if (view.getId() == getResources().getIdentifier("essay" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("?????????", bookTitle.get(i + 5));
                startActivity(intent);
                break;
            } else if (view.getId() == getResources().getIdentifier("humanities" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("?????????", bookTitle.get(i + 11));
                startActivity(intent);
                break;
            } else if (view.getId() == getResources().getIdentifier("economy" + i, "id", getPackageName())) {
                intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("?????????", bookTitle.get(i + 17));
                startActivity(intent);
                break;
            }
        }
    }

    //???????????? ?????? ??? ??????
    public void onClick1(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("?????????", "???????????? ???????????????");
        startActivity(intent);
    }
    public void onClick2(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("?????????", "????????????");
        startActivity(intent);
    }
    public void onClick3(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("?????????", "????????? ???");
        startActivity(intent);
    }
    public void onClick3_1(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("?????????", "????????? ??????????????? ??????");
        startActivity(intent);
    }
    public void onClick4(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("?????????", "?????????, ????????? ??????");
        startActivity(intent);
    }
    public void onClick5(View view) {
        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
        intent.putExtra("?????????", "?????? ?????? ?????? ??????");
        startActivity(intent);
    }
    public void NonClick(View view) {
        Toast.makeText(getApplicationContext(), "?????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
    }

    public void clickup(View view) {
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
    }

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
        searchView.setQueryHint("??? ??????, ??????, ??????");
        // Get SearchView autocomplete object.
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        //searchAutoComplete.setAutofillHints("??? ??????, ??????, ?????? ??????");
        searchAutoComplete.setBackgroundColor(Color.TRANSPARENT);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

        // Create a new ArrayAdapter and add data to search auto complete object.
        dataArr = Book.createList();

        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(newsAdapter);
        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("??????", queryString);
                startActivity(intent);
            }
        });
        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("??????", query);
                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}

