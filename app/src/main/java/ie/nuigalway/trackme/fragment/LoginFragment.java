package ie.nuigalway.trackme.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import ie.nuigalway.trackme.activity.MainActivity;
import ie.nuigalway.trackme.application.App;
import ie.nuigalway.trackme.application.AppConfig;
import ie.nuigalway.trackme.helper.LocalDBHandler;
import ie.nuigalway.trackme.helper.SessionManager;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = LoginFragment.class.getSimpleName();

    private EditText email, password;
    private Button login_button, login_reglink;
    private SessionManager sm;
    private LocalDBHandler db;
    private ProgressDialog pd;
    private String em, pw;

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

        login_reglink = (Button) view.findViewById(R.id.login_reglink);
        login_reglink.setOnClickListener(this);

        email = (EditText) view.findViewById(R.id.login_email);
        password = (EditText) view.findViewById(R.id.login_password);

        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);

        db = new LocalDBHandler(getContext());
        sm = new SessionManager(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                attemptLogin();
                break;

            case R.id.login_reglink:
                switchToRegFrag();
                break;
        }
    }
    private void attemptLogin() {

        em = email.getText().toString().trim();
        pw = password.getText().toString().trim();

        // else if block(s) to verify is user entered correct information.

        if (!em.isEmpty() && !pw.isEmpty()) {

            // Verify user login using details provided
            verifyLogin(em, pw);

        }else if (em.isEmpty()&&pw.isEmpty()){


            Toast.makeText(getActivity(),
                    "Please Enter Email Address & Password To Log In", Toast.LENGTH_LONG)
                    .show();

        }
        else if (em.isEmpty()){
            Toast.makeText(getContext(),
                    "Please Enter Email Address", Toast.LENGTH_LONG)
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

    private void verifyLogin(final String email, final String password){

        String req = "req_login";

        pd.setMessage("Logging In");

        if(!pd.isShowing()){
            pd.show();

        }

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String res) {

                Log.d(TAG, "Response: " + res.toString());
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
                        String ph = user.getString("phone_no");
                        String cr = user.getString("created_at");

                        sm.startLoginSession(true, fn, sn, em, ph);
                        db.addUser(uid, fn, sn, em, ph, cr);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);

                    } else {
                        String err = j.getString("error_msg");
                        Toast.makeText(getContext(), "On Response: "+err , Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jse) {

                    jse.printStackTrace();
                    Toast.makeText(getContext(), "On Response Error" + jse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError e){

                Log.e(TAG,"Login : "+ e.getMessage());
                Toast.makeText(getContext(),"On Error Response: "+ e.getMessage(), Toast.LENGTH_LONG).show();

                if(pd.isShowing()) {

                    pd.dismiss();
                }
            }

        }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<String,String>();
                p.put("email", email);
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
    private void switchToHomeFrag() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment newFragment = new HomeFragment();
        ft.replace(R.id.fragment_login, newFragment );
        ft.commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
