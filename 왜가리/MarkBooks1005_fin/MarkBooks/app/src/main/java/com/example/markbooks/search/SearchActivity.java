package com.example.markbooks.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.MainActivity;
import com.example.markbooks.R;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements TextWatcher {

    private RecyclerView recyclerView;
    private EditText editText;
    private SearchAdapter adapter;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID = fAuth.getCurrentUser().getUid();

    // ??????
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        InitializeLayout();

        recyclerView = (RecyclerView)findViewById(R.id.searchRecycler);
        editText = (EditText)findViewById(R.id.editText);
        editText.addTextChangedListener(this);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i){
                    case KeyEvent.KEYCODE_ENTER:
                        //event
                        if (!(editText.getText().toString().length() == 0)) {
                            userID = fAuth.getCurrentUser().getUid();
                            Map<String, Object> search = new HashMap<>();
                            search.put("record", editText.getText().toString());
                            fStore.collection("user").document(userID).collection("search").document(editText.getText().toString()).set(search);
                            String kw = editText.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                            intent.putExtra("??????", kw);
                            startActivity(intent);
                        }
                }
                return true;
            }
        });

        CollectionReference productRef = fStore.collection("user").document(userID).collection("search");
        //get()??? ????????? ?????? ???????????? ????????? ????????????.
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //????????? ??????????????? ????????????
                if (task.isSuccessful()) {
                    //????????? ????????? ?????? ?????? ????????? ????????????.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //document.getData() or document.getId() ?????? ?????? ????????????
                        //???????????? ????????? ??? ??????.
                        String record = document.getId();
                        records.add(record);
                    }
                }
            }
        });

        fStore.collection("book")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            String title = (String) ds.get("title");
                            String author = (String) ds.get("author");
                            String genre = (String) ds.get("genre");
                            if (!items.contains(title)) {
                                items.add(title);
                            }
                            if (!items.contains(author)) {
                                items.add(author);
                            }
                            if (!items.contains(genre)) {
                                items.add(genre);
                            }
                        }
                    }
                });

        adapter = new SearchAdapter(getApplicationContext(), items, records);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


//        adapter.setOnItemClicklistener(new OnKeywordClickListener() {
//            @Override
//            public void onItemClick(SearchAdapter.MyViewHolder holder, View view, int position) {
//                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                intent.putExtra("??????", items.get(position));
//                startActivity(intent);
//            }
//        });

    }

    //??????
    private void InitializeLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // ?????? title ?????????
        actionBar.setDisplayHomeAsUpEnabled(true); // ???????????? ?????? ?????????
        actionBar.setHomeAsUpIndicator(R.drawable.menu_white); //???????????? ?????? ????????? ??????

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
                } else if (id == R.id.post) {
                    posting();
                } else if (id == R.id.logout) {
                    logout();
                }
                return true;
            }
        });
    }

    // ?????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // ?????? ?????? ?????? ????????? ???
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

    public void posting() {
        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
        startActivity(intent);
    }

    public void logout() {
        fAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        adapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public ArrayList<String> getRecords() {
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        CollectionReference productRef = fStore.collection("Users").document(userID).collection("search");
        //get()??? ????????? ?????? ???????????? ????????? ????????????.
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //????????? ??????????????? ????????????
                if (task.isSuccessful()) {
                    //????????? ????????? ?????? ?????? ????????? ????????????.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //document.getData() or document.getId() ?????? ?????? ????????????
                        //???????????? ????????? ??? ??????.
                        String record = document.getId();
                        records.add(record);
                    }
                }
            }
        });
        return records;
    }

    public void clickButton(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void clickSearch(View view) {
        String s = editText.getText().toString();
        String ss = s.trim();
        if (ss.length() == 0 || ss == null) {
            Toast.makeText(SearchActivity.this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
        } else {
            String kw = editText.getText().toString();
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("??????", kw);
            startActivity(intent);
        }
    }
}
