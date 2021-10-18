package com.example.test_922.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {

    private String documentId;
    private String title;
    private String contents;
    private String nicname;
    @ServerTimestamp
    private Date date;

    public  Post(){}
    public Post(String documentId, String title, String contents ,String nicname){
        this.documentId =documentId;
        this.title = title;
        this.contents = contents;
        this.nicname=nicname;
    }

    public String getNicname() {
        return nicname;
    }

    public void setNicname(String nicname) {
        this.nicname = nicname;
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

    public String getContents() {
        return contents;
    }
    public void setContents() {
        this.contents=contents;
    }


    @Override
    public String toString() {
        return "Post{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", nicname='" + nicname + '\'' +
                ", date=" + date +
                '}';
    }
}
