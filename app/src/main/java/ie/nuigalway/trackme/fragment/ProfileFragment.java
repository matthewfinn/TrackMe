package ie.nuigalway.trackme.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    private static final String TAG = ProfileFragment.class.getSimpleName();

    HashMap<String, String> userDetails;
    private SessionManager sm;
    private ListView list;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sm = new SessionManager(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        userDetails = sm.getUserDetails();
        MyAdapter adapter = new MyAdapter(userDetails);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView header = new TextView(getActivity());
        header.setText("My Profile");
        header.setGravity(Gravity.CENTER);
        header.setTextSize(26);
        header.setTypeface(null, Typeface.BOLD);
        header.setPadding(10,10,10,10);
        list  = (ListView) view.findViewById(R.id.profile_list);
        list.addHeaderView(header);
        list.setPadding(10,20,10,10);
        list.setAdapter(adapter);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
    }

    private class MyAdapter extends BaseAdapter {

        private final ArrayList mData;

        public MyAdapter(Map<String, String> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public Map.Entry<String, String> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO implement you own logic with ID
            return 0;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;


            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile, parent, false);
            } else {
                result = convertView;
            }

            Map.Entry<String, String> item = getItem(position);


            ((TextView) result.findViewById(R.id.txt1)).setText(item.getKey()+": "+item.getValue());

            return result;
        }

    }
}
