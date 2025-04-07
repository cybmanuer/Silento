//package com.example.autosilent_madapp;
//
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.media.AudioManager;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
//public class TimeBasedService extends Service {
//
//    private static final String TAG = "TimeBasedSilentModeService";
//    private DatabaseHelper dbHelper;
//    private AudioManager audioManager;
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        dbHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        checkTimeAndSetSilentMode();
//        return START_STICKY;
//    }
//
//    private void checkTimeAndSetSilentMode() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//        String currentDate = dateFormat.format(calendar.getTime());
//        String currentTime = timeFormat.format(calendar.getTime());
//
//        Cursor cursor = dbHelper.getAllTimerData();
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int dateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
//                int startTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
//                int endTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);
//
//                if (dateColumnIndex >= 0 && startTimeColumnIndex >= 0 && endTimeColumnIndex >= 0) {
//                    String savedDate = cursor.getString(dateColumnIndex);
//                    String startTime = cursor.getString(startTimeColumnIndex);
//                    String endTime = cursor.getString(endTimeColumnIndex);
//
//                    if (savedDate.equals(currentDate) && isTimeBetween(currentTime, startTime, endTime)) {
//                        activateSilentMode();
//                        break; // Exit loop after activating silent mode
//                    } else {
//                        restoreNormalVolume();
//                    }
//                } else {
//                    Log.e(TAG, "One or more columns not found in the cursor");
//                }
//            } while (cursor.moveToNext());
//            cursor.close();
//        } else {
//            Log.i(TAG, "No timer data found in the database");
//            restoreNormalVolume();
//        }
//
//        stopSelf(); // Stop the service after checking and setting the mode
//    }
//
//    private boolean isTimeBetween(String currentTime, String startTime, String endTime) {
//        try {
//            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            Date currentDate = timeFormat.parse(currentTime);
//            Date startDate = timeFormat.parse(startTime);
//            Date endDate = timeFormat.parse(endTime);
//
//            if (currentDate != null && startDate != null && endDate != null) {
//                return currentDate.after(startDate) && currentDate.before(endDate);
//            }
//            return false;
//        } catch (ParseException e) {
//            Log.e(TAG, "Error parsing time", e);
//            return false;
//        }
//    }
//
//    private void activateSilentMode() {
//        // Save current volume before changing to silent
//        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//
//        // Set ringer mode to silent and all volumes to 0
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//        Log.d(TAG, "Silent Mode Activated by Time");
//    }
//
//    private void restoreNormalVolume() {
//        // Restore previous volume levels
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//        audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//
//        Log.d(TAG, "Volume Restored by Time");
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}



