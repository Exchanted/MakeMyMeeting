package com.example.callum.makemymeeting;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

/**
 * This class is used for the first activity presented on the application
 * Here the user can navigate to create a meeting. load their meetings and view locations
 * Also provided is the navigation menu to browse between activities
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Creating the buttons
     * Creating the Database object
     */
    Button btnCreateMeeting;
    Button btnLoadMeetings;
    DBHelper myDb;

    /**
     * {@inheritDoc}
     * @param savedInstanceState - Get phone state
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            Settings for navigation side menu
         */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateMeeting();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
            Instantiating the buttons along with formatting
         */

        btnLoadMeetings = (Button) findViewById(R.id.btnLoadMeetings);
        btnLoadMeetings.setBackgroundColor(Color.WHITE);
        btnLoadMeetings.setTextColor(Color.MAGENTA);

        btnCreateMeeting = (Button) findViewById(R.id.btnCreateMeeting);
        btnCreateMeeting.setBackgroundColor(Color.WHITE);
        btnCreateMeeting.setTextColor(Color.MAGENTA);

        /*
        Instantiating the Database
         */

        myDb = new DBHelper(this);

        /*
        Calling methods below
         */
        openCreate();
        viewAll();
    }

    /**
     * Navigate to CreateMeeting activity through button
     */
    public void openCreate() {
        btnCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CreateMeeting.class);
                startActivity(i);
            }
        });
    }

    /**
     * When load meetings button pressed, all meetings will be grabbed from the database
     * and formatted with the StringBuffer
     */
    public void viewAll() {
        btnLoadMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Get rows from database
                 */
                Cursor res = myDb.getAllData();
                if (res.getCount() == 0) {
                    //Show some message
                    showMeeting("Error", "No meetings found");
                    return;
                }

                /*
                Format rows being read from database
                 */
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("ID: " + res.getString(0) + "\n");
                    buffer.append("Meeting Name: " + res.getString(1) + "\n");
                    buffer.append("Attendees: " + res.getString(2) + "\n");
                    buffer.append("Date: " + res.getString(3) + "\n");
                    buffer.append("Time: " + res.getString(4) + "\n");
                    buffer.append("Notes: " + res.getString(5) + "\n");
                    buffer.append("Location: " + res.getString(6) + "\n\n");
                }

                //Show all meetings data
                showMeeting("List of your Meetings", buffer.toString());
            }
        });
    }

    /**
     * @param title - Set the meetings display title
     * @param meeting - Meeting object set as window message
     */
    public void showMeeting(String title, String meeting) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(meeting);
        builder.show();
    }

    /**
     * Navigate to CreateMeeting through fab button in bottom right
     */
    public void openCreateMeeting() {
        Intent i = new Intent(this, CreateMeeting.class);
        startActivity(i);
    }

    /**
     *{@inheritDoc}
     * On back presses, close side menu
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * {@inheritDoc}
     * @param menu - Create the inflator side menu
     * @return - true if menu has been opened and items added
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @param item - Navigate to the settings tab or selected activity
     * @return - Navigate to selected intent of list
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsMain.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     * @param item - Navigate to selected activity of side menu
     * @return -  Selected activity
     */
    @SuppressWarnings("StatementWithEmptyBody")
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
}
