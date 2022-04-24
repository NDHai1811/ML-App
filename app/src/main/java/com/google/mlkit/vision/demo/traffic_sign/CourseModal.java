package com.google.mlkit.vision.demo.traffic_sign;

public class CourseModal {
    // variables for our course
    // name and description.
    private String courseName;
    private String courseDescription;
    private String courseUrl;



    // creating constructor for our variables.
    public CourseModal(String courseName, String courseDescription, String courseUrl) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseUrl = courseUrl;
    }

    // creating getter and setter methods.
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseUrl() {
        return courseUrl;
    }

    public void setCourseUrl(String courseUrl) {
        this.courseUrl = courseUrl;
    }
}
