package ie.nuigalway.trackme;

import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import java.net.HttpURLConnection;
import org.json.JSONObject;
import org.apache.http.NameValuePair;

import java.net.MalformedURLException;
import java.util.List;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.ClientProtocolException;
import java.net.URL;
import java.io.IOException;
import org.apache.http.HttpResponse;



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

    public JSONObject getDataFromURL(String u){




        try{
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            readStream(in);

        }catch(IOException  e){

            e.printStackTrace();
        }
    }

    public JSONObject makeRequest(String url, String m, List<NameValuePair> p){

        return null;

    }


}
