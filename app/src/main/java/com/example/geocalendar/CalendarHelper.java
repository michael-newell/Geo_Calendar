package com.example.geocalendar;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as a bridge between the application and the calendar content provider
 */
public class CalendarHelper {
    private static final int CALENDAR_REQUEST_CODE = 0;
    private Context context;
    private final static String TAG = "CalendarHelperTag";

    /**
     * Constructor for CalendarHelper class
     * @param context a reference to the activity that created this
     */
    public CalendarHelper(Context context) {
        this.context = context;
    }

    /**
     * Queries the calendar for a list of events that have a specified location
     * @return a list of Event objects
     * @throws SecurityException if the application does not have CALENDAR_READ permission
     */
    public List<Event> getEventsList() throws SecurityException{
        List<Event> eventsList = null;
        //form the query
        //equivalent to sql SELECT _id, title, description, dtstart, dtend, eventLocation
        String projection[] = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DESCRIPTION

        };
        //WHERE eventLocation is not ''
        String selection = CalendarContract.Events.EVENT_LOCATION + " IS NOT ''";

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
        if(cursor != null) {
            eventsList = new ArrayList<>();
            //add each event to the list
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
                eventsList.add(event);

                //log out the event we just grabbed
                Log.d(TAG, "id:" + cursor.getString(0));
                Log.d(TAG, "title:" + cursor.getString(1));
                Log.d(TAG, "description:" + cursor.getString(2));
                Log.d(TAG, "startdate:" + cursor.getString(3));
                Log.d(TAG, "enddate:" + cursor.getString(4));
                Log.d(TAG, "location:" + cursor.getString(5));
            }
            cursor.close();
        }
        return eventsList;
    }
}
