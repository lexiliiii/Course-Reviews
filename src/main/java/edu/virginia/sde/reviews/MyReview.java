package edu.virginia.sde.reviews;

public class MyReview {
    private String username;
    private String courseMnemonic;
    private int courseNumber;
    private String courseTitle;
    private int rating;

    public MyReview(String username, String courseMnemonic, int courseNumber, String courseTitle, int rating) {
        this.username = username;
        this.courseMnemonic = courseMnemonic;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.rating = rating;
    }

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCourseMnemonic() {
        return courseMnemonic;
    }

    public void setCourseMnemonic(String courseMnemonic) {
        this.courseMnemonic = courseMnemonic;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Course: " + courseMnemonic +
                " " + courseNumber + " " + courseTitle +"\n" +
                "Rating: " + rating+ "\n" +
                "CLICK to see your specific comment.";
    }
}
