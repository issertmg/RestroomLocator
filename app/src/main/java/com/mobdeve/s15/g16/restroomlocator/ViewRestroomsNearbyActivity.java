package com.mobdeve.s15.g16.restroomlocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.location.LocationManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s15.g16.restroomlocator.utils.AccuracyOverlay;
import com.mobdeve.s15.g16.restroomlocator.utils.IntentKeys;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;


import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ViewRestroomsNearbyActivity extends AppCompatActivity {

    private MapView map = null;
    IMapController mapController;

    // For getting GPS location
    private static LocationManager locationManager;
    private static final int REQUEST_CHECK_SETTINGS = 111;

    // Testing
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000;  /* 1 secs */
    private long FASTEST_INTERVAL = 1000; /* 1 sec */


    private MapEventsOverlay myLocationEventsOverlay;
    private Marker myPin;
    private AccuracyOverlay accuracyOverlay;
    private AlertDialog dialog;

    private MapEventsOverlay newRestroomEventsOverlay;
    private Marker addPin;
    private boolean isAddRestroomMode;

    //Floating action buttons
    FloatingActionButton btnCurrentLocation, btnAddReview;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private CoordinatorLayout cl;
    private Snackbar sb;

    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_view_restrooms_nearby);
        initializeNavigationDrawer();

        // Initialize Floating Action Buttons
        btnCurrentLocation = findViewById(R.id.fab_current_location);
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { pinCurrentLocationToMap(); }
        });

        btnAddReview = findViewById(R.id.fab_add_review);
        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddRestroomMode) {
                    setAddMyLocationMode();
                    sb.dismiss();
                    Intent i = new Intent(ViewRestroomsNearbyActivity.this, AddRestroomActivity.class);
                    i.putExtra(IntentKeys.LATITUDE_KEY, addPin.getPosition().getLatitude());
                    i.putExtra(IntentKeys.LONGITUDE_KEY, addPin.getPosition().getLongitude());
                    addRestroomResultLauncher.launch(i);
                }
                else {
                    setAddRestroomMode();
                }

            }
        });

        // Initialize snackbar
        cl = findViewById(R.id.snackbar_area);
        sb = Snackbar.make(cl, "", Snackbar.LENGTH_INDEFINITE);
        sb.setAction("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.dismiss();
            }
        });
        sb.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                setAddMyLocationMode();
            }
        });

        //FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(getApplicationContext(), "uid: "+ mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();

        // Initialize mapview
        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(8.0);
        mapController = map.getController();
        mapController.setZoom(13.0);
        mapController.setCenter(new GeoPoint(14.5995,120.9842));

        //Check external storage write permission is not granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            map.setVisibility(View.GONE);
            btnAddReview.setVisibility(View.GONE);
            btnCurrentLocation.setVisibility(View.GONE);
            requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        //Initialize user pin
        myPin = new Marker(map);
        myPin.setIcon(getResources().getDrawable(R.drawable.ic_my_location));
        myPin.setVisible(false);
        myPin.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });
        map.getOverlays().add(myPin);

        // Initialize map listener overlay for user pin
        // add map listener overlay to index 0 of Map Overlays List
        myLocationEventsOverlay = new MapEventsOverlay(new MapEventsReceiver(){
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                dialog.show();
                MyFirestoreHelper.displayNearbyRestroomLocations(p, map, dialog,ViewRestroomsNearbyActivity.this);
                showPin(p);
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });
        map.getOverlays().add(0, myLocationEventsOverlay);

        // Initialize add restroom pinpoint
        addPin = new Marker(map);
        addPin.setIcon(getResources().getDrawable(R.drawable.ic_add_restroom));
        addPin.setVisible(false);
        addPin.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });
        map.getOverlays().add(addPin);

        // Initialize map listener for add restroom pin
        final MapEventsReceiver newRestroomReceiver = new MapEventsReceiver(){
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                addPin.setVisible(true);
                addPin.setPosition(p);
                map.invalidate();

                // Show btnAddReview as Check button
                btnAddReview.setImageResource(R.drawable.checkmark);
                btnAddReview.setVisibility(View.VISIBLE);

                sb.setText("Restroom location pinned.");
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        newRestroomEventsOverlay = new MapEventsOverlay(newRestroomReceiver);

        isAddRestroomMode = false;
        markers = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialog_layout);
        dialog = builder.create();

        //pinCurrentLocationToMap();
    }

    public void addMarkerToList(Marker m) {
        this.markers.add(m);
    }

    public void removePreviousRestroomMarkers() {
        for (Marker m : this.markers)
            map.getOverlays().remove(m);
        this.markers.clear();
    }

    private void initializeNavigationDrawer() {
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name, R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setItemIconTintList(null);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.view_profile:
                        startActivity(new Intent(ViewRestroomsNearbyActivity.this, UserProfileActivity.class));
                        break;
                    case R.id.change_password:
                        startActivity(new Intent(ViewRestroomsNearbyActivity.this, ChangePasswordActivity.class));
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ViewRestroomsNearbyActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void askToEnableGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        ViewRestroomsNearbyActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        pinCurrentLocationToMap();
                        Toast.makeText(getApplicationContext(),"GPS enabled.", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(),"GPS is disabled. Current location cannot be determined.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    protected void getLocationOnce() {
        dialog.show();

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(UPDATE_INTERVAL)
        .setFastestInterval(FASTEST_INTERVAL)
        .setWaitForAccurateLocation(true);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        mFusedLocationClient.removeLocationUpdates(this);
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        Toast.makeText(this, "Current location found", Toast.LENGTH_SHORT).show();

        // Create GeoPoint from location
        GeoPoint p = new GeoPoint(location.getLatitude(), location.getLongitude());

        MyFirestoreHelper.displayNearbyRestroomLocations(p, map, dialog, ViewRestroomsNearbyActivity.this);

        //Zoom map to current location
        mapController.setZoom(17.5);
        mapController.setCenter(p);

        showPin(p);
    }

    public void showPin(GeoPoint p) {
        //Pin current location on map
        myPin.setVisible(true);
        myPin.setPosition(p);

        //Add accuracy circle
        if (accuracyOverlay != null) {
            map.getOverlays().remove(accuracyOverlay);
        }
        accuracyOverlay = new AccuracyOverlay(p, 500);
        map.getOverlays().add(accuracyOverlay);
        map.invalidate();
    }

    public boolean areLocationPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private ActivityResultLauncher<String[]> requestMultiplePermissions =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {
                    // Permission denied
                    Toast.makeText(ViewRestroomsNearbyActivity.this, "Location-related permissions were denied. Please grant permissions to get current location.", Toast.LENGTH_LONG).show();
                } else {
                    // Permission is granted. Continue the action or workflow in your app.
                    pinCurrentLocationToMap();
                }
            });

    private ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Show map and floating action buttons.
                    map.setVisibility(View.VISIBLE);
                    btnAddReview.setVisibility(View.VISIBLE);
                    btnCurrentLocation.setVisibility(View.VISIBLE);
                } else {
                    // Permission denied
                    Toast.makeText(ViewRestroomsNearbyActivity.this, "External storage write permission was denied. Please grant permission to view map.", Toast.LENGTH_LONG).show();
                }
            });

    public void pinCurrentLocationToMap() {

        // Check if location permissions are granted
        if (areLocationPermissionsGranted()) {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled) {
                getLocationOnce();
            }
            else {
                askToEnableGPS();
            }
        }
        // Else ask for permissions
        else {
            requestMultiplePermissions.launch(new String [] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void setAddMyLocationMode() {
        // Show the default BtnAddReview
        btnAddReview.setImageResource(R.drawable.sharp_add_black_36);
        btnAddReview.setVisibility(View.VISIBLE);

        // Hide addRestroom pin
        addPin.setVisible(false);

        // Replace addRestroomOverlay with  myLocationOverlay
        map.getOverlays().set(0, myLocationEventsOverlay);

        map.invalidate();
        isAddRestroomMode = false;
    }

    public void setAddRestroomMode() {
        // Hide btnAddReview
        btnAddReview.setVisibility(View.GONE);

        // Replace myLocationOverlay with addRestroomOverlay
        map.getOverlays().set(0, newRestroomEventsOverlay);

        sb.setText("Pin a location to add a restroom");
        sb.show();
        isAddRestroomMode = true;
    }

    private ActivityResultLauncher<Intent> addRestroomResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // TODO
                        String restroomId = result.getData().getStringExtra(IntentKeys.RESTROOM_ID_KEY);
                        double latitude = result.getData().getDoubleExtra(IntentKeys.LATITUDE_KEY, 0.0);
                        double longitude = result.getData().getDoubleExtra(IntentKeys.LONGITUDE_KEY, 0.0);

                        Marker m = new Marker(map);
                        //m.setId(restroomId);
                        m.setPosition(new GeoPoint(latitude, longitude));
                        m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                Intent i = new Intent(ViewRestroomsNearbyActivity.this, ViewReviewsActivity.class);
                                i.putExtra(IntentKeys.RESTROOM_ID_KEY, restroomId);
                                startActivity(i);
                                return true;
                            }
                        });
                        map.getOverlays().add(m);
                        map.invalidate();
                    }
                }
            }
    );
}