package ie.nuigalway.trackme.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.CloudDBHandler;
import ie.nuigalway.trackme.helper.GPSHandler;
import ie.nuigalway.trackme.helper.LocalDBHandler;
import ie.nuigalway.trackme.helper.SessionManager;

//import com.google.android.gms.identity.intents.Address;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackUserFragment extends Fragment implements OnMapReadyCallback{

    private static final int fl = 1;
    private static final String TAG = TrackUserFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private static final String IDENTIFIER = "Location";
    private static final String KEY = "locData";

    private static final String UN = "username";
    private static final String LAT = "latitude";
    private static final String LON = "longitude";
    private static final String TS = "timestamp";
    private LatLng currentLocation;
    private String locationAddress;
    private GPSHandler gh;
    private boolean gotLoc=false;
    private CloudDBHandler cdb;
    private LocalDBHandler ldb;
    private HashMap<String, String> loc;
    private GoogleMap map;
    private ProgressDialog dialog;
  //  private int aflCheck;
    //private Button trackMeButton, trackUserButton;
    private SessionManager sm;
    private Context ctx;


    public TrackUserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_track_user, null, false);

        sm = new SessionManager(getContext());
        ldb = new LocalDBHandler(getContext());
        cdb = new CloudDBHandler(getContext());
        gh = new GPSHandler(getContext());

        SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        smf.getMapAsync(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        ctx = context;
        super.onAttach(context);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

            try{
                getMap();
            }catch(IOException e){

                e.printStackTrace();
            }

    }
    private void getMap() throws IOException{

        if(gh.checkInternetServiceAvailable()) {
            new CallServerLocation().execute();
            dialog = ProgressDialog.show(getContext(), "", "Detecting...",
                    true);
            dialog.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 4500);
        }else{
            Toast.makeText(getContext(),"No Internet Connection. Cannot Track User.", Toast.LENGTH_LONG).show();
        }
    }

    private class CallServerLocation extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            Bundle bundle = getArguments();
            cdb.requestUserLocation(bundle.getString("email"));
            try{

                Thread.sleep(4000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loc = cdb.getLocationMap();
            if(loc!=null) {
                Log.d(TAG, loc.toString());
                currentLocation = new LatLng(Double.parseDouble(loc.get(LAT)), Double.parseDouble(loc.get(LON)));
                Log.d(TAG, currentLocation.toString());

                try {
                    locationAddress =loc.get(UN)+": "+gh.getShortAddressString(currentLocation) + " @ " + loc.get(TS);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                map.setPadding(10, 10, 10, 10);
                Log.d(TAG, "User Location is :" + locationAddress);
                map.addMarker(new MarkerOptions().position(currentLocation).title(locationAddress));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }else{

                //open dialog with options...
                //Either reenter email address or go back to homefrag.
            }
        }
    }
}





