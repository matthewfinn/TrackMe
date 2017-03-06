package ie.nuigalway.trackme.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.GPSHelper;
import ie.nuigalway.trackme.helper.SessionManager;
import ie.nuigalway.trackme.services.GPSService;

//import com.google.android.gms.identity.intents.Address;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{

    private static final int fl = 1;
    private static final String TAG = HomeFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private LatLng currentLocation;
    private GPSHelper gh;
    private GoogleMap map;
    private int aflCheck;
    private Button trackMeButton;
    private SessionManager sm;
    private ProgressDialog pd;

    public HomeFragment() {
    }

    public Fragment getFragment(){
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null, false);

        sm = new SessionManager(getContext());

        trackMeButton = (Button) v.findViewById(R.id.trackme_button);
        trackMeButton.setOnClickListener(this);

        aflCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        Log.i(TAG, "Permission already given to access location using device");

        if (aflCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Requesting permission to access location");

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    fl);
        }

        SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        smf.getMapAsync(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
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

            case fl: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    aflCheck = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION);
                    Log.i(TAG, "Checking if location permission given. Status: "+aflCheck);

                    try{
                        getMap();
                    }catch(IOException e){

                        e.printStackTrace();
                    }
                }
                else{
                    Log.i(TAG, "Location permission denied by user"+aflCheck);
                }
                }
            }
        }

    private void getMap() throws IOException{

        String address;
        gh = new GPSHelper(getContext());
        if(!gh.checkInternetServiceAvailable()) {

            new AlertDialog.Builder(getActivity()).
                    setTitle("No Connection").
                    setMessage("Enable Internet Connection If You Want Address To Be Displayed\n"+
                            "Otherwise on Latitude/Longitude Values will be displayed").
                    setNeutralButton("Close", null).show();
        }

        currentLocation = gh.getCurrentStaticLocation();

        map.setPadding(10, 10, 10, 10);

            address = gh.getAddressString(currentLocation);


        Log.d(TAG, "User Location is" + address );
        map.addMarker(new MarkerOptions().position(currentLocation).title(address));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trackme_button:

                Toast.makeText(getContext(), "Starting GPS Tracking Service", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Starting Service: "+GPSService.class.getSimpleName());
                Intent intent = new Intent(getActivity(), GPSService.class);
                getActivity().startService(intent);
                break;
                    //map.clear();
        }
    }

    public void addToMap(LatLng loc, String add){
        map.clear();
        map.addMarker(new MarkerOptions().position(loc).title(add));
    }

    private BroadcastReceiver br  = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}




