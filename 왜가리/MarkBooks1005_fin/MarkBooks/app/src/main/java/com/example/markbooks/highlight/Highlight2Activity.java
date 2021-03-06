package com.example.markbooks.highlight;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.markbooks.MainActivity;
import com.example.markbooks.R;
import com.example.markbooks.common.Book;
import com.example.markbooks.common.MyBookActivity;
import com.example.markbooks.common.ResultAdapter;
import com.example.markbooks.login.LoginActivity;
import com.example.markbooks.post.PostingActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class Highlight2Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userID;
    private String highlight;
    TextView texthighlight;
    TextView highlight_title;
    TextView highlight_author;
    TextView highlight_genre;
    ImageView img;
    @ServerTimestamp
    Date date;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();

    // ??????
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight2);

        InitializeLayout();

        texthighlight = findViewById(R.id.hl_sentence);
        highlight_title = findViewById(R.id.highlight_title);
        highlight_genre = findViewById(R.id.highlight_genre);
        highlight_author = findViewById(R.id.highlight_author);
        img = findViewById(R.id.bookImage);
        //hl = findViewById(R.id.hl_sentence);

        Intent dataIntent = getIntent();
        highlight = dataIntent.getStringExtra("???????????????");
        System.out.println(highlight);

        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


        DocumentReference docRef = fStore.collection("user").document(userID).collection("highlight").document(highlight);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {    //ds: documentSnapshot
                String highlights = (ds.getString("sentence"));
                System.out.println(highlights);
                texthighlight.setText(highlights);

                fStore.collection("book").document(highlight)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot ds) {    //ds: documentSnapshot
                        String title = (ds.getString("title"));
                        String author = (ds.getString("author"));
                        String genre = (ds.getString("genre"));

                        highlight_title.setText(title);
                        highlight_author.setText(author);
                        highlight_genre.setText(genre);

                        storageRef.child("Book img/" + title + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getApplicationContext())
                                        .load(uri)
                                        .into(img);
                            }
                        });
                    }
                });
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
