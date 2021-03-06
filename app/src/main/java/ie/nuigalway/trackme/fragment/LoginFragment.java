package ie.nuigalway.trackme.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import ie.nuigalway.trackme.activity.MainActivity;
import ie.nuigalway.trackme.application.App;
import ie.nuigalway.trackme.application.AppConfig;
import ie.nuigalway.trackme.helper.GPSHandler;
import ie.nuigalway.trackme.helper.LocalDBHandler;
import ie.nuigalway.trackme.helper.SessionManager;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = LoginFragment.class.getSimpleName();

    private EditText username, password;
    private Button login_button;
    private TextView login_reglink;
    private SessionManager sm;
    private LocalDBHandler db;
    private GPSHandler gh;
    private ProgressDialog pd;
    private String un, pw;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        login_button = (Button) view.findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        login_reglink =  (TextView) view.findViewById(R.id.login_reglink);
        login_reglink.setOnClickListener(this);

        username = (EditText) view.findViewById(R.id.login_username);
        password = (EditText) view.findViewById(R.id.login_password);

        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);

        db = new LocalDBHandler(getContext());
        sm = new SessionManager(getContext());
        gh = new GPSHandler(getContext());

        if(!gh.checkInternetServiceAvailable()) {

            new AlertDialog.Builder(getContext()).
                    setTitle("No Connection").
                    setMessage("Internet Connection Needed To Log In").
                    setNeutralButton("Close", null).show();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if(gh.checkInternetServiceAvailable()) {
                    attemptLogin();
                }else{
                    Toast.makeText(getContext(),"No Internet Connection", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.login_reglink:
                switchToRegFrag();
                break;
        }
    }
    private void attemptLogin() {

        un = username.getText().toString().trim();
        pw = password.getText().toString().trim();

        // else if block(s) to verify is user entered correct information.
        if (!un.isEmpty() && !pw.isEmpty()) {

            // Verify user login using details provided
            verifyLogin(un, pw);

        }else if (un.isEmpty()&&pw.isEmpty()){

            Toast.makeText(getActivity(),
                    "Please Enter Username & Password To Log In", Toast.LENGTH_LONG)
                    .show();

        }
        else if (un.isEmpty()){
            Toast.makeText(getContext(),
                    "Please Enter Username Address", Toast.LENGTH_LONG)
                    .show();
        }else if(pw.isEmpty()){
            Toast.makeText(getContext(),
                    "Please Enter Password", Toast.LENGTH_LONG)
                    .show();
        }
        else{

            Toast.makeText(getContext(),
                    "Incorrect Login Details", Toast.LENGTH_LONG)
                    .show();
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

    private void verifyLogin(final String username, final String password){

        String req = "req_login";

        pd.setMessage("Logging In");

        if(!pd.isShowing()){
            pd.show();

        }

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String res) {

                Log.d(TAG, "Response: " + res);
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                try {
                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");

                    if (!error) {
                        String uid = j.getString("uid");
                        JSONObject user = j.getJSONObject("user");

                        String fn = user.getString("first_name");
                        String sn = user.getString("surname");
                        String em = user.getString("email");
                        String uname = user.getString("username");
                        String ph = user.getString("phone_no");
                        String ty = user.getString("type");
                        String cr = user.getString("created_at");

                        sm.startLoginSession(true, fn, sn, em, uname, ph, ty, uid);
                        db.addUser(uid, fn, sn, em, uname, ph, ty, cr);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);

                    } else {
                        String err = j.getString("error_msg");
                        Toast.makeText(getContext(), err , Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jse) {

                    jse.printStackTrace();
                    Toast.makeText(getContext(), jse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError e){

                Log.e(TAG,"Login : "+ e.getMessage());
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                if(pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<String,String>();
                p.put("username", username);
                p.put("password",password);

                return p;
            }
        };

        App.getInstance().addToRQ(r, req);
    }

    private void switchToRegFrag() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment newFragment = new RegisterFragment();
        ft.replace(R.id.fragment_login, newFragment );
        ft.commit();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
