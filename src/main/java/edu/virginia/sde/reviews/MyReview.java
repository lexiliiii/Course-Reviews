package edu.virginia.sde.reviews;

public class MyReview {
    private String username;
    private String courseMnemonic;
    private int courseNumber;
    private int rating;

    public MyReview(String username, String courseMnemonic, int courseNumber, int rating) {
        this.username = username;
        this.courseMnemonic = courseMnemonic;
        this.courseNumber = courseNumber;
        this.rating = rating;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

        @Override
    public String toString() {
        return courseMnemonic + '\'' +
               + courseNumber +"\n" + "\n"+
                "Rating: "+ rating
               ;
    }
}
