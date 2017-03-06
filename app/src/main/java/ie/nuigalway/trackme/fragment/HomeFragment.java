package ie.nuigalway.trackme.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    private Intent myGpsService;
    private SessionManager sm;

    public HomeFragment() {
        // Required empty public constructor
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

        gh = new GPSHelper(getContext());
        currentLocation = gh.getCurrentStaticLocation();

        map.setPadding(10, 10, 10, 10);

        String address = gh.getAddressString(currentLocation);

       // String address = currentLocation.toString();

        Log.d(TAG, address );
        map.addMarker(new MarkerOptions().position(currentLocation).title(address));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onClick(View v) {
        myGpsService = new Intent(getActivity(), GPSService.class);
        Log.d(TAG,myGpsService.toString());
        //startActivity(myGpsService);

        switch (v.getId()) {
            case R.id.trackme_button:

                Log.d(TAG, "Tracking Enabled");
                Intent intent = new Intent(getActivity(), GPSService.class);
                getActivity().startService(intent);

        }
    }

}




