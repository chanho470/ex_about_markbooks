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

import com.google.android.material.navigation.NavigationView;

public class Highlight extends AppCompatActivity {

    // 툴바
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight);

        TextView HighLighttv=findViewById(R.id.highlightword);
        //BookInfo 엑티비티에서 값 가져오기
        Intent intent = getIntent();

        String highlightwords = intent.getExtras().getString("Highlightwords");
        HighLighttv.setText("Highlighted Words:\n\n" + highlightwords);
        this.InitializeLayout();
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

                if(id == R.id.read_books){
                    readBooks();
                }
                else if(id == R.id.highlight){
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
    public void readBooks() {
        Intent intent = new Intent(getApplicationContext(), ReadBooks.class);
        startActivity(intent);
    }

    // 메뉴에서 하이라이트 선택
    public void hlList() {
        Intent intent = new Intent(getApplicationContext(), Highlight.class);
        startActivity(intent);
    }

    // 툴바 누르면 메인으로
    public void clickButton(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}