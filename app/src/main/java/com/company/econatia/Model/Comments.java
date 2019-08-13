package com.company.econatia.Model;

public class Comments {

    private String comment;
    private String publisher;
    private String commentid;

    public Comments() {
    }

    public Comments(String comment, String publisher , String commentid) {
        this.comment = comment;
        this.commentid = commentid;
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}
