//
//
//package com.example.autosilent_madapp;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.CalendarView;
//import android.widget.TimePicker;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.Calendar;
//
//public class TimerActivity extends AppCompatActivity {
//    private CalendarView calendarView;
//    private TimePicker timePickerStart, timePickerEnd;
//    private Button btnSaveTimer;
//    private int selectedYear, selectedMonth, selectedDay;
//    private int startHour, startMinute, endHour, endMinute;
//    private Calendar currentCalendar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_timer);
//
//        calendarView = findViewById(R.id.calendarView);
//        timePickerStart = findViewById(R.id.timePickerStart);
//        timePickerEnd = findViewById(R.id.timePickerEnd);
//        btnSaveTimer = findViewById(R.id.btnSaveTimer);
//
//        // Initialize with current date
//        currentCalendar = Calendar.getInstance();
//        selectedYear = currentCalendar.get(Calendar.YEAR);
//        selectedMonth = currentCalendar.get(Calendar.MONTH);
//        selectedDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
//
//        // Restrict past date selection
//        calendarView.setMinDate(System.currentTimeMillis() - 1000);
//
//        // CalendarView Date Selection
//        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
//            Calendar selectedDate = Calendar.getInstance();
//            selectedDate.set(year, month, dayOfMonth);
//
//            if (selectedDate.before(currentCalendar)) {
//                Toast.makeText(this, "Cannot select past dates!", Toast.LENGTH_SHORT).show();
//            } else {
//                selectedYear = year;
//                selectedMonth = month;
//                selectedDay = dayOfMonth;
//            }
//        });
//
//        // TimePicker Listeners
//        timePickerStart.setOnTimeChangedListener((view, hourOfDay, minute) -> {
//            startHour = hourOfDay;
//            startMinute = minute;
//        });
//
//        timePickerEnd.setOnTimeChangedListener((view, hourOfDay, minute) -> {
//            endHour = hourOfDay;
//            endMinute = minute;
//        });
//
//        // Save Button Click Event
//        btnSaveTimer.setOnClickListener(v -> {
//            Calendar selectedDate = Calendar.getInstance();
//            selectedDate.set(selectedYear, selectedMonth, selectedDay);
//
//            if (selectedDate.before(currentCalendar)) {
//                Toast.makeText(this, "Please select a valid future date!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Validate Start Time < End Time
//            if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
//                Toast.makeText(this, "Start time must be before End time!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // If all validations pass
//            String selectedDateStr = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
//            String startTime = String.format("%02d:%02d", startHour, startMinute);
//            String endTime = String.format("%02d:%02d", endHour, endMinute);
//
//            Toast.makeText(TimerActivity.this,
//                    "Date: " + selectedDateStr + "\nStart: " + startTime + "\nEnd: " + endTime,
//                    Toast.LENGTH_LONG).show();
//        });
//    }
//}




