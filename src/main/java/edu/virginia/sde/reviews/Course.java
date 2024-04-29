package edu.virginia.sde.reviews;

public class Course {
    /**
     * Course subject mnemonic: String with at most 4 characters.
     * such as "CS", "STS", "CHEM" etc.
     */
    private final String mnemonic;
    /**
     * Course number: 4-digit integer.
     * such as 3140, 1110, 2600, etc.
     */
    private final int courseNumber;
    /**
     * Course title: String with at most 50 characters including spaces, numbers, etc.
     * such as "Software Development Essentials", "Engineering Ethics", etc.
     */
    private final String courseTitle;
    /**
     * Average course review rating: should be blank if no reviews, otherwise show as a number with two decimal places.
     * such as 2.73, 5.00, etc.
     */
    private double avgRating;


    public Course( String mnemonic, int courseNumber, String courseTitle ) {
        if ( mnemonic == null || String.valueOf( courseNumber ).length() != 4 || courseTitle == null ) {
            throw new IllegalArgumentException("Invalid Course arguments.");
        }
        this.mnemonic = mnemonic;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.avgRating = 0.00;
    }

    public String getMnemonic(){
        return mnemonic;
    }

    public int getCourseNumber(){
        return courseNumber;
    }

    public String getCourseTitle(){
        return courseTitle;
    }

    public double getAvgRating(){
        return avgRating;
    }

}
