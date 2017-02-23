package ie.nuigalway.trackme.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.application.App;
import ie.nuigalway.trackme.application.AppConfig;
import ie.nuigalway.trackme.helper.LocalDBHandler;
import ie.nuigalway.trackme.helper.SessionManager;


public class RegisterFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = RegisterFragment.class.getSimpleName();
    private EditText fName,surname,email, password, phno;
    private Button register_button, login_link;
    private SessionManager sesh;
    private LocalDBHandler db;
    private ProgressDialog pd;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        fName = (EditText) view.findViewById(R.id.edit_fname);
        surname = (EditText) view.findViewById(R.id.edit_surname);
        phno = (EditText) view.findViewById(R.id.edit_phno);
        email = (EditText) view.findViewById(R.id.edit_email);
        password = (EditText) view.findViewById(R.id.edit_password);

        register_button = (Button) view.findViewById(R.id.reg_btn);
        register_button.setOnClickListener(this);
        login_link = (Button) view.findViewById(R.id.lgn_lnk);
        login_link.setOnClickListener(this);

        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);

        db = new LocalDBHandler(getContext());
        sesh = new SessionManager(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_btn:
                attemptRegister();
                break;
            case R.id.lgn_lnk:
                switchFragmentToLogin();
                break;
        }
    }

    public void attemptRegister() {

        String fn = fName.getText().toString().trim();
        String sn = surname.getText().toString().trim();
        String ph = phno.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();

        // Check for empty data in the form
        if (!fn.isEmpty() && !sn.isEmpty() && !ph.isEmpty() && !em.isEmpty() && !pw.isEmpty()) {

            registerUser(fn,sn, ph, em, pw);

        } else {

            Toast.makeText(getContext(),
                    "Not all information..", Toast.LENGTH_LONG)
                    .show();
        }
    }
    
    private void registerUser(final String fn, final String sn,final String ph, final String em, final String pw){

        String req = "req_register";

        pd.setMessage("Registering Information on TrackMe Server");

        if(!pd.isShowing()){
            pd.show();
        }

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.REGISTRATION_URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String res) {

                Log.d(TAG, "Register Response: " + res);

                if (pd.isShowing()) {
                    pd.dismiss();
                }

                try {

                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");
                    System.out.print(j.toString());

                    if (!error) {
                        String uid = j.getString("uid");
                        JSONObject user = j.getJSONObject("user");

                        String fn = user.getString("first_name");
                        String sn = user.getString("surname");
                        String em = user.getString("email");
                        String ph = user.getString("phone_no");
                        String cr = user.getString("created_at");

                        db.addUser(uid, fn, sn, em, ph, cr);

                        Toast.makeText(getContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        switchFragmentToLogin();

                    } else {
                        String err = j.getString("error_msg");
                        Toast.makeText(getContext(), "On Response: "+err, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jse) {

                    jse.printStackTrace();
                    Toast.makeText(getContext(), "On Response Error" + jse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError e){

                Log.e(TAG,"Registration Error : "+ e.getMessage());
                Toast.makeText(getContext(),"On Error Response: "+ e.getMessage(), Toast.LENGTH_LONG).show();

                if(pd.isShowing()){

                    pd.dismiss();
                }
            }

        }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<>();
                p.put("first_name", fn);
                p.put("surname",sn);
                p.put("phone_no", ph);
                p.put("email", em);
                p.put("password", pw);

                return p;
            }
        };
        App.getInstance().addToRQ(r, req);
    }

    private void switchFragmentToLogin() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment newFragment = new LoginFragment();
        ft.replace(R.id.fragment_register, newFragment );

        ft.commit();
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
}
