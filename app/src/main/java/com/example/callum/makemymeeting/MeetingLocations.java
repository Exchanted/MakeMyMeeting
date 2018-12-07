package com.example.callum.makemymeeting;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class allows a user to search for their meeting location on google maps
 * Utilizes Maps API and Places API to perform a search
 */

public class MeetingLocations extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    /**
     * Create GoogleMap variable
     */

    private GoogleMap mMap;

    /**
     * {@inheritDoc}
     * @param savedInstanceState - Get phone state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_locations);

        /**
         * Create Google Map fragment
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Create Google Place fragment
         */
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            /**
             * {@inheritDoc}
             * @param place - Address selected by user input
             */

            @Override
            public void onPlaceSelected(Place place) {
                // Add some markers to the map, and add a data object to each marker.

                /**
                 * Marker placed on user selected location
                 * Auto-zoom in on marker
                 */
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title("Meeting Here"));

                /**
                 * Auto-zoom on marker
                 */
                float zoom = 16.0f;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom));
            }

            /**
             * {@inheritDoc}
             * @param status - Error code if marker not correctly placed
             */

            @Override
            public void onError(Status status) {
                Toast.makeText(MeetingLocations.this, "Marker Failed To Locate Position", Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * {@inheritDoc}
     * @param item - Navigate to selected activity of side menu
     * @return -  Selected activity
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.create_meeting) {
            Intent i = new Intent(this, CreateMeeting.class);
            startActivity(i);
        } else if (id == R.id.meeting_history) {
            Intent i = new Intent(this, MeetingHistory.class);
            startActivity(i);
        } else if (id == R.id.location_history) {
            Intent i = new Intent(this, MeetingLocations.class);
            startActivity(i);
        } else if (id == R.id.navigate_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @param googleMap - When map ready create map object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}

