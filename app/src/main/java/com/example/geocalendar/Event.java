package com.example.geocalendar;

public class Event {
    private int id;
    private String title;
    private String startdate;
    private String enddate;
    private String location;
    private String description;

    public Event(int id, String title, String startdate, String enddate, String location, String description) {
        this.id = id;
        this.title = title;
        this.startdate = startdate;
        this.enddate = enddate;
        this.location = location;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startdate=" + startdate +
                ", enddate=" + enddate +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
