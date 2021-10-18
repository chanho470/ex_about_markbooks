package com.example.markbooks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ResultActivity extends AppCompatActivity {

    // 검색
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);

        Intent dataIntent = getIntent();
        String keyword;

        // 검색 키워드값 가져오기
        keyword = dataIntent.getStringExtra("검색어");
        resultText = (TextView) findViewById(R.id.resultText1);
        resultText.setText(keyword);
    }
}
