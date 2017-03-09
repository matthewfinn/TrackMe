package ie.nuigalway.trackme.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;

public class Splash extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static String TAG = Splash.class.getSimpleName();

    // Splash screen timeout, amount of time screen wants to be shown for

    private static int TIMEOUT = 5000;
    SessionManager sm;
    GoogleApiClient gac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sm = new SessionManager(getApplicationContext());
        sm.setGPSServiceRunning(false);

        if(!sm.hasLocationServiceOn()) {
            if (gac == null) {

                Log.i(TAG, "Requesting User To Enable Location Services");
                gac = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

            gac.connect();

            LocationRequest lr = new LocationRequest();
            lr.setInterval(15);
            lr.setFastestInterval(15);

            //CHECK THIS
            //Decided not to set priority. It just sets as default best (I THINK)
            //lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(lr);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(gac, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult locationSettingsResult) {

                    final Status status = locationSettingsResult.getStatus();
                   // final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(Splash.this, REQUEST_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;
                    }
                }
            });

        }else{
            createDelayHandler();
        }

    }

    private void createDelayHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i;
                if (sm.isLoggedIn()) {
                    i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    i= new Intent(Splash.this, StartupActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, TIMEOUT);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.i(TAG,"User Granted Permission To User Location Services");
                        createDelayHandler();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG,"Permission to user location services denied");
                        System.exit(0);
                        finish();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}