//--------------------------------------------------- it stores the time and date but time is not correctly stored
//
//package com.example.autosilent_madapp;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.CalendarView;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Calendar;
//
//public class TimerActivity extends AppCompatActivity {
//    private CalendarView calendarView;
//    private TimePicker timePickerStart, timePickerEnd;
//    private Button btnSaveTimer;
//    private int selectedYear, selectedMonth, selectedDay;
//    private int startHour, startMinute, endHour, endMinute;
//    private Calendar currentCalendar;
//    private DatabaseHelper dbHelper; // Add DatabaseHelper reference
//    private DatabaseHelper databaseHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
////        databaseHelper = new DatabaseHelper(this);
//
//        setContentView(R.layout.activity_timer);
//
//        calendarView = findViewById(R.id.calendarView);
//        timePickerStart = findViewById(R.id.timePickerStart);
//        timePickerEnd = findViewById(R.id.timePickerEnd);
//        btnSaveTimer = findViewById(R.id.btnSaveTimer);
//
//        // Initialize the DatabaseHelper
//        dbHelper = new DatabaseHelper(this);
//
//        // Initialize with current date
//        currentCalendar = Calendar.getInstance();
//        selectedYear = currentCalendar.get(Calendar.YEAR);
//        selectedMonth = currentCalendar.get(Calendar.MONTH);
//        selectedDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
//
//        // Restrict past date selection
//        calendarView.setMinDate(System.currentTimeMillis() - 1000);
//
//        // CalendarView Date Selection
//        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
//            Calendar selectedDate = Calendar.getInstance();
//            selectedDate.set(year, month, dayOfMonth);
//
//            if (selectedDate.before(currentCalendar)) {
//                Toast.makeText(this, "Cannot select past dates!", Toast.LENGTH_SHORT).show();
//            } else {
//                selectedYear = year;
//                selectedMonth = month;
//                selectedDay = dayOfMonth;
//            }
//        });
//
//        // TimePicker Listeners
//        timePickerStart.setOnTimeChangedListener((view, hourOfDay, minute) -> {
//            startHour = hourOfDay;
//            startMinute = minute;
//        });
//
//        timePickerEnd.setOnTimeChangedListener((view, hourOfDay, minute) -> {
//            endHour = hourOfDay;
//            endMinute = minute;
//        });
//
//        // Save Button Click Event
//        btnSaveTimer.setOnClickListener(v -> {
//            Calendar selectedDate = Calendar.getInstance();
//            selectedDate.set(selectedYear, selectedMonth, selectedDay);
//
//            if (selectedDate.before(currentCalendar)) {
//                Toast.makeText(this, "Please select a valid future date!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Validate Start Time < End Time
//            if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
//                Toast.makeText(this, "Start time must be before End time!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // If all validations pass
//            String selectedDateStr = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
//            String startTime = String.format("%02d:%02d", startHour, startMinute);
//            String endTime = String.format("%02d:%02d", endHour, endMinute);
//
//            // Save to database
//            long result = dbHelper.insertTimerData(selectedDateStr, startTime, endTime);
//            if (result != -1) {
//                Toast.makeText(this, "Timer saved successfully!", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Failed to save timer.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}



//----------------------------- udating time storing method


package com.example.autosilent_madapp;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class TimerActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TimePicker timePickerStart, timePickerEnd;
    private Button btnSaveTimer;
    private int selectedYear, selectedMonth, selectedDay;
    private Calendar currentCalendar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        calendarView = findViewById(R.id.calendarView);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);
        btnSaveTimer = findViewById(R.id.btnSaveTimer);

        // Initialize the DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize with current date
        currentCalendar = Calendar.getInstance();
        selectedYear = currentCalendar.get(Calendar.YEAR);
        selectedMonth = currentCalendar.get(Calendar.MONTH);
        selectedDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // Restrict past date selection
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        // CalendarView Date Selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
        });

        // Save Button Click Event
        btnSaveTimer.setOnClickListener(v -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            int startHour, startMinute, endHour, endMinute;

            // Get the current API version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Use getHour() and getMinute() for API 23 and above
                startHour = timePickerStart.getHour();
                startMinute = timePickerStart.getMinute();
                endHour = timePickerEnd.getHour();
                endMinute = timePickerEnd.getMinute();
            } else {
                // Use getCurrentHour() and getCurrentMinute() for API less than 23
                startHour = timePickerStart.getCurrentHour();
                startMinute = timePickerStart.getCurrentMinute();
                endHour = timePickerEnd.getCurrentHour();
                endMinute = timePickerEnd.getCurrentMinute();
            }

            if (selectedDate.before(currentCalendar)) {
                Toast.makeText(this, "Please select a valid future date!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate Start Time < End Time
            if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                Toast.makeText(this, "Start time must be before End time!", Toast.LENGTH_SHORT).show();
                return;
            }

            // If all validations pass
            String selectedDateStr = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            String startTime = String.format("%02d:%02d", startHour, startMinute);
            String endTime = String.format("%02d:%02d", endHour, endMinute);

            // Save to database
            long result = dbHelper.insertTimerData(selectedDateStr, startTime, endTime);
            if (result != -1) {
                Toast.makeText(this, "Timer saved successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to save timer.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}




