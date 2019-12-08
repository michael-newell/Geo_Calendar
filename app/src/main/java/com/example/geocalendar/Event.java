/**
 * This application allows the user to see all their calendar events on a map
 * CPSC 312-01, Fall 2019
 * Final project
 * No sources to cite.
 *
 * @authors Andrew Brodhead, Michael Newell
 * @version v1.0 12/8/2019
 */
package com.example.geocalendar;

/**
 * This class contains the information regarding a single event in the calendar
 */
public class Event {
    private int id;
    private String title;
    private String startdate;
    private String enddate;
    private String location;
    private String description;

    /**
     * Constructor for the Event object
     * @param id the event id from the calendar content provider
     * @param title the name of the event
     * @param startdate the numeric time/date the event starts
     * @param enddate the numeric time/date the event ends
     * @param location the address of the event
     * @param description the description that the user has given this event
     */
    public Event(int id, String title, String startdate, String enddate, String location, String description) {
        this.id = id;
        this.title = title;
        this.startdate = startdate;
        this.enddate = enddate;
        this.location = location;
        this.description = description;
    }

    /**
     * gets the id of the event
     * @return an integer id
     */
    public int getId() {
        return id;
    }

    /**
     * sets the id of the event
     * @param id an integer id that the event should have
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * gets the title of the event
     * @return the string title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets the title of the event
     * @param title a string title the event should have
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets the start date of the event
     * @return a string number that is the number of milliseconds since 1960 that the event will take place in
     */
    public String getStartdate() {
        return startdate;
    }

    /**
     * sets the date an event starts at
     * @param startdate the string number that the event should start at
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    /**
     * gets the end date of the event
     * @return a string number that is the number of milliseconds since 1960 that the event will end in
     */
    public String getEnddate() {
        return enddate;
    }

    /**
     * sets the date an event ends at
     * @param enddate the string number that the event should end at
     */
    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    /**
     * gets the location of the event
     * @return a string address the event takes place at
     */
    public String getLocation() {
        return location;
    }

    /**
     * sets the location of the event
     * @param location a string containing the address the event should take place at
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * gets the description of the event
     * @return a string containing the event's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets the description of an event
     * @param description a string containing the new description for the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * converts an event object to a string
     * @return a string representing the event
     */
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
