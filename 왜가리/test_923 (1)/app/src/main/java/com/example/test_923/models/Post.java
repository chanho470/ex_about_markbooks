package com.example.test_923.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {

    private String documentId;
    private String title;
    private String author;
    private String publish;
    private String nicname;
    @ServerTimestamp
    private Date date;

    public  Post(){}
    public Post(String documentId, String title, String author ,String publish,String nicname){
        this.documentId =documentId;
        this.title = title;
        this.author = author;
        this.publish =publish;
        this.nicname=nicname;

    }



    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getNicname() {
        return nicname;
    }

    public void setNicname(String nicname) {
        this.nicname = nicname;
    }

    @Override
    public String toString() {
        return "Post{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publish='" + publish + '\'' +
                ", nicname='" + nicname + '\'' +
                ", date=" + date +
                '}';
    }
}
