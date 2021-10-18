package com.example.markbooks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements TextWatcher {

    private RecyclerView recyclerView;
    private EditText editText;
    private SearchAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID;

    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
//                            userID = fAuth.getCurrentUser().getUid();
//                            Map<String, Object> search = new HashMap<>();
//                            search.put("record", editText.getText().toString());
//                            fStore.collection("Users").document(userID).collection("search").document(editText.getText().toString()).set(search);
//                            ArrayList<String> list = new ArrayList<>();
//                            list = getRecords();
                            String kw = editText.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                            intent.putExtra("검색", kw);
                            startActivity(intent);
                        }
                }
                return true;
            }
        });

        fStore = FirebaseFirestore.getInstance();
//        userID = fAuth.getCurrentUser().getUid();
//
//        CollectionReference productRef = fStore.collection("Users").document(userID).collection("search");
//        //get()을 통해서 해당 컬렉션의 정보를 가져온다.
//        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                //작업이 성공적으로 마쳤을때
//                if (task.isSuccessful()) {
//                    //컬렉션 아래에 있는 모든 정보를 가져온다.
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        //document.getData() or document.getId() 등등 여러 방법으로
//                        //데이터를 가져올 수 있다.
//                        String record = document.getId();
//                        records.add(record);
//                    }
//                }
//            }
//        });

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

        adapter = new SearchAdapter(getApplicationContext(), items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
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
        //get()을 통해서 해당 컬렉션의 정보를 가져온다.
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //작업이 성공적으로 마쳤을때
                if (task.isSuccessful()) {
                    //컬렉션 아래에 있는 모든 정보를 가져온다.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //document.getData() or document.getId() 등등 여러 방법으로
                        //데이터를 가져올 수 있다.
                        String record = document.getId();
                        records.add(record);
                    }
                }
            }
        });
        return records;
    }
}
