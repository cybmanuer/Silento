//package com.example.autosilent_madapp;
//
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.location.Location;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//public class LocationService extends Service {
//
//    private static final String CHANNEL_ID = "LocationMonitoringService";
//    private boolean isMonitoring = false;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private DatabaseHelper databaseHelper;
//    private AudioManager audioManager;
//
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        databaseHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//                for (Location location : locationResult.getLocations()) {
//                    checkLocationAndActivateSilentMode(location);
//                }
//            }
//        };
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
//            stopLocationMonitoring();
//            stopSelf();
//        } else {
//            startLocationMonitoring();
//            createNotification();
//        }
//        return START_STICKY;
//    }
//
//    private void createNotification() {
//        // Create a notification channel for Android Oreo and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Location Monitoring Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Location Monitoring Service")
//                .setContentText("Monitoring location for automatic silent mode activation.")
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//    }
//
//    private void startLocationMonitoring() {
//        if (isMonitoring) return;
//        isMonitoring = true;
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("LocationService", "Location permission not granted.");
//            return;
//        }
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationMonitoring() {
//        if (!isMonitoring) return;
//        isMonitoring = false;
//
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    private void checkLocationAndActivateSilentMode(Location currentLocation) {
//        Cursor cursor = databaseHelper.getAllLocations();
//        if (cursor != null) {
//            try {
//                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
//                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);
//
//                if (latitudeIndex < 0 || longitudeIndex < 0) {
//                    Log.e("LocationService", "Invalid column index in database.");
//                    return;
//                }
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        double latitude = cursor.getDouble(latitudeIndex);
//                        double longitude = cursor.getDouble(longitudeIndex);
//                        Location storedLocation = new Location("");
//                        storedLocation.setLatitude(latitude);
//                        storedLocation.setLongitude(longitude);
//
//                        float distance = currentLocation.distanceTo(storedLocation);
//                        if (distance <= 50) { // Within 50 meters
//                            activateSilentMode();
//                            return;
//                        }
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("LocationService", "Error: " + e.getMessage());
//            } finally {
//                cursor.close();
//            }
//        }
//        restoreNormalVolume();
//    }
//
//    private void activateSilentMode() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//            previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//            Log.d("LocationService", "Silent Mode Activated");
//        } catch (Exception e) {
//            Log.e("LocationService", "Error: " + e.getMessage());
//        }
//    }
//
//    private void restoreNormalVolume() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//
//            Log.d("LocationService", "Normal Mode Restored");
//        } catch (Exception e) {
//            Log.e("LocationService", "Error: " + e.getMessage());
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopLocationMonitoring();
//        Log.d("LocationService", "Service Destroyed");
//    }
//}
/////   ---------------- CORECTION AFTER THE UPDATE ----------------------

//version 2   its work correctly
//package com.example.autosilent_madapp;
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.location.Location;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//public class LocationService extends Service {
//
//    private static final String CHANNEL_ID = "LocationMonitoringService";
//    private boolean isMonitoring = false;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private DatabaseHelper databaseHelper;
//    private AudioManager audioManager;
//
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//
//    // Variable to track if the app activated silent mode
//    private boolean appActivatedSilentMode = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        databaseHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//                for (Location location : locationResult.getLocations()) {
//                    checkLocationAndActivateSilentMode(location);
//                }
//            }
//        };
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
//            stopLocationMonitoring();
//            restoreNormalVolume(); // Restore volume when service is stopped
//            stopSelf();
//        } else {
//            // Save current volume settings before starting location monitoring
//            saveCurrentVolumeSettings();
//            startLocationMonitoring();
//            createNotification();
//        }
//        return START_STICKY;
//    }
//
//    private void createNotification() {
//        // Create a notification channel for Android Oreo and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Location Monitoring Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Location Monitoring Service")
//                .setContentText("Monitoring location for automatic silent mode activation.")
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//    }
//
//    private void startLocationMonitoring() {
//        if (isMonitoring) return;
//        isMonitoring = true;
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("LocationService", "Location permission not granted.");
//            return;
//        }
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationMonitoring() {
//        if (!isMonitoring) return;
//        isMonitoring = false;
//
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    private void saveCurrentVolumeSettings() {
//        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//    }
//
//    private void checkLocationAndActivateSilentMode(Location currentLocation) {
//        Cursor cursor = databaseHelper.getAllLocations();
//        if (cursor != null) {
//            try {
//                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
//                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);
//
//                if (latitudeIndex < 0 || longitudeIndex < 0) {
//                    Log.e("LocationService", "Invalid column index in database.");
//                    return;
//                }
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        double latitude = cursor.getDouble(latitudeIndex);
//                        double longitude = cursor.getDouble(longitudeIndex);
//                        Location storedLocation = new Location("");
//                        storedLocation.setLatitude(latitude);
//                        storedLocation.setLongitude(longitude);
//
//                        float distance = currentLocation.distanceTo(storedLocation);
//                        if (distance <= 50) { // Within 50 meters
//                            // Check if the user has already enabled silent mode
//                            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
//                                activateSilentMode();
//                                appActivatedSilentMode = true;
//                            }
//                            return;
//                        }
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("LocationService", "Error: " + e.getMessage());
//            } finally {
//                cursor.close();
//            }
//        }
//        // If outside silent zone, restore volume only if the app activated silent mode
//        if (appActivatedSilentMode) {
//            restoreNormalVolume();
//            appActivatedSilentMode = false;
//        }
//    }
//
//    private void activateSilentMode() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            // Set ringer mode to silent and all volumes to 0
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//            Log.d("LocationService", "Silent Mode Activated");
//        } catch (Exception e) {
//            Log.e("LocationService", "Error: " + e.getMessage());
//        }
//    }
//
//    private void restoreNormalVolume() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            // Restore previous volume levels for each stream
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//
//            // Set ringer mode back to normal
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//
//            Log.d("LocationService", "Normal Mode Restored");
//        } catch (Exception e) {
//            Log.e("LocationService", "Error: " + e.getMessage());
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopLocationMonitoring();
//        Log.d("LocationService", "Service Destroyed");
//    }
//}


//--------------------------------- ADDING NOTIFICATION BAR
package com.example.autosilent_madapp;
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.location.Location;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//public class LocationService extends Service {
//
//    private static final String CHANNEL_ID = "LocationMonitoringService";
//    private boolean isMonitoring = false;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private DatabaseHelper databaseHelper;
//    private AudioManager audioManager;
//
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//
//    // Variable to track if the app activated silent mode
//    private boolean appActivatedSilentMode = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        databaseHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//                for (Location location : locationResult.getLocations()) {
//                    checkLocationAndActivateSilentMode(location);
//                }
//            }
//        };
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
//            stopLocationMonitoring();
//            restoreNormalVolume(); // Restore volume when service is stopped
//            stopSelf();
//        } else {
//            // Save current volume settings before starting location monitoring
//            saveCurrentVolumeSettings();
//            startLocationMonitoring();
//            createNotification();
//        }
//        return START_STICKY;
//    }
//    //notification bar---------------------------------------------------------
//
//    private void createNotification() {
//        // Create a notification channel for Android Oreo and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Location Monitoring Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//
//
//        //notification messageeee------
////        String statusText = "Monitoring location. Silent mode: " + (appActivatedSilentMode ? "Active" : "Inactive");
//        String statusText = "Silent mode: " + (appActivatedSilentMode ? "Active" : "Inactive");
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle("Silento")
//                .setContentText(statusText)
//                .setColor(ContextCompat.getColor(this, R.color.purple_500)) // Set the color theme
//                .setContentIntent(pendingIntent)
//                .setOngoing(true); // Make the notification non-dismissable
//
//        Notification notification = builder.build();
//
//        startForeground(1, notification);
//    }
//
//    private void startLocationMonitoring() {
//        if (isMonitoring) return;
//        isMonitoring = true;
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("LocationService", "Location permission not granted.");
//            return;
//        }
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationMonitoring() {
//        if (!isMonitoring) return;
//        isMonitoring = false;
//
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    private void saveCurrentVolumeSettings() {
//        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//    }
//
//    private void checkLocationAndActivateSilentMode(Location currentLocation) {
//        Cursor cursor = databaseHelper.getAllLocations();
//        if (cursor != null) {
//            try {
//                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
//                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);
//
//                if (latitudeIndex < 0 || longitudeIndex < 0) {
//                    Log.e("LocationService", "Invalid column index in database.");
//                    return;
//                }
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        double latitude = cursor.getDouble(latitudeIndex);
//                        double longitude = cursor.getDouble(longitudeIndex);
//                        Location storedLocation = new Location("");
//                        storedLocation.setLatitude(latitude);
//                        storedLocation.setLongitude(longitude);
//
//                        float distance = currentLocation.distanceTo(storedLocation);
//                        if (distance <= 50) { // Within 50 meters
//                            // Check if the user has already enabled silent mode
//                            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
//                                activateSilentMode();
//                                appActivatedSilentMode = true;
//                            }
//                            return;
//                        }
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("LocationService", "Error: " + e.getMessage());
//            } finally {
//                cursor.close();
//            }
//        }
//        // If outside silent zone, restore volume only if the app activated silent mode
//        if (appActivatedSilentMode) {
//            restoreNormalVolume();
//            appActivatedSilentMode = false;
//        }
//    }
//
//    private void activateSilentMode() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            // Set ringer mode to silent and all volumes to 0
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//            Log.d("LocationService", "Silent Mode Activated");
//            // Update the notification to show the new status
//            createNotification();
//        } catch (Exception e) {
//            Log.e("LocationService", "Error: " + e.getMessage());
//        }
//    }
//
//    private void restoreNormalVolume() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            // Restore previous volume levels for each stream
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//
//            // Set ringer mode back to normal
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//
//            Log.d("LocationService", "Normal Mode Restored");
//            // Update the notification to show the new status
//            createNotification();
//        } catch (Exception e) {
//            Log.e("LocationService", "Error: " + e.getMessage());
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopLocationMonitoring();
//        Log.d("LocationService", "Service Destroyed");
//    }
//}


//version 5 modifying notification bar------- good



import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    private static final String CHANNEL_ID = "LocationMonitoringService";
    private boolean isMonitoring = false;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseHelper databaseHelper;
    private AudioManager audioManager;

    private int previousRingVolume;
    private int previousMusicVolume;
    private int previousAlarmVolume;

    // Variable to track if the app activated silent mode
    private boolean appActivatedSilentMode = false;

    @Override
    public void onCreate() {
        super.onCreate();

        databaseHelper = new DatabaseHelper(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    checkLocationAndActivateSilentMode(location);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
            stopLocationMonitoring();
            restoreNormalVolume(); // Restore volume when service is stopped
            stopSelf();
        } else {
            // Save current volume settings before starting location monitoring
            saveCurrentVolumeSettings();
            startLocationMonitoring();
            // Ensure the notification is created with the correct initial status
            createNotification();
        }
        return START_STICKY;
    }

    private void createNotification() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Monitoring Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        String statusText = "Silent mode: " + (appActivatedSilentMode ? "Active" : "Inactive");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("AutoSilent App")
                .setContentText(statusText)
                .setColor(ContextCompat.getColor(this, R.color.purple_500)) // Set the color theme
                .setContentIntent(pendingIntent)
                .setOngoing(true); // Make the notification non-dismissable

        Notification notification = builder.build();

        startForeground(1, notification);
    }

    private void startLocationMonitoring() {
        if (isMonitoring) return;
        isMonitoring = true;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationService", "Location permission not granted.");
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationMonitoring() {
        if (!isMonitoring) return;
        isMonitoring = false;

        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void saveCurrentVolumeSettings() {
        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
    }

    private void checkLocationAndActivateSilentMode(Location currentLocation) {
        Cursor cursor = databaseHelper.getAllLocations();
        if (cursor != null) {
            try {
                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);

                if (latitudeIndex < 0 || longitudeIndex < 0) {
                    Log.e("LocationService", "Invalid column index in database.");
                    return;
                }

                if (cursor.moveToFirst()) {
                    do {
                        double latitude = cursor.getDouble(latitudeIndex);
                        double longitude = cursor.getDouble(longitudeIndex);
                        Location storedLocation = new Location("");
                        storedLocation.setLatitude(latitude);
                        storedLocation.setLongitude(longitude);

                        float distance = currentLocation.distanceTo(storedLocation);
                        if (distance <= 50) { // Within 50 meters
                            // Check if the user has already enabled silent mode
                            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                                activateSilentMode();
                                appActivatedSilentMode = true;
                            }
                            // Update the notification to reflect the silent mode status
                            createNotification();
                            return;
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e("LocationService", "Error: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        // If outside silent zone, restore volume only if the app activated silent mode
        if (appActivatedSilentMode) {
            restoreNormalVolume();
            appActivatedSilentMode = false;
        }
        // Update the notification to reflect the silent mode status
        createNotification();
    }

    private void activateSilentMode() {
        if (audioManager == null) {
            Log.e("LocationService", "AudioManager is not available.");
            return;
        }

        try {
            // Set ringer mode to silent and all volumes to 0
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);

            Log.d("LocationService", "Silent Mode Activated");
            // Update the notification to show the new status
            createNotification();
        } catch (Exception e) {
            Log.e("LocationService", "Error: " + e.getMessage());
        }
    }

    private void restoreNormalVolume() {
        if (audioManager == null) {
            Log.e("LocationService", "AudioManager is not available.");
            return;
        }

        try {
            // Restore previous volume levels for each stream
            audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);

            // Set ringer mode back to normal
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            Log.d("LocationService", "Normal Mode Restored");
            // Update the notification to show the new status
            createNotification();
        } catch (Exception e) {
            Log.e("LocationService", "Error: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationMonitoring();
        Log.d("LocationService", "Service Destroyed");
    }
}



//VERSION 7 MERGING LOCATION AND TIME SERVICE TO ONE............................
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.location.Location;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.Log;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
//public class LocationService extends Service {
//
//    private static final String CHANNEL_ID = "LocationMonitoringService";
//    private boolean isMonitoring = false;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private DatabaseHelper databaseHelper;
//    private AudioManager audioManager;
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//    private boolean appActivatedSilentMode = false;
//    private boolean isLocationBasedSilent = false;
//    private boolean isTimeBasedSilent = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        databaseHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//                for (Location location : locationResult.getLocations()) {
//                    // Priority to Time Based service, if the timebased is off, then Location service may be performed
//                    if (!isTimeBasedSilent) {
//                        checkLocationAndActivateSilentMode(location);
//                    }
//                }
//            }
//        };
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
//            stopLocationMonitoring();
//            restoreNormalVolume();
//            stopSelf();
//        } else {
//            saveCurrentVolumeSettings();
//            startLocationMonitoring();
//            // Ensure time-based checks are performed regularly
//            checkTimeAndSetSilentMode();
//            createNotification();
//        }
//        return START_STICKY;
//    }
//
//    private void createNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Location Monitoring Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        String statusText = "Silent mode: ";
//        if (isTimeBasedSilent) {
//            statusText += "Active (Time)";
//        } else if (isLocationBasedSilent) {
//            statusText += "Active (Location)";
//        } else {
//            statusText += "Inactive";
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle("AutoSilent App")
//                .setContentText(statusText)
//                .setColor(ContextCompat.getColor(this, R.color.purple_500))
//                .setContentIntent(pendingIntent)
//                .setOngoing(true);
//
//        Notification notification = builder.build();
//        startForeground(1, notification);
//    }
//
//    private void startLocationMonitoring() {
//        if (isMonitoring) return;
//        isMonitoring = true;
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("LocationService", "Location permission not granted.");
//            return;
//        }
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationMonitoring() {
//        if (!isMonitoring) return;
//        isMonitoring = false;
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    private void saveCurrentVolumeSettings() {
//        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//    }
//
//    private void checkLocationAndActivateSilentMode(Location currentLocation) {
//        Cursor cursor = databaseHelper.getAllLocations();
//        if (cursor != null) {
//            try {
//                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
//                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);
//
//                if (latitudeIndex < 0 || longitudeIndex < 0) {
//                    Log.e("LocationService", "Invalid column index in database.");
//                    return;
//                }
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        double latitude = cursor.getDouble(latitudeIndex);
//                        double longitude = cursor.getDouble(longitudeIndex);
//                        Location storedLocation = new Location("");
//                        storedLocation.setLatitude(latitude);
//                        storedLocation.setLongitude(longitude);
//
//                        float distance = currentLocation.distanceTo(storedLocation);
//                        if (distance <= 50) {
//                            // Activate silent mode only if it's not already active due to time
//                            if (!isTimeBasedSilent && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
//                                activateSilentMode(true);
//                                isLocationBasedSilent = true;
//                                appActivatedSilentMode = true;
//                                Log.d("LocationService", "Location-based silent mode activated.");
//                            }
//                            createNotification();
//                            return;
//                        }
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("LocationService", "Error: " + e.getMessage());
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//
//        // Restore volume only if location-based silent mode was active and no longer meets criteria
//        if (isLocationBasedSilent) {
//            restoreNormalVolume();
//            isLocationBasedSilent = false;
//            appActivatedSilentMode = false;
//            Log.d("LocationService", "Location-based silent mode deactivated.");
//            createNotification();
//        }
//    }
//
//    private void checkTimeAndSetSilentMode() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//        String currentDate = dateFormat.format(calendar.getTime());
//        String currentTime = timeFormat.format(calendar.getTime());
//
//        Cursor cursor = databaseHelper.getAllTimerData();
//
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    do {
//                        int dateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
//                        int startTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
//                        int endTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);
//
//                        if (dateColumnIndex >= 0 && startTimeColumnIndex >= 0 && endTimeColumnIndex >= 0) {
//                            String savedDate = cursor.getString(dateColumnIndex);
//                            String startTime = cursor.getString(startTimeColumnIndex);
//                            String endTime = cursor.getString(endTimeColumnIndex);
//
//                            if (savedDate.equals(currentDate)) {
//                                Log.d("LocationService", "Current Time: " + currentTime + ", Start Time: " + startTime + ", End Time: " + endTime);
//                                if (isTimeBetween(currentTime, startTime, endTime)) {
//                                    // Activate silent mode only if it's not already active, and set time-based flag
//                                    if (!isTimeBasedSilent && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
//                                        activateSilentMode(false); // Deactivate location-based checks
//                                        isTimeBasedSilent = true;
//                                        isLocationBasedSilent = false;
//                                        appActivatedSilentMode = true;
//                                        Log.d("LocationService", "Time-based silent mode activated.");
//                                    }
//                                    createNotification();
//                                    return; // Exit loop after activating based on time
//                                } else {
//                                    // Restore volume only if it was time-based and the time has passed
//                                    if (isTimeBasedSilent) {
//                                        restoreNormalVolume();
//                                        isTimeBasedSilent = false;
//                                        appActivatedSilentMode = false;
//                                        Log.d("LocationService", "Time-based silent mode deactivated.");
//                                    }
//                                }
//                            }
//                        } else {
//                            Log.e("LocationService", "One or more time columns not found in the cursor");
//                        }
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("LocationService", "Error processing timer data: " + e.getMessage());
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        } else {
//            Log.i("LocationService", "No timer data found in the database");
//        }
//        // Create Notification with status
//        createNotification();
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
//            Log.e("LocationService", "Error parsing time", e);
//            return false;
//        }
//    }
//
//    private void activateSilentMode(boolean isLocationBased) {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            // Save current volume settings before activating silent mode
//
//            // Set ringer mode to silent and all volumes to 0
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//            appActivatedSilentMode = true;
//            Log.d("LocationService", "Silent Mode Activated");
//            // Update the notification to show the new status
//            createNotification();
//            if (!isLocationBased) {
//                stopLocationMonitoring();
//            }
//
//        } catch (SecurityException e) {
//            Log.e("LocationService", "Security Exception: " + e.getMessage(), e);
//        }
//    }
//
//    private void restoreNormalVolume() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            // Restore previous volume levels for each stream
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//            // Set ringer mode back to normal
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            appActivatedSilentMode = false;
//            Log.d("LocationService", "Normal Mode Restored");
//            isTimeBasedSilent = false;
//            isLocationBasedSilent = false;
//            // Update the notification to show the new status
//            createNotification();
//        } catch (SecurityException e) {
//            Log.e("LocationService", "Security Exception: " + e.getMessage(), e);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopLocationMonitoring();
//        Log.d("LocationService", "Service Destroyed");
//    }
//}


//--------------------------------------------------
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.location.Location;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
//
//public class LocationService extends Service {
//
//    private static final String CHANNEL_ID = "LocationMonitoringService";
//    private boolean isMonitoring = false;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private DatabaseHelper databaseHelper;
//    private AudioManager audioManager;
//    private int previousRingVolume;
//    private int previousMusicVolume;
//    private int previousAlarmVolume;
//    private boolean appActivatedSilentMode = false;
//    private boolean isLocationBasedSilent = false;
//    private boolean isTimeBasedSilent = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        databaseHelper = new DatabaseHelper(this);
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//                for (Location location : locationResult.getLocations()) {
//                    // Check location-based silent mode when time-based mode is inactive
//                    if (!isTimeBasedSilent) {
//                        checkLocationAndActivateSilentMode(location);
//                    }
//                }
//            }
//        };
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
//            stopLocationMonitoring();
//            restoreNormalVolume();
//            stopSelf();
//        } else {
//            saveCurrentVolumeSettings();
//            startLocationMonitoring();
//            checkTimeAndSetSilentMode(); // Check time-based silencing
//            createNotification();
//        }
//        return START_STICKY;
//    }
//
//    private void createNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Location Monitoring Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        String statusText = "Silent mode: ";
//        if (isTimeBasedSilent) {
//            statusText += "Active (Time)";
//        } else if (isLocationBasedSilent) {
//            statusText += "Active (Location)";
//        } else {
//            statusText += "Inactive";
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle("AutoSilent App")
//                .setContentText(statusText)
//                .setColor(ContextCompat.getColor(this, R.color.purple_500))
//                .setContentIntent(pendingIntent)
//                .setOngoing(true);
//
//        Notification notification = builder.build();
//        startForeground(1, notification);
//    }
//
//    private void startLocationMonitoring() {
//        if (isMonitoring) return;
//        isMonitoring = true;
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("LocationService", "Location permission not granted.");
//            return;
//        }
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationMonitoring() {
//        if (!isMonitoring) return;
//        isMonitoring = false;
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    private void saveCurrentVolumeSettings() {
//        previousRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//    }
//
//    private void checkLocationAndActivateSilentMode(Location currentLocation) {
//        Cursor cursor = databaseHelper.getAllLocations();
//        if (cursor != null) {
//            try {
//                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
//                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);
//
//                if (latitudeIndex < 0 || longitudeIndex < 0) {
//                    Log.e("LocationService", "Invalid column index in database.");
//                    return;
//                }
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        double latitude = cursor.getDouble(latitudeIndex);
//                        double longitude = cursor.getDouble(longitudeIndex);
//                        Location storedLocation = new Location("");
//                        storedLocation.setLatitude(latitude);
//                        storedLocation.setLongitude(longitude);
//
//                        float distance = currentLocation.distanceTo(storedLocation);
//                        if (distance <= 50) {
//                            // Activate silent mode only if it's not already active due to time
//                            if (!isTimeBasedSilent && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
//                                activateSilentMode(true);
//                                isLocationBasedSilent = true;
//                                appActivatedSilentMode = true;
//                                Log.d("LocationService", "Location-based silent mode activated.");
//                            }
//                            createNotification();
//                            return;
//                        }
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("LocationService", "Error: " + e.getMessage());
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//
//        // Restore volume only if location-based silent mode was active and no longer meets criteria
//        if (isLocationBasedSilent) {
//            restoreNormalVolume();
//            isLocationBasedSilent = false;
//            appActivatedSilentMode = false;
//            Log.d("LocationService", "Location-based silent mode deactivated.");
//            createNotification();
//        }
//    }
//
//    private void checkTimeAndSetSilentMode() {
//
//        Toast.makeText(getApplicationContext(), "checktimeandsettime-----------): " , Toast.LENGTH_SHORT).show();
//
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//        String currentTime = timeFormat.format(calendar.getTime());
//
//        Cursor cursor = databaseHelper.getAllTimerData();
//        boolean isSilentModeActivated = false;
//
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    do {
//                        int startIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
//                        int endIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);
//
//                        if (startIndex != -1 && endIndex != -1) {
//                            String startTime = cursor.getString(startIndex);
//                            String endTime = cursor.getString(endIndex);
//
//                            Toast.makeText(getApplicationContext(), "-----------): " + startTime , Toast.LENGTH_SHORT).show();
//                            if (startTime != null && endTime != null) {
//                                if (isTimeBetween(currentTime, startTime, endTime)) {
//                                    activateSilentMode(false);
//                                    isSilentModeActivated = true;
//                                    Toast.makeText(getApplicationContext(), "Silent Mode Activated (Time): " + currentTime, Toast.LENGTH_SHORT).show();
//                                    break;  // Stop checking after first match
//                                }
//                            }
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
//            Toast.makeText(getApplicationContext(), "Silent Mode Restored (No matching time found).", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private boolean isTimeBetween(String currentTime, String startTime, String endTime) {
//        try {
//            Toast.makeText(getApplicationContext(), "-----is time betwen------): " + startTime , Toast.LENGTH_SHORT).show();
//
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
//            Log.e("LocationService", "Error parsing time", e);
//            return false;
//        }
//    }
//
//    private void activateSilentMode(boolean isLocationBased) {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
//
//            appActivatedSilentMode = true;
//            Log.d("LocationService", "Silent Mode Activated");
//            createNotification();
//
//            if (!isLocationBased) {
//                stopLocationMonitoring();
//            }
//
//        } catch (SecurityException e) {
//            Log.e("LocationService", "Security Exception: " + e.getMessage(), e);
//        }
//    }
//
//    private void restoreNormalVolume() {
//        if (audioManager == null) {
//            Log.e("LocationService", "AudioManager is not available.");
//            return;
//        }
//
//        try {
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousMusicVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//
//            appActivatedSilentMode = false;
//            Log.d("LocationService", "Volume Restored");
//            createNotification();
//        } catch (SecurityException e) {
//            Log.e("LocationService", "Error restoring volume: " + e.getMessage());
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
