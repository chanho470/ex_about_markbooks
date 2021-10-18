package com.example.markbooks.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {

    private String documentId;
    private String title;
    private String author;
    private String publish;
    private String nickname;
    private String detail;
    @ServerTimestamp
    private Date date;

    public  Post(){}
    public Post(String documentId, String title, String author ,String publish,String nickname,String detail){
        this.documentId =documentId;
        this.title = title;
        this.author = author;
        this.publish =publish;
        this.nickname=nickname;
        this.detail = detail;
    }
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "Post{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publish='" + publish + '\'' +
                ", nickname='" + nickname + '\'' +
                ", detail='" + detail + '\'' +
                ", date=" + date +
                '}';
    }
}
