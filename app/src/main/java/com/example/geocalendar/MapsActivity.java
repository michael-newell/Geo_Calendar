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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class displays the map on the screen that shows all events the user currently has
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final static String TAG = "MapsActivity";
    //15,000 ms per 1/4 minute
    final static int QUARTER_MINUTE = 15000;
    final static int CALENDAR_REQUEST_CODE = 1;

    private GoogleMap mMap;
    CalendarHelper calendarHelper;
    List<Marker> currentMarkers;

    /**
     * This method is triggered when the fragment is created
     * @param savedInstanceState the previous saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currentMarkers = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        calendarHelper = new CalendarHelper(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        centerMap();
        addAllEventsToMap();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            /**
             * Triggers when the user long clicks on the map
             * @param latLng the latitude and longitude where the user clicked
             */
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    /**
                     * Triggers when a dialog interface button is clicked
                     * @param dialog the dialog object which was interacted with
                     * @param which the button that was pressed
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case AlertDialog.BUTTON_POSITIVE:
                                addEventToCalendar(latLng);
                                break;
                            case AlertDialog.BUTTON_NEGATIVE:
                                addAllEventsToMap();
                                break;
                        }
                    }
                };
                dialog.setTitle(R.string.add_dialog_title)
                        .setMessage(R.string.add_dialog_message)
                        .setPositiveButton(R.string.dialog_positive_button, listener)
                        .setNegativeButton(R.string.dialog_negative_button, listener)
                        .show();
            }
        });
        //start the refreshing of the screen to detect for deleted events
        runMapRefresh();
    }

    /**
     * Centers the map on Gonzaga University
     */
    public void centerMap() {
        CameraUpdate gonzagaCameraUpdate = CameraUpdateFactory.newLatLngZoom(
                getLatLngUsingGeocoding("Gonzaga University"), 15.0f);
        mMap.moveCamera(gonzagaCameraUpdate);
    }

    /**
     * Takes an address and geocodes it into latitude/longitude coordinates
     * Borrowed from in-class code
     * @param addressStr the string address that should be converted to coordinates
     * @return the first latitude/longitude result that matches the address passed in
     */
    private LatLng getLatLngUsingGeocoding(String addressStr) {
        LatLng latLng = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(addressStr, 1);
            if (addressList != null && addressList.size() > 0) {
                Address addressResult = addressList.get(0);
                latLng = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
            }
        } catch (IOException e) {
            //error is thrown if user does not have location services enabled
            e.printStackTrace();
            Toast.makeText(this, "Unable to Access location services", Toast.LENGTH_SHORT).show();
            //default LatLng so the program doesn't crash
            return new LatLng(0, 0);
        }
        return latLng;
    }

    /**
     * Reverse geocodes a latitude longitude object into a string address
     * adapted from in-class code
     * @param latLng the coordinates that are to be translated into an address
     * @return the first address that matches the input coordinates
     */
    private String getAddressUsingLatLng(LatLng latLng) {
        String address = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address addressResult = addressList.get(0);
                address = addressResult.getAddressLine(0);
                Log.d(TAG, "getAddressUsingLatLng: " + address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    /**
     * Starts an intent to add a single event to the user's calendar and
     * passes the location the user clicked on to the calendar app.
     * @param latLng the location that the user selected
     */
    public void addEventToCalendar(LatLng latLng) {
        String address = getAddressUsingLatLng(latLng);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, address);
        startActivity(intent);
    }

    /**
     * Adds a marker to the map corresponding to a single event object
     * @param event the event that is to be placed on the map
     */
    public void addEventMarker(Event event) {
        MarkerOptions eventMarker = new MarkerOptions();
        eventMarker.title(event.getTitle());
        eventMarker.snippet(event.getDescription());
        eventMarker.position(getLatLngUsingGeocoding(event.getLocation()));
        Marker markerObject = mMap.addMarker(eventMarker);
        //keep track of each marker so that we can delete them all
        currentMarkers.add(markerObject);
    }

    /**
     * Clears all markers currently on the map and readds them
     */
    public void addAllEventsToMap() {
        removeAllMarkers();
        //get the list of events from the calendar content provider
        List<Event> eventsList = calendarHelper.getEventsList();
        for(Event e: eventsList) {
            addEventMarker(e);
        }
    }

    /**
     * Removes all markers from the map
     */
    public void removeAllMarkers() {
        for(Marker m: currentMarkers) {
            m.remove();
        }
    }

    /**
     * Runs a method every 1/4 minute to refresh the markers on the map
     * we need to do this because the calendar takes a while to show when an event is deleted
     * when querying it
     */
    public void runMapRefresh() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                addAllEventsToMap();
                handler.postDelayed(this, QUARTER_MINUTE);
            }
        });
    }

    /**
     * Triggers when the application comes back into focus
     */
    @Override
    protected void onResume() {
        super.onResume();
        //check permissions to read/write calendar
        if(mMap != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED) {
                //add all our events to the map
                addAllEventsToMap();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, CALENDAR_REQUEST_CODE);
            }
        }
    }

    /**
     * Triggers when a request for the permissions necessary for the application to function returns
     * @param requestCode the code that the request returned
     * @param permissions the list of permissions requested
     * @param grantResults the list of grant/rejects for requested permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CALENDAR_REQUEST_CODE) {
            if(permissions.length == 2 && permissions[0].equals(Manifest.permission.WRITE_CALENDAR)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && permissions[1].equals(Manifest.permission.READ_CALENDAR)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //we got the user's permission
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED) {
                    if(mMap != null) {
                        addAllEventsToMap();
                    }
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.WRITE_CALENDAR}, CALENDAR_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, "Unable to get permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}