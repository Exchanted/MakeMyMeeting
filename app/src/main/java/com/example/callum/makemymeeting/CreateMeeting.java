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

public class CreateMeeting extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DBHelper myDb;

    Button btnAdd;
    Button btnTime;

    TextView showAddress;

    TextView showTime;
    TextView showDate;

    EditText editNotes;
    TextView myNotes;
    Button btnSaveNotes;

    EditText editMeetingName;
    EditText editAddAttendee;
    Button btnCreateMeeting;

    Button btnEditMeetings;
    EditText editMeeting;

    Button btnDeleteMeeting;
    EditText editDeleteID;

    LinearLayout linearLayout;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        btnCreateMeeting = (Button) findViewById(R.id.btnCreateMeeting);
        btnCreateMeeting.setBackgroundColor(Color.WHITE);
        btnCreateMeeting.setTextColor(Color.MAGENTA);

        btnDeleteMeeting = (Button) findViewById(R.id.btnDeleteMeetings);
        btnDeleteMeeting.setBackgroundColor(Color.WHITE);
        btnDeleteMeeting.setTextColor(Color.MAGENTA);

        myDb = new DBHelper(this);

        AddData();

        editMeetingName = (EditText) findViewById(R.id.editTextMeetingName);

        btnAdd = (Button) findViewById(R.id.btnAddAttendee);
        editAddAttendee = (EditText) findViewById(R.id.editTextAttendeeName);

        btnEditMeetings = (Button) findViewById(R.id.btnEditMeetings);
        btnEditMeetings.setBackgroundColor(Color.WHITE);
        btnEditMeetings.setTextColor(Color.MAGENTA);

        linearLayout = (LinearLayout) findViewById(R.id.attendeeList);

        final LayoutInflater layoutInflater = (LayoutInflater) CreateMeeting.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View myView = layoutInflater.inflate(R.layout.attendee_format_row, null);
                TextView attendeeText = myView.findViewById(R.id.textAttendee);
                attendeeText.setText(editAddAttendee.getText().toString());

                if(editAddAttendee.length() != 0) {
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

        btnTime = (Button) findViewById(R.id.btnTime);
        showTime = (TextView) findViewById(R.id.textTime);
        showDate = (TextView) findViewById(R.id.textHour);

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

        btnSaveNotes = (Button) findViewById(R.id.btnSaveNotes);
        editNotes = (EditText) findViewById(R.id.editNotes);
        myNotes = (TextView) findViewById(R.id.myNotes);

        btnSaveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notes = editNotes.getText().toString();
                myNotes.setText(notes);
            }
        });

        showAddress = (TextView) findViewById(R.id.selectedLocation);

        PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String placeDetails = (String) place.getAddress();
                showAddress.setText(placeDetails);
            }

            @Override
            public void onError(Status status) {

            }
        });

        UpdateData();
        RemoveData();

        editMeeting = (EditText) findViewById(R.id.editMeeting);

        editDeleteID = (EditText) findViewById(R.id.deleteMeeting);

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

    public void AddData() {
        btnCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder attendees = new StringBuilder();

                int count = linearLayout.getChildCount();
                RelativeLayout relativeLayoutView = null;
                for(int i=0; i<count; i++) {
                    relativeLayoutView = (RelativeLayout) linearLayout.getChildAt(i);
                    TextView textView = (TextView) relativeLayoutView.findViewById(R.id.textAttendee);
                    attendees.append(textView.getText().toString()).append(", ");
                }

                String attendeesString = attendees.toString();

                if (attendeesString.length() > 0) {
                    attendeesString = attendeesString.substring(0, attendees.length() - 2);
                }

                boolean isInserted = myDb.insertData(editMeetingName.getText().toString(),
                        attendeesString,
                        showDate.getText().toString(),
                        showTime.getText().toString(),
                        myNotes.getText().toString(),
                        showAddress.getText().toString());

                finish();

                if (isInserted == true)
                    Toast.makeText(CreateMeeting.this, "Meeting Inserted Correctly", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(CreateMeeting.this, "Enter Correct Meeting Format", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpdateData() {
        btnEditMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder attendees = new StringBuilder();

                int count = linearLayout.getChildCount();
                RelativeLayout relativeLayoutView = null;
                for(int i=0; i<count; i++) {
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

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        showTime.setText("Day: " + dayFinal + "   " + "Month: " + monthFinal + "   " + "Year: " + yearFinal);
        showDate.setText("Hour(24): " + hourFinal + "  " + "Minute: " + minuteFinal);

    }

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