//
//
//package com.example.autosilent_madapp;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.media.AudioManager;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
//public class TimeBasedService extends Service {
//
//    private static final String TAG = "TimeBasedService";
//    private DatabaseHelper dbHelper;
//    private AudioManager audioManager;
//    private Handler handler;
//    private Runnable timeCheckerRunnable;
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Toast.makeText(TimeBasedService.this,"Silent Mode---------------------" , Toast.LENGTH_SHORT).show();
//        dbHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        handler = new Handler();
//        startCheckingTime();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }
//
//    private void startCheckingTime() {
//        timeCheckerRunnable = new Runnable() {
//            @Override
//            public void run() {
//                checkTimeAndSetSilentMode();
//                handler.postDelayed(this, 60000); // Check every 60 seconds
//            }
//        };
//        handler.post(timeCheckerRunnable);
//    }
////
////    private void checkTimeAndSetSilentMode() {
////        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
////        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
////        Calendar calendar = Calendar.getInstance();
////        String currentDate = dateFormat.format(calendar.getTime());
////        String currentTime = timeFormat.format(calendar.getTime());
////
////        Cursor cursor = dbHelper.getAllTimerData();
////        boolean isSilentModeActivated = false;
////
////        if (cursor != null) {
////            try {
////                if (cursor.moveToFirst()) {
////                    do {
////                        int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
////                        int startIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
////                        int endIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);
////
////                        if (dateIndex != -1 && startIndex != -1 && endIndex != -1) {
////                            String savedDate = cursor.getString(dateIndex);
////                            String startTime = cursor.getString(startIndex);
////                            String endTime = cursor.getString(endIndex);
////
////                            if (savedDate != null && startTime != null && endTime != null) {
////                                if (savedDate.equals(currentDate) && isTimeBetween(currentTime, startTime, endTime)) {
////                                    activateSilentMode();
////                                    isSilentModeActivated = true;
////                                    break;  // No need to check further once silent mode is activated
////                                }
////                            }
////                        } else {
////                            Log.e(TAG, "One or more columns are missing in the database.");
////                        }
////
////                    } while (cursor.moveToNext());
////                }
////            } finally {
////                cursor.close();  // Ensures the cursor is closed even if an exception occurs
////            }
////        }
////
////        if (!isSilentModeActivated) {
////            restoreNormalVolume();
////        }
////    }
//
//
//
//    private void checkTimeAndSetSilentMode() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//        String currentDate = dateFormat.format(calendar.getTime());
//        String currentTime = timeFormat.format(calendar.getTime());
//
//        Cursor cursor = dbHelper.getAllTimerData();
//        boolean isSilentModeActivated = false;
//        boolean isTimeMatched = false;  // Track if any time matches
//
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    do {
//                        int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
//                        int startIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
//                        int endIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);
//
//                        if (dateIndex != -1 && startIndex != -1 && endIndex != -1) {
//                            String savedDate = cursor.getString(dateIndex);
//                            String startTime = cursor.getString(startIndex);
//                            String endTime = cursor.getString(endIndex);
//
//                            if (savedDate != null && startTime != null && endTime != null) {
//                                if (savedDate.equals(currentDate) && isTimeBetween(currentTime, startTime, endTime)) {
//                                    activateSilentMode();
//                                    isSilentModeActivated = true;
//                                    isTimeMatched = true;  // Time matched with DB
//
//                                    // Show Toast for match
//                                    Toast.makeText(getApplicationContext(), "Silent Mode Activated! Time Matched: " + currentTime, Toast.LENGTH_SHORT).show();
//                                    break;  // Stop checking after first match
//                                }
//                            }
//                        } else {
//                            Log.e(TAG, "One or more columns are missing in the database.");
//                        }
//
//                    } while (cursor.moveToNext());
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//
//        if (!isSilentModeActivated) {
//            restoreNormalVolume();
//        }
//
//        // Show Toast if no match was found
//        if (!isTimeMatched) {
//            Toast.makeText(getApplicationContext(), "No matching time found! Current Time: " + currentTime, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//
//
//
//
//    private boolean isTimeBetween(String currentTime, String startTime, String endTime) {
//        try {
//            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            Date currentDate = timeFormat.parse(currentTime);
//            Date startDate = timeFormat.parse(startTime);
//            Date endDate = timeFormat.parse(endTime);
//
//            if (currentDate != null && startDate != null && endDate != null) {
//                return !currentDate.before(startDate) && !currentDate.after(endDate);
//            }
//            return false;
//        } catch (ParseException e) {
//            Log.e(TAG, "Error parsing time", e);
//            return false;
//        }
//    }
//
//    private void activateSilentMode() {
//        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//        Log.d(TAG, "Silent Mode Activated by Time");
//    }
//
//    private void restoreNormalVolume() {
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//        audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//
//        Log.d(TAG, "Volume Restored by Time");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (handler != null && timeCheckerRunnable != null) {
//            handler.removeCallbacks(timeCheckerRunnable);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}



package com.example.autosilent_madapp;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeBasedService extends Service {
    private static final String TAG = "TimeService";
    private Handler handler = new Handler();
    private Runnable runnable;
    private DatabaseHelper dbHelper;
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Runnable to check the current time every minute
        runnable = new Runnable() {
            @Override
            public void run() {
                checkAndActivateSilentMode();
                handler.postDelayed(this, 60000); // Check every 1 minute
            }
        };
        handler.post(runnable); // Start the time check
    }

    private void checkAndActivateSilentMode() {
        Cursor cursor = dbHelper.getAllTimerData(); // Fetch all timer data
        if (cursor != null) {
            Log.d(TAG, "Checking timer data...");

            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            while (cursor.moveToNext()) {
                int startTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);

                if (startTimeIndex == -1 || endTimeIndex == -1) {
                    Log.e(TAG, "Column not found: start_time or end_time");
                    continue; // Skip this row
                }

                String startTime = cursor.getString(startTimeIndex);
                String endTime = cursor.getString(endTimeIndex);

                Log.d(TAG, "Start Time: " + startTime + ", End Time: " + endTime);

                if (currentTime.equals(startTime)) {
                    activateSilentMode();
                } else if (currentTime.equals(endTime)) {
                    restoreNormalVolume();
                }
            }
            cursor.close();
        } else {
            Log.d(TAG, "Cursor is null");
        }
    }

    private void activateSilentMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.d(TAG, "Silent Mode Activated");
    }

    private void restoreNormalVolume() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.d(TAG, "Normal Volume Restored");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable); // Stop the time check
        super.onDestroy();
    }
}