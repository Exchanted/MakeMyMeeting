package com.example.callum.makemymeeting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.Calendar;

/**
 * This class is used to create meetings based off an ID system
 * Here the user can create a meeting to be place in the database are perform CRUD operations
 * Also provided is the navigation menu to browse between activities
 */

public class CreateMeeting extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /**
     * Create a database object to perform CRUD operations on
     * Create XML Button objects
     * Create XML TextView objects
     * Create XML EditText objects
     * Create LinearLayout for attendee LayoutInflater
     * Create multiple ints for storing day / time
     */

    DBHelper myDb;

    Button btnAdd;
    Button btnTime;
    Button btnDeleteMeeting;
    Button btnCreateMeeting;
    Button btnSaveNotes;
    Button btnEditMeetings;

    TextView showAddress;
    TextView showTime;
    TextView showDate;
    TextView myNotes;

    EditText editNotes;
    EditText editMeetingName;
    EditText editAddAttendee;
    EditText editMeeting;
    EditText editDeleteID;

    LinearLayout linearLayout;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    /**
     * {@inheritDoc}
     * @param savedInstanceState - Get phone state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        /**
         * Instantiating XML objects along with formatting
         */
        btnCreateMeeting = (Button) findViewById(R.id.btnCreateMeeting);
        btnCreateMeeting.setBackgroundColor(Color.WHITE);
        btnCreateMeeting.setTextColor(Color.MAGENTA);
        btnDeleteMeeting = (Button) findViewById(R.id.btnDeleteMeetings);
        btnDeleteMeeting.setBackgroundColor(Color.WHITE);
        btnDeleteMeeting.setTextColor(Color.MAGENTA);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnEditMeetings = (Button) findViewById(R.id.btnEditMeetings);
        btnEditMeetings.setBackgroundColor(Color.WHITE);
        btnEditMeetings.setTextColor(Color.MAGENTA);
        btnAdd = (Button) findViewById(R.id.btnAddAttendee);
        btnSaveNotes = (Button) findViewById(R.id.btnSaveNotes);

        showTime = (TextView) findViewById(R.id.textTime);
        showDate = (TextView) findViewById(R.id.textHour);
        myNotes = (TextView) findViewById(R.id.myNotes);
        showAddress = (TextView) findViewById(R.id.selectedLocation);

        editMeeting = (EditText) findViewById(R.id.editMeeting);
        editDeleteID = (EditText) findViewById(R.id.deleteMeeting);
        editMeetingName = (EditText) findViewById(R.id.editTextMeetingName);
        editAddAttendee = (EditText) findViewById(R.id.editTextAttendeeName);
        editNotes = (EditText) findViewById(R.id.editNotes);

        /**
         * Creating DBHelper
         */
        myDb = new DBHelper(this);

        /**
         * Attendee LayoutInflater instantiation
         */
        linearLayout = (LinearLayout) findViewById(R.id.attendeeList);
        final LayoutInflater layoutInflater = (LayoutInflater) CreateMeeting.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        /**
         * Create methods below and allow them to work when activity created
         */
        AddData();
        UpdateData();
        RemoveData();

        /**
         * Adding attendees to list
         * Check if editText is blank, if not blank add to view within the inflater
         * Using image to remove from the list
         * Reset edittext upon successful entry
         */
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View myView = layoutInflater.inflate(R.layout.attendee_format_row, null);
                TextView attendeeText = myView.findViewById(R.id.textAttendee);
                attendeeText.setText(editAddAttendee.getText().toString());

                if (editAddAttendee.length() != 0) {
                    linearLayout.addView(myView);

                    ImageView delete = myView.findViewById(R.id.imageDeleteButton);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            linearLayout.removeView((ViewGroup) view.getParent());
                        }
                    });
                    editAddAttendee.setText("");
                } else {
                    Toast.makeText(CreateMeeting.this, "Add an Attendee", Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * Select the Date and Time through button being pressed
         * Get current date and time from phone incase meetings created now
         */
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateMeeting.this,
                        CreateMeeting.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btnSaveNotes.setOnClickListener(new View.OnClickListener() {
            /**
             * Save notes into TextView
             * @param view - Loads edittext information entered into TextView to be saved upon
             *             meeting creation
             */
            @Override
            public void onClick(View view) {
                String notes = editNotes.getText().toString();
                myNotes.setText(notes);
            }
        });

        /**
         * Let user select location of meeting from Places API
         */
        PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            /**
             * Load selected place into TextView
             * @param place - Selected place object from Place API
             */
            @Override
            public void onPlaceSelected(Place place) {
                String placeDetails = (String) place.getAddress();
                showAddress.setText(placeDetails);
            }

            /**
             * Used to throw any error from Place API
             * @param status
             */
            @Override
            public void onError(Status status) {
                Toast.makeText(CreateMeeting.this, "Please enter location again", Toast.LENGTH_LONG).show();
            }
        });

        /**
         * Create side menu drawer
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * When create button pressed, load all data selected and insert into Database
     *      as a new row entry
     */
    public void AddData() {
        btnCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Build list of attendees to be placed into database
                 */
                StringBuilder attendees = new StringBuilder();
                int count = linearLayout.getChildCount();
                RelativeLayout relativeLayoutView = null;
                for (int i = 0; i < count; i++) {
                    relativeLayoutView = (RelativeLayout) linearLayout.getChildAt(i);
                    TextView textView = (TextView) relativeLayoutView.findViewById(R.id.textAttendee);
                    attendees.append(textView.getText().toString()).append(", ");
                }

                String attendeesString = attendees.toString();

                /**
                 * Remove extra formatting ", " from attendee being added to database
                 */
                if (attendeesString.length() > 0) {
                    attendeesString = attendeesString.substring(0, attendees.length() - 2);
                }

                /**
                 * Get entered fields and place into database
                 * Throw toast of success or fail to notify user.
                 */
                boolean isInserted = myDb.insertData(editMeetingName.getText().toString(),
                        attendeesString,
                        showDate.getText().toString(),
                        showTime.getText().toString(),
                        myNotes.getText().toString(),
                        showAddress.getText().toString());

                finish();

                /**
                 * Success / Error message of successful database placement
                 */
                if (isInserted == true)
                    Toast.makeText(CreateMeeting.this, "Meeting Inserted Correctly", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(CreateMeeting.this, "Enter Correct Meeting Format", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Edit rows of create a meeting then select a meeting ID to overwrite in the database
     * Check ID exists in database and overwrite existing information fields
     */
    public void UpdateData() {
        btnEditMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder attendees = new StringBuilder();

                int count = linearLayout.getChildCount();
                RelativeLayout relativeLayoutView = null;
                for (int i = 0; i < count; i++) {
                    relativeLayoutView = (RelativeLayout) linearLayout.getChildAt(i);
                    TextView textView = (TextView) relativeLayoutView.findViewById(R.id.textAttendee);
                    attendees.append(textView.getText().toString()).append(", ");
                }

                String attendeesString = attendees.toString();

                if (attendeesString.length() > 0) {
                    attendeesString = attendeesString.substring(0, attendees.length() - 2);
                }

                boolean isUpdated = myDb.updateData(editMeeting.getText().toString(),
                        editMeetingName.getText().toString(),
                        attendeesString,
                        showDate.getText().toString(),
                        showTime.getText().toString(),
                        editNotes.getText().toString(),
                        showAddress.getText().toString());

                finish();

                if (isUpdated == true)
                    Toast.makeText(CreateMeeting.this, "Meeting Updated Correctly", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(CreateMeeting.this, "Enter Correct Meeting Format", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Remove data from database row where ID matches an ID within the database
     */
    public void RemoveData() {
        btnDeleteMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer deletedRows = myDb.deleteData(editDeleteID.getText().toString());

                finish();

                if (deletedRows > 0)
                    Toast.makeText(CreateMeeting.this, "Meeting Deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(CreateMeeting.this, "Meeting Not Deleted", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * {@inheritDoc}
     * @param datePicker - Select Date - loaded with 3 parameters
     * @param i - Year parameter
     * @param i1 - Month parameter
     * @param i2 - Day parameter
     */
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateMeeting.this, CreateMeeting.this,
                hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();

    }

    /**
     * {@inheritDoc}
     * @param timePicker - Select Time - loaded with 2 parameters
     * @param i - Hour parameter (24 hour)
     * @param i1 - Minute parameter
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        /**
         * Load Date and Time into TextView to clarify to user what they selected
         */
        showTime.setText("Day: " + dayFinal + "   " + "Month: " + monthFinal + "   " + "Year: " + yearFinal);
        showDate.setText("Hour(24): " + hourFinal + "  " + "Minute: " + minuteFinal);

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
}
