package com.example.markbooks.highlight;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.MainActivity;
import com.example.markbooks.R;
import com.example.markbooks.common.Book;
import com.example.markbooks.common.MyBookActivity;
import com.example.markbooks.login.LoginActivity;
import com.example.markbooks.post.PostingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class HighlightActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HighlightAdapter adapter;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userID = fAuth.getCurrentUser().getUid();;
    private String highlight;
    private Date date;

    // ??????
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private View navHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight);

        InitializeLayout();

        recyclerView = (RecyclerView) findViewById(R.id.highlightRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //VERTICAL ?????????  HORIZONTAL ??????
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HighlightAdapter(getApplicationContext(), this);

        fStore = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
        StorageReference storageRef = storage.getReference();

        CollectionReference productRef = fStore.collection("user").document(userID).collection("highlight");
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
                        String record = document.getId(); //record :?????????????????? ??? ??????
                        Intent dataIntent = getIntent();
                        highlight = dataIntent.getStringExtra("???????????????");

                        DocumentReference docRef = fStore.collection("user").document(userID).collection("highlight").document(record);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot ds) {    //ds: documentSnapshot
                                String highlights = (ds.getString("sentence"));
                                String time = (ds.getString("time"));
                                adapter.addItem(new Book(record,highlights,time,"",""));
                                recyclerView.setAdapter(adapter);

                            }

                        });
                    }
                }
            }
        });

        adapter.setOnItemClickListener(new HighlightAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HighlightAdapter.MyViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(), Highlight2Activity.class);
                intent.putExtra("???????????????", adapter.getItem(position).getTitle());
                startActivity(intent);
            }
        });

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
                java.lang.String title = menuItem.getTitle().toString();

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

    public void clickButton(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}