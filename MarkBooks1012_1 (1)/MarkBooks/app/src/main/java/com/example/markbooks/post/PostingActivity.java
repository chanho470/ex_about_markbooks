package com.example.markbooks.post;

import static com.example.markbooks.post.FirebaseID.post;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.MainActivity;
import com.example.markbooks.R;
import com.example.markbooks.common.MyBookActivity;
import com.example.markbooks.common.RecyclerViewItemClickListner;
import com.example.markbooks.highlight.HighlightActivity;
import com.example.markbooks.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostingActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewItemClickListner.OnItemClickListener{
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 사용자 가져오기
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String id1;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private View navHeaderView;

    private String Id;
    private String userID = mAuth.getCurrentUser().getUid();

    private RecyclerView mPostRecyclerView;
    private PostAdapter mAdapter;
    private List<Post> mDatas;

    private ImageButton popup;
    private TextView total;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        InitializeLayout();

        fStore = FirebaseFirestore.getInstance();
        total = findViewById(R.id.total_post);

        //포스팅 총개수 구하기
        fStore.collection("post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int cnt = 0;
                            cnt=task.getResult().size();
                            System.out.println(cnt);
                            total.setText(String.valueOf(cnt));
                        }
                    }
                });

        mPostRecyclerView = findViewById(R.id.main_recyclerview);
        findViewById(R.id.main_post_edit).setOnClickListener(this);
        mPostRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListner(this,mPostRecyclerView, this));
        id1 = mAuth.getCurrentUser().getUid();


    }


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

        DocumentReference docRef = mStore.collection("user").document(userID);
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
                    readBooks();
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
    public void readBooks() {
        Intent intent = new Intent(getApplicationContext(), MyBookActivity.class);
        startActivity(intent);
    }

    // 메뉴에서 하이라이트 선택
    public void hlList() {
        Intent intent = new Intent(getApplicationContext(), HighlightActivity.class);
        startActivity(intent);
    }

    // 메뉴에서 요청 게시판 선택
    public void posting() {
        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
        startActivity(intent);
    }

    // 로그아웃
    public void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatas = new ArrayList<>();
        mStore.collection(post)
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)//내림 차순 정렬
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (queryDocumentSnapshots != null) {
                            mDatas.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()){
                                Map<String,Object> shot = snap.getData();

                                String timestamp = String.valueOf(shot.get(FirebaseID.timestamp));
                                String user = String.valueOf(shot.get(FirebaseID.user));
                                String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                String title = String.valueOf(shot.get(FirebaseID.title));
                                String author = String.valueOf(shot.get(FirebaseID.author));
                                String publish = String.valueOf(shot.get(FirebaseID.publish));
                                String detail = String.valueOf(shot.get(FirebaseID.detail));
                                Post data = new Post(timestamp,user,documentId,title,author,publish,nickname,detail);
                                mDatas.add(data);
                            }
                            mAdapter = new PostAdapter(mDatas);
                            mPostRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(PostingActivity.this, PostActivity.class));
    }

    @Override
    public void onItemClick(View view, int position){
        Intent intent = new Intent(PostingActivity.this, Post2Activity.class);
        intent.putExtra(FirebaseID.documentId,mDatas.get(position).getDocumentId());
        startActivity(intent);
    }


    // 게시물 삭제
    @Override
    public void onItemLongClick(View view, int position)
    {

        final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
        getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if (menuItem.getItemId() == R.id.mod_post)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PostingActivity.this);
                    dialog.setMessage("수정 하시겠습니까?");
                    dialog.setPositiveButton(Html.fromHtml("<font color='#000000'>수정</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DocumentReference colRef = mStore.collection("post").document(mDatas.get(position).getDocumentId());
                            colRef.get().
                                    addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String user = (documentSnapshot.getString("user"));
                                            if (id1.equals(user)) {
                                                mStore.collection(FirebaseID.post).document(mDatas.get(position).getDocumentId()).delete();
                                                mStore.collection("user").document(id1).collection("post").document(mDatas.get(position).getDocumentId()).delete();
                                                startActivity(new Intent(PostingActivity.this, PostActivity.class));
                                                Toast.makeText(PostingActivity.this, "다시 작성하시오", Toast.LENGTH_SHORT).show();

                                            }else{
                                                Toast.makeText(PostingActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }).setNegativeButton(Html.fromHtml("<font color='#000000'>취소</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(PostingActivity.this, "취소 되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.setTitle("수정 알림");
                    AlertDialog alert = dialog.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(144, 127, 164)));
                    alert.show();
                }
                else if (menuItem.getItemId() == R.id.del_post)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PostingActivity.this);
                    dialog.setMessage("삭제 하시겠습니까?");
                    dialog.setPositiveButton(Html.fromHtml("<font color='#000000'>삭제</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DocumentReference colRef = mStore.collection("post").document(mDatas.get(position).getDocumentId());
                            colRef.get().
                                    addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String user = (documentSnapshot.getString("user"));
                                            if (id1.equals(user)) {
                                                mStore.collection(FirebaseID.post).document(mDatas.get(position).getDocumentId()).delete();
                                                mStore.collection("user").document(id1).collection("post").document(mDatas.get(position).getDocumentId()).delete();
                                                Toast.makeText(PostingActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(PostingActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }).setNegativeButton(Html.fromHtml("<font color='#000000'>취소</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(PostingActivity.this, "취소 되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.setTitle("삭제 알림");
                    AlertDialog alert = dialog.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(144, 127, 164)));
                    alert.show();

                }
                return false;
            }
        });
        popupMenu.show();
    }


    public void clickButton1(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}