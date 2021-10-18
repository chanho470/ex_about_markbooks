package com.example.markbooks.common;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Book {
    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    public String pic;
    public String genre;
    public String title;
    public String author;
    public String highlight;
    public String time;

    public Book(String title) {
        this.title = title;
    }

    public Book(String title, String highlight, String time,String genre,String author) {
        this.title = title;
        this.highlight = highlight;
        this.time = time;
        this.author = author;
        this.genre = genre;
    }

    public Book(String pic, String genre, String title, String author) {
        this.pic = pic;
        this.genre = genre;
        this.title = title;
        this.author = author;
    }

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setHighlight(String highlight) { this.highlight = highlight; }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getHighlight() {
        return highlight;
    }

    public String getTime() {
        return time;
    }

    public static ArrayList<Book> createList() {
        ArrayList<Book> contents = new ArrayList<Book>();

        fStore.collection("book")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            //String pic = (String) ds.get("pic");
                            String title = (String) ds.get("title");
                            String author = (String) ds.get("author");
                            String genre = (String) ds.get("genre");
                            contents.add(new Book(title, author, genre));

                        }
                    }
                });
        return contents;
    }
}
