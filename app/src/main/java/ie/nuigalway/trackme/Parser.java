package ie.nuigalway.trackme;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import java.io.BufferedInputStream;
import java.util.List;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;



/**
 * Created by matthew on 16/01/2017.
 */

public class Parser {

    static InputStream is = null;
    static JSONObject jso ;
    static String s = "";


    //const...no args...may not be needed at all?????
    public Parser(){


    }

   /* public JSONObject getData(String u){

        try{
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());



           // readStream(in);

        }catch(IOException  e){

            e.printStackTrace();
        }
    }*/

    public JSONObject makeRequest(String u, String m, List<NameValuePair> p){

        try { // checking request method

            if(m == "POST") {

                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);
                    is = new BufferedInputStream(urlConnection.getInputStream());

                }finally {
                    urlConnection.disconnect();
                }

            }else if(m == "GET"){

                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                   is = new BufferedInputStream(urlConnection.getInputStream());

                } finally {
                    urlConnection.disconnect();
                }



            }

        }catch(IOException e){

            e.printStackTrace();
        }


        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder str = new StringBuilder();
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                str.append(strLine + "\n");
            }
            is.close();

            s = str.toString();

        }catch(Exception e){

        }
        try{
            jso = new JSONObject(s);


        }catch(JSONException e){

        }
        return jso;
    }

    /*public void readStream(InputStream i){

    }

    public void writeStream(OutputStream o){

    }*/


}
