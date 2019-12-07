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
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final static String TAG = "MapsActivity";
    CalendarHelper calendarHelper;
    //15,000 ms per 1/4 minute
    final static int QUARTER_MINUTE = 15000;
    final static int CALENDAR_REQUEST_CODE = 1;
    List<Marker> currentMarkers;

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
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    /**
                     * Triggers when
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
        runMapRefresh();
    }

    public void centerMap() {
        CameraUpdate gonzagaCameraUpdate = CameraUpdateFactory.newLatLngZoom(
                getLatLngUsingGeocoding("Gonzaga University"), 15.0f);
        mMap.moveCamera(gonzagaCameraUpdate);
    }

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
            e.printStackTrace();
        }

        return latLng;
    }

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

    public void addEventToCalendar(LatLng latLng) {
        String address = getAddressUsingLatLng(latLng);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, address);
        startActivity(intent);
    }

    public void addEventMarker(Event event) {
        MarkerOptions eventMarker = new MarkerOptions();
        eventMarker.title(event.getTitle());
        eventMarker.snippet(event.getDescription());
        eventMarker.position(getLatLngUsingGeocoding(event.getLocation()));
        Marker markerObject = mMap.addMarker(eventMarker);
        currentMarkers.add(markerObject);
    }

    public void addAllEventsToMap() {
        removeAllMarkers();
        List<Event> eventsList = calendarHelper.getEventsList();
        for(Event e: eventsList) {
            addEventMarker(e);
        }
    }

    public void removeAllMarkers() {
        for(Marker m: currentMarkers) {
            m.remove();
        }
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED) {
                addAllEventsToMap();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, CALENDAR_REQUEST_CODE);
            }
        }
    }

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