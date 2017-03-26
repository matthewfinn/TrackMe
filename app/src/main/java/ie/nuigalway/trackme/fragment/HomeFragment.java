package ie.nuigalway.trackme.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.CloudDBHandler;
import ie.nuigalway.trackme.helper.GPSHandler;
import ie.nuigalway.trackme.helper.LocalDBHandler;
import ie.nuigalway.trackme.helper.MessageHandler;
import ie.nuigalway.trackme.helper.SessionManager;
import ie.nuigalway.trackme.services.GPSService;

public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 5;

    private static final String TAG = HomeFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private static final String IDENTIFIER = "Location";
    //private static final String KEY = "locData";

    private static final String LAT = "latData";
    private static final String LNG = "lngData";
    private static final String CDT = "timeData";
    private LatLng currentLocation;
    private GPSHandler gh;
    private CloudDBHandler cdb;
    private LocalDBHandler ldb;
    private MessageHandler mh;
    private GoogleMap map;
    private int aflCheck, smsCheck;
    private Button trackMeButton, trackUserButton, smsBtn;
    private SessionManager sm;
    private Context ctx;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {

        super.onResume();
        getActivity().registerReceiver(rec, new IntentFilter(IDENTIFIER));

    }
    @Override
    public void onPause() {

        super.onPause();
        getActivity().unregisterReceiver(rec);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null, false);

        ctx = getContext();

        sm = new SessionManager(ctx);
        ldb = new LocalDBHandler(ctx);
        cdb = new CloudDBHandler(ctx);
        mh = new MessageHandler(ctx);

        trackMeButton = (Button) v.findViewById(R.id.trackme_button);
        trackMeButton.setOnClickListener(this);

        trackUserButton = (Button) v.findViewById(R.id.trackuser_button);
        trackUserButton.setOnClickListener(this);


        checkPermissions();

        SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        smf.getMapAsync(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        ctx = context;
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(aflCheck!=-1){
            try{
                getMap();
            }catch(IOException e){

                e.printStackTrace();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)  {

        switch (requestCode) {

            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");

                        try{
                            getMap();
                        }catch(IOException e){

                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");


                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }

                        else {
                            Toast.makeText(getActivity(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }
    }

    private boolean checkPermissions(){

        aflCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        smsCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (aflCheck != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (smsCheck != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;


    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void getMap() throws IOException{

        String address;
        gh = new GPSHandler(ctx);
        if(!gh.checkInternetServiceAvailable()) {

            new AlertDialog.Builder(getActivity()).
                    setTitle("No Connection").
                    setMessage("Enable Internet Connection For Better Accuracy.\n"+
                            "Otherwise Addresses Can't Be Displayed").
                    setNeutralButton("Close", null).show();
        }

        currentLocation = gh.getCurrentStaticLocation();
        map.setPadding(10, 10, 10, 10);
        address = gh.getShortAddressString(currentLocation);

        Log.d(TAG, "User Location is :" + address );
        map.addMarker(new MarkerOptions().position(currentLocation).title(address));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trackme_button:

                if(!sm.isGPSServiceRunning()) {

                    Intent intent = new Intent(getActivity(), GPSService.class);
                    getActivity().startService(intent);
                    Toast.makeText(ctx, "Starting GPS Tracking Service", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Starting Service: " + GPSService.class.getSimpleName());

                }else if(sm.isGPSServiceRunning()){

                    Intent intent = new Intent(getActivity(), GPSService.class);
                    getActivity().stopService(intent);
                    Toast.makeText(ctx, "Stopping GPS Tracking Service ", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Starting Service: " + GPSService.class.getSimpleName());
                }
                break;


            case R.id.trackuser_button:

                final EditText txtUrl = new EditText(ctx);
                txtUrl.setHint("Enter Username Here");
                new AlertDialog.Builder(ctx)
                        .setTitle("Track User")
                        .setMessage("Enter Username Of User You Would Like To Track")
                        .setView(txtUrl)
                        .setPositiveButton("Track", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String email = txtUrl.getText().toString();
                                if(email.isEmpty()){

                                    Toast.makeText(ctx, "Please Enter Username To Track", Toast.LENGTH_LONG).show();
                                }else {

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("email", email);
                                    TrackUserFragment fragment = new TrackUserFragment();
                                    fragment.setArguments(bundle);

                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.flContent, fragment );
                                    ft.commit();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
                break;


        }
    }

    private BroadcastReceiver rec = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            map.clear();

            Double lat = intent.getDoubleExtra(LAT,0.0);
            Double lon = intent.getDoubleExtra(LNG,0.0);
            String cdt = intent.getExtras().get(CDT).toString();
            LatLng currPos = new LatLng(lat,lon);
            Log.d(TAG, "onReceive Location Data: "+lat+" "+lon+" "+cdt);


            try {
                map.addMarker(new MarkerOptions().position(currPos).title(gh.getShortAddressString(currPos)+" @ "+cdt
                        ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}