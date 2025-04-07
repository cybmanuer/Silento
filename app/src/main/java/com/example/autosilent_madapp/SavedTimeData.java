package com.example.autosilent_madapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SavedTimeData extends AppCompatActivity {

    private ListView listViewTimerData;
    private DatabaseHelper databaseHelper;
    private ArrayList<TimerItem> savedTimerData;
    private ArrayAdapter<TimerItem> adapter;
    private static final String TAG = "TimerDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_timedata); // Ensure this matches the XML layout file

        listViewTimerData = findViewById(R.id.listViewTimerData);
        databaseHelper = new DatabaseHelper(this);
        savedTimerData = new ArrayList<>();

        loadTimerData();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedTimerData);
        listViewTimerData.setAdapter(adapter);

        // Set a long-click listener to show delete option
        listViewTimerData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TimerItem selectedItem = savedTimerData.get(position);
                new AlertDialog.Builder(SavedTimeData.this)
                        .setTitle("Delete Timer Data")
                        .setMessage("Do you want to delete this timer data?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int rowsDeleted = databaseHelper.deleteTimerData(selectedItem.getId());
                                if (rowsDeleted > 0) {
                                    savedTimerData.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(SavedTimeData.this, "Timer data deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SavedTimeData.this, "Failed to delete timer data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;  // Indicates the long-click event is consumed
            }
        });
    }



    // Load timer data from the database into the savedTimerData list
    private void loadTimerData() {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getAllTimerData();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Get the column indices
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMER_ID));
                    int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
                    int startTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
                    int endTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);

                    if (dateIndex == -1 || startTimeIndex == -1 || endTimeIndex == -1) {
                        Log.e(TAG, "One or more columns not found in the cursor");
                        continue;
                    }

                    // Extract the data
                    String date = cursor.getString(dateIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);

                    // Create a display string for the item
                    String details =
                                     "Date: " + date +
                                    "\nStart Time: " + startTime +
                                    "\nEnd Time: " + endTime + "\n";

                    // Add the item (with its ID) to the list
                    savedTimerData.add(new TimerItem(id, details));
                } while (cursor.moveToNext());
            } else {
                Log.i(TAG, "No timer data found in the database");
                savedTimerData.add(new TimerItem(-1, "No timer data saved yet."));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching timer data from database", e);
            savedTimerData.add(new TimerItem(-1, "Error fetching timer data."));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Inner class representing a timer item
    private class TimerItem {
        private int id;
        private String details;

        public TimerItem(int id, String details) {
            this.id = id;
            this.details = details;
        }

        public int getId() {
            return id;
        }

        // The adapter will call toString() to display the item in the list
        @Override
        public String toString() {
            return details;
        }
    }
}
