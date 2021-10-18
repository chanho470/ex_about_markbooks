package com.example.markbooks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Book> resultList = new ArrayList<>();;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        Intent dataIntent = getIntent();
        String keyword = dataIntent.getStringExtra("검색");
        String keywords = keyword.trim();


//        resultList.add(new Book("달러구트", "김작가", "소설"));
//        resultList.add(new Book("꽃", "김작가", "소설"));
//        resultList.add(new Book("달러구트", "김작가", "소설"));
//        resultList.add(new Book("안녕", "김작가", "소설"));
//        resultList.add(new Book("당당", "김작가", "소설"));
//        resultList.add(new Book("달리자", "김작가", "소설"));

        ArrayList<Book> list = new ArrayList<>();
        list = Book.createList();
        for (Book item : list) {
            String title = item.getTitle();
            String author = item.getAuthor();
            String genre = item.getGenre();
            if (title.contains(keywords) || author.contains(keywords) || genre.contains(keywords)) {
                resultList.add(item);
            }
        }

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

    }
}
