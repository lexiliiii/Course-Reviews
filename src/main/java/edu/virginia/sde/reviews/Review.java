package edu.virginia.sde.reviews;

import java.sql.Timestamp;

public class Review {
    private int reviewID;
    private String username;
    private String courseMnemonic;
    private int courseNumber;
    private int rating;
    private String courseTitle;
    private Timestamp timestamp;
    private String comment;

    public Review(int reviewID, String username, String courseMnemonic, int courseNumber, int rating, Timestamp timestamp, String comment, String courseTitle) {
        this.reviewID = reviewID;
        this.username = username;
        this.courseMnemonic = courseMnemonic;
        this.courseTitle=courseTitle;
        this.courseNumber = courseNumber;
        this.rating = rating;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public int getReviewID() {
        return reviewID;
    }

    public String getUsername() {
        return username;
    }

    public String getCourseMnemonic() {
        return courseMnemonic;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public int getRating() {
        return rating;
    }
    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return  "Rating: "+ rating+"  "+
        "Time" +" "+
                timestamp +
                "\n" + "\n"+
                "Comment: "+comment;
    }
}

