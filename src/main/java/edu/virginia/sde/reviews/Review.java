package edu.virginia.sde.reviews;

import java.sql.Timestamp;

public class Review {
    private int reviewID;
    private String username;
    private String courseMnemonic;
    private int courseNumber;
    private String courseTitle;
    private int rating;
    private Timestamp timestamp;
    private String comment;

    public Review(int reviewID, String username, String courseMnemonic, int courseNumber, String courseTitle, int rating, Timestamp timestamp, String comment) {
        this.reviewID = reviewID;
        this.username = username;
        this.courseMnemonic = courseMnemonic;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getComment() {
        return comment;
    }

    public String getCourseTitle() {
        return courseTitle;
    }
    public String setCourseTitle() {
        return courseTitle;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewID=" + reviewID +
                "username=" + username +
                ", courseMnemonic='" + courseMnemonic + '\'' +
                ", courseNumber=" + courseNumber +
                ", courseTitle=" + courseTitle +
                ", rating=" + rating +
                ", timestamp=" + timestamp +
                ", comment='" + comment + '\'' +
                '}';
    }
}

