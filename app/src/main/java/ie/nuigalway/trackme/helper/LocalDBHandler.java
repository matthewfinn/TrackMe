package ie.nuigalway.trackme.helper;

/**
 * Created by matthew on 17/01/2017.
 * https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html
 * Class handles local database stored on device storage.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;


public class LocalDBHandler extends SQLiteOpenHelper{

    private static final String TAG = LocalDBHandler.class.getSimpleName();
    private static final int VERSION = 13;
    private static final String DB_NAME = "trackMe_db";
    private static final String TABLE_USER_DETAILS = "user";
    private static final String TABLE_USER_LOCATION = "location";
    Context ctx;

    /*
    * Fields created for use in local database tables
    * */
    private static final String ID = "id";
    private static final String FN = "first_name";
    private static final String SN = "surname";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PHNO = "phone_no";
    private static final String TYPE = "type";
    private static final String UID = "unique_id";
    private static final String CR = "created_at";
    private static final String LAT = "latitude";
    private static final String LNG = "longitude";
    private static final String TS = "timestamp";
    private static final String TSM = "timestampmilli";

    private SessionManager sm;


    public LocalDBHandler(Context ctx){
        super(ctx, DB_NAME, null, VERSION);
        sm = new SessionManager(ctx);
    }
    @Override
    public void onCreate(SQLiteDatabase db){

        //Creating User Table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER_DETAILS + "("
                + ID + " INTEGER PRIMARY KEY," +  UID + " TEXT," + FN
                + " TEXT," + SN + " TEXT," + PHNO + " TEXT,"
                + EMAIL + " TEXT,"+ USERNAME + " TEXT,"+ TYPE + " TEXT," + CR + " TEXT" + ")";

        db.execSQL(CREATE_USER_TABLE);
        Log.d(TAG, "User Details Database Table created.");

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_USER_LOCATION + "("
                +  UID + " TEXT," + USERNAME + " TEXT," + LAT + " TEXT," + LNG
                + " TEXT," + TS + " TEXT,"+ TSM + " TEXT"+")";

        db.execSQL(CREATE_LOCATION_TABLE);
        Log.d(TAG, "User Location Database Table created.");

    }


    public void createTrigger(){
        String TRIGGER = "CREATE TRIGGER IF NOT EXISTS trigger INSERT ON " +
                TABLE_USER_LOCATION+ " WHEN (SELECT COUNT(*) FROM "+TABLE_USER_LOCATION+") >50 " +
                "BEGIN " +
                "DELETE FROM "+TABLE_USER_LOCATION+" WHERE "+TSM+" IN " +
                "(SELECT "+TSM+" FROM "+TABLE_USER_LOCATION+" " +
                "ORDER BY "+TSM+" limit (select count(*) -50 from "+TABLE_USER_LOCATION+")); " +
                "END;";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(TRIGGER);

        String SQLQuery = "SELECT COUNT(" + USERNAME + ") FROM " + TABLE_USER_LOCATION + ";";
        Cursor cursor = db.rawQuery(SQLQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        Log.d(TAG,"Trigger. "+TABLE_USER_LOCATION+"Table Size ="+count);


    }

    public void addUser(String uid, String fn, String sn, String phno, String email, String un, String ty,  String cr){

        Cursor cursor = null;
        int counter;
        SQLiteDatabase db1 = this.getReadableDatabase();

        try {
            String query = "select count(*) from "+TABLE_USER_DETAILS+" where email = ?";
            cursor = db1.rawQuery(query, new String[]{email});
            if (cursor.moveToFirst()) {
                counter = cursor.getInt(0);
            }else {
                counter = 0; //data not found in readable db
            }

        }finally{
            if (cursor != null) {
                cursor.close();
                Log.d(TAG,  "User exists in "+TABLE_USER_DETAILS + " table. Cursor closed");

            }
            if (db1 != null) {
                db1.close();
                Log.d(TAG,  "User exists in "+TABLE_USER_DETAILS + " table. Database connection closed");

            }
        }


        if(counter==0) {
            SQLiteDatabase db2 = this.getWritableDatabase();
            ContentValues vals = new ContentValues();//empty set of values that will be used to store user info
            vals.put(UID, uid);
            vals.put(FN, fn);
            vals.put(SN, sn);
            vals.put(EMAIL, email);
            vals.put(USERNAME, un);
            vals.put(PHNO, phno);
            vals.put(TYPE, ty);
            vals.put(CR, cr);

            long ins = db2.insert(TABLE_USER_DETAILS, null, vals);
            db2.close(); // Closing database connection
            Log.d(TAG, "User inserted into db table " +TABLE_USER_DETAILS+ "  " + ins);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n){

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_LOCATION);
        onCreate(db);//creates db with tables again

    }

    public void deleteTables(){


    }

    public void updateLocation(String lt, String ln, String t){

        HashMap<String,String> uDetails = sm.getUserDetails();
        String id = uDetails.get(UID);
        String username = uDetails.get(USERNAME);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();//empty set of values that will be used to store user info
        vals.put(UID, id);
        vals.put(USERNAME, username);
        vals.put(LAT, lt);
        vals.put(LNG, ln);
        vals.put(TS, t);
        vals.put(TSM, Calendar.getInstance().getTimeInMillis());

        long ins = db.insert(TABLE_USER_LOCATION, null, vals);
        db.close(); // Closing database connection
        Log.d(TAG, "User location into db table " +TABLE_USER_LOCATION+ "  " + ins);
        createTrigger();

    }
//
//    public HashMap<String, String> getUserDetails(){
//
//        HashMap<String, String> userDetails = new HashMap<String, String>();
//        String query = "SELECT  * FROM " + TABLE_USER_DETAILS;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(query, null);
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            userDetails.put(UID, cursor.getString(1));
//            userDetails.put(FN, cursor.getString(2));
//            userDetails.put(SN, cursor.getString(3));
//            userDetails.put(PHNO, cursor.getString(4));
//            userDetails.put(EMAIL, cursor.getString(5));
//            userDetails.put(USERNAME, cursor.getString(5));
//            userDetails.put(CR, cursor.getString(6));
//        }
//        cursor.close();
//
//        db.close();
//        // return user
//        Log.d(TAG, "Fetching User from Sqlite: " + userDetails.toString());
//
//        return userDetails;
//    }
}








