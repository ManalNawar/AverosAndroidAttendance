package com.averos.als.positioningdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.Log;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.averos.als.positioning.ALSManager;
import com.averos.als.positioning.ble.DebugListener;
import com.averos.als.positioning.ble.model.BeaconNode;
import com.averos.als.positioning.location.ALSPositionListener;
import com.averos.als.positioning.location.IndoorLocation;
import com.averos.als.positioning.misc.ALSActivity;
import com.averos.als.positioning.misc.ALSConstants;
import com.averos.als.positioning.misc.ALSUtils;
import com.averos.als.positioning.models.Beacons;
import com.averos.als.positioning.models.Buildings;
import com.averos.als.positioning.models.FloorRegion;
import com.averos.als.positioningdemo.callback.ServerCallback;
//import com.averos.als.positioningdemo.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.MsalServiceException;
import com.microsoft.identity.client.MsalUiRequiredException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import static com.averos.als.positioningdemo.LoginActivity.userID;


public class MainActivity extends ALSActivity implements ALSPositionListener {

    private final String APIKey = "VBO0h6fSi3DF4kQ7F02uxhnGkm63m2XOWl1l7bHJARo=";
    private final String companyId = "107";
    private Buildings buildings;
    private Beacons beacons;
    private ALSManager alsManager;

    private EditText editTextScanWindow;
    private Button scanButton;
    private Button postButton;
    private Button attendButton;
    private Spinner scanTypeSpinner;
    private TextView textView;
    private TextView username;
    private TextView useremail;
    private TextView bloctitle;
    private boolean isScanning;
    private ProgressDialog progressDialog;
    private AsyncHttpClient client;

    private RecyclerView.Adapter beaconListAdapter;
    private RecyclerView recyclerView;
    private List<BeaconNode> beaconNodes = new ArrayList<>();
    private ALSManager.ScanType scanType;
    private CountDownTimer timer = null;
    private int startTime;
    public static String regionName;

    /* UI & Debugging Variables */
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button callGraphButton;
    Button signOutButton;
    Button attendenceLog;

    /* Azure AD Variables */
     PublicClientApplication sampleApp = LoginActivity.sampleApp;
     AuthenticationResult authResult = LoginActivity.authResult;

    public static String userID;
    BottomNavigationView nav_bottom;
    ImageView img;

    List<User> users;
    Intent myIntent;
    Users user;
    Toolbar toolbar;
    LinearLayout beaconsUI;
    String userName;
    String userEmail;
    String token;
    private static String blocTitle;
    TextView className;
    ImageButton classNamebt;
    AlertDialog.Builder builder;
    ImageView imageview;
    boolean isDarkMode;

    Bundle auth;

    public MainActivity() {
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for Internet Connection
        if (!Connection.InternetConnection(MainActivity.this)) {
            Connection.Dialog(MainActivity.this,"Please check your Internet Connection to use the App","No Internet Connection");

        }

        imageview = findViewById(R.id.imageview);

        // check the theme mode saved in shared preference.
        isDarkMode = SharedPrefManager.getInstance(getApplicationContext()).isDarkMode();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            imageview.setImageResource(R.drawable.image_dark);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

         userName = SharedPrefManager.getInstance(getApplicationContext()).getUser().getName();
         userEmail =  SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();
         token = SharedPrefManager.getInstance(getApplicationContext()).getToken().getToken();

        checkBlock(userEmail,token);

        ////////////////////////////tool bar ////////////////////////////////////
        toolbar = findViewById(R.id.main_toolbar);
        //toolbar three dots color change
        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.textcolor) , PorterDuff.Mode.SRC_ATOP);

        // providing title for the ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        builder = new AlertDialog.Builder(this);
        ///////////////////sampleapp//////////////

        //users = getIntent().getExtras();
        //sampleApp = users.getString("sampleApp");
        // check if user is loggedin
//        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//        }

        ////////////////////////////tool bar ////////////////////////////////////
        //toolbar = findViewById(R.id.main_toolbar);

       // beaconsUI = findViewById(R.id.beaconsUI);
        // providing title for the ActionBar
        //toolbar.setVisibility(View.INVISIBLE);
        //beaconsUI.setVisibility(View.INVISIBLE);




////////////////////////////end tool bar////////////////////////////////////
        client = new AsyncHttpClient();

        alsManager = new ALSManager(MainActivity.this);




        //editTextScanWindow = findViewById(R.id.editTxt);
        scanButton = findViewById(R.id.btn);
        postButton = findViewById(R.id.viewAttend);
        //textView = findViewById(R.id.txtView);
        //scanTypeSpinner = findViewById(R.id.scanType);
        attendButton = findViewById(R.id.attend);
       // recyclerView = findViewById(R.id.beacon_list);


        username =  findViewById(R.id.username);
        username.setText("welcome "+ userName);
        useremail =  findViewById(R.id.useremail);
        useremail.setText(userEmail);
        bloctitle = findViewById(R.id.bloctitle);

        progressDialog = new ProgressDialog(this);

        className = findViewById(R.id.classname);
        classNamebt = findViewById(R.id.imageButton);
        classNamebt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check for Internet Connection
                if (!Connection.InternetConnection(MainActivity.this)) {
                    Connection.Dialog(MainActivity.this,"Please check your Internet Connection to use the App","No Internet Connection");

                }
                progressDialog.setTitle("Searching...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                getBuildingInfo(APIKey, companyId, new ServerCallback() {
                    @Override
                    public void onResponse(boolean status, String response) {
                        buildings = new Buildings(getApplicationContext(), response);
                        getBeacons(APIKey, companyId, new ServerCallback() {
                            @Override
                            public void onResponse(boolean status, String response) {
                                beacons = new Beacons(getApplicationContext(), response);

                                if (buildings.message.equals(ALSConstants.SUCCESS_RESPONSE) && beacons.message.equals(ALSConstants.SUCCESS_RESPONSE)) {
                                    //Toast.makeText(MainActivity.this, "successfully initialized", Toast.LENGTH_SHORT).show();
                                    alsManager.initialize(beacons.beacons, buildings.buildings);
                                    alsManager.addLocationListener(MainActivity.this);
                                    scanButton.callOnClick();
                                } else {
                                    scanButton.setClickable(false);
                                    Toast.makeText(MainActivity.this, "Not initialized", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                //className.setText(regionName);
//
                className.setVisibility(View.VISIBLE);
                className.setText(regionName);


                //                        classNamebt.setVisibility(View.VISIBLE);
//                        className.setVisibility(View.INVISIBLE);
//
//                        String email ="alsaylanih@ksau-hh.edu.sa";
//                        String[] split = email.split("@");
//                        System.out.print(split);
//                        Log.d(TAG, "split is : " + split);
//                        String domain = split[1];
//                        if (!domain.equals("ksau-hs.edu.sa")){
//                            Log.d(TAG, " No login");
//
//                        }else {
//                            Log.d(TAG, "can login");
//
//                        }
//                        Log.d(TAG, "domain is : " + domain);

            }
        });




        setupBeaconListView();


        TextView seekbarValue = findViewById(R.id.seekbar_rssivalue);
        seekbarValue.setText("-120 dbm");
        SeekBar rssiSeekbar = findViewById(R.id.seekBarrssi);
        rssiSeekbar.setMax(120);
        rssiSeekbar.setProgress(rssiSeekbar.getMax());
        rssiSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                int value = (progress)*-1;
                seekbarValue.setText(value+" dBm");
                alsManager.setMinimumRSSI(value);
            }
        });

        scanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isScanning) {
//                    isScanning = false;
//                    alsManager.stopContinuousScan();
//                    return;
//                }
                clearBeaconsList();
                scanButton.setClickable(false);
//                if (scanTypeSpinner.getSelectedItemPosition() == 0) {
//                    scanType = ALSManager.ScanType.CONTINUOUS;
//                    isScanning = true;
//                } else
                    scanType = ALSManager.ScanType.CONTINUOUS;

//                if (!editTextScanWindow.getText().toString().isEmpty()) {
//                    startTime = Integer.parseInt(editTextScanWindow.getText().toString());
//                    alsManager.startScan(scanType, Double.parseDouble(editTextScanWindow.getText().toString()));
//                } else {
                    startTime = (int) ALSManager.DEFAULT_SCANNING_WINDOW;
                    alsManager.startScan(scanType);
               // }

                alsManager.addDebugListener(getDebugListener());
                startTimer();
            }
        });




        // status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statuscolor));
        }


    }

    ////////////////////////////////////////////////////////////////


    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.app_bar_items, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.records:
                Intent myIntent = new Intent(getApplicationContext(), AttendanceActivity.class);
               // myIntent.putExtra("useremail",authResult.getUser().getDisplayableId());
                startActivity(myIntent);
                break;
            case R.id.profile:
               // Toast.makeText(this, "profile Clicked", Toast.LENGTH_SHORT).show();
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private DebugListener debugListener;

    private DebugListener getDebugListener() {
        if (debugListener != null)
            return debugListener;
        else
            return debugListener = new DebugListener() {
                @Override
                public void beaconDebugListener(List<BeaconNode> beaconNodes) {
                    MainActivity.this.beaconNodes = beaconNodes;
                    beaconListAdapter.notifyDataSetChanged();
                }
            };
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void locationCallback(IndoorLocation location, List<FloorRegion> regions) {
        StringBuilder string = new StringBuilder();
        if (location != null && regions != null) {
            for (FloorRegion region : regions) {
                string.append("\n\nRegion ID : ").append(region.regionId).append(", Name : ").append(region.regionName);
                 regionName =region.regionName;

                 System.out.println("this is the region"+regionName);
            }
//            className.setText(regionName);
//            if(className.toString().isEmpty()){
//            }else {
//                className.setText(regionName);
//            }
//            textView.setText(
//                    "Location X : " + location.x +
//                            "\nLocation Y : " + location.y +
//                            "\n\nBuilding ID : " + location.buildingId +
//                            "\n\nBuilding Name : " + location.buildingName +
//                            "\n\nFloor ID : " + location.floor +
//                            string);

            className.setText(regionName);
            progressDialog.dismiss();
            attendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    submitattendance(token,userEmail,regionName);

                }
            });
        } else {
            className.setText("Class not found");
            progressDialog.dismiss();
            clearBeaconsList();
        }

        if (scanType == ALSManager.ScanType.CONTINUOUS) {
            stopTimer();
            startTimer();
        }
    }

    private void clearBeaconsList(){
        if(beaconNodes != null) beaconNodes.clear();
        if (beaconListAdapter != null) beaconListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScanStart() {
        if (scanType == ALSManager.ScanType.CONTINUOUS) {
            scanButton.setClickable(true);
        }
    }

    @Override
    public void onScanStop() {
        scanButton.setClickable(true);
        scanButton.setText("Start Scan");
        alsManager.removeDebugListener(getDebugListener());
        stopTimer();
        beaconListAdapter.notifyDataSetChanged();
        alsManager.resetLastLocation();
    }

    public void getBeacons(final String APIkey, String companyId, final ServerCallback callback) {
        String url = "http://apials.averos.com/api/v2/admin/GetBeaconsDetail";
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("APIkey", APIkey);
        headers[1] = new BasicHeader("CompanyId", companyId);

        if (!ALSUtils.isInternetConnectionOnline(MainActivity.this)) {
            callback.onResponse(false, null);
            return;
        }

        client.get(null, url, headers, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                Log.d(TAG, "response json respons respone is ======> " + response);


                callback.onResponse(true, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.onResponse(false, null);
            }
        });
    }

    public void getBuildingInfo(final String APIkey, String companyId, final ServerCallback callback) {
        String url = "http://apials.averos.com/api/v1/admin/GetBuildingInfo";
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("APIkey", APIkey);
        headers[1] = new BasicHeader("CompanyId", companyId);

        if (!ALSUtils.isInternetConnectionOnline(MainActivity.this)) {
            callback.onResponse(false, null);
            return;
        }

        client.get(null, url, headers, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                Log.d(TAG, "response json respons respone is ======> " + response);


                callback.onResponse(true, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.onResponse(false, null);
            }
        });
    }

    private void setupBeaconListView() {
        beaconListAdapter = getBeaconListAdapter();
      //  recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
      //  recyclerView.setAdapter(beaconListAdapter);
    }

    private RecyclerView.Adapter getBeaconListAdapter() {
        return new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MainActivity.BeaconViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_list_item, null));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((MainActivity.BeaconViewHolder) holder).bind(beaconNodes.get(position));
            }

            @Override
            public int getItemCount() {
                return MainActivity.this.beaconNodes.size();
            }
        };
    }

    class BeaconViewHolder extends RecyclerView.ViewHolder {
        private TextView major, minor, rssi, meanRSSI, uuid;

        public BeaconViewHolder(View itemView) {
            super(itemView);
            major = itemView.findViewById(R.id.majorValue);
            minor = itemView.findViewById(R.id.minorValue);
            rssi = itemView.findViewById(R.id.rssiValue);
            meanRSSI = itemView.findViewById(R.id.meanRSSI);
            uuid = itemView.findViewById(R.id.uuid);
        }

        public void bind(BeaconNode beaconNode) {
            major.setText("" + beaconNode.major);
            minor.setText("" + beaconNode.minor);
            rssi.setText("" + beaconNode.getCurrentRSSI());
            meanRSSI.setText("" + beaconNode.meanRSSI());
            uuid.setText("" + beaconNode.UUID);
        }
    }

    private void startTimer() {
        int time = (startTime + 1) * 1000;
        timer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                if (scanType == ALSManager.ScanType.SINGLE)
                    scanButton.setText("Scanning (" + millisUntilFinished / 1000 + ")");
                else
                    scanButton.setText("Stop Scan (" + millisUntilFinished / 1000 + ")");
                    //textView.setText("No Location avalable ("+ millisUntilFinished / 1000 +")");

            }

            public void onFinish() {
            }
        };
        timer.start();
    }

    private void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alsManager.removeLocationListener(MainActivity.this);
    }



    public void submitattendance(String token,String useremail,String regionname){

        progressDialog.setTitle("Submitting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_email", useremail)
                .addFormDataPart("beacon_name", regionname)
                .build();

        Request request = new Request.Builder()
                .url(URLs.URL_ATTENDANCE_SUBMIT)
                .post(formBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Dialog("Thank you "+ userName,"attendance taken successfully");
                        }
                    });
                    System.out.println("attendance was logged");
                }
                else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Dialog("Error ","Error submitting the attendance");

                        }
                    });
                    System.out.println("Error submitting the attendance");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Dialog("failed ","failed to fetch api");
                    }
                });
                System.out.println("failed to fetch api");
            }

        });


//        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URLs.URL_ATTENDANCE_SUBMIT,
//                new com.android.volley.Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        if(!response.isEmpty()){
//                            Dialog("Thank you "+ userName,"attendance taken successfully");
//
//                        }else{
//                            Dialog("Error ","Error submitting the attendance");
//
//                        }
//
//                    }
//                },
//                new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                // required parameters for the API
//                Map<String, String> params = new HashMap<>();
//                params.put("user_email", useremail);
//                params.put("beacon_name", regionname);
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("Authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    // checkBlock method to check the block title for user based on email and token
    public void checkBlock(String useremail, String Token){

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URLs.URL_BLOCKS+useremail,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting string response to json object
                            JSONObject obj = new JSONObject(response);
                            blocTitle = obj.getString("block_title");

                            Log.d(TAG, "checkBlock json respons respone is ======> " + obj);
                            Log.d(TAG, "block title json respons respone is ======> " + blocTitle);

                            bloctitle.setText(blocTitle);

                            if(bloctitle.getText().toString().isEmpty()){


                                //set title and message of dialog
                                builder.setMessage("Please contact student affairs to assign block to you then reopen the app") .setTitle("No Block found");
                                // dialog actions on YES
                                builder.setCancelable(false)
                                        .setPositiveButton("exit", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                finish();

                                            }
                                        });
                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();

                            }


//                            //storing the user block title in shared preferences
//                            Log.d(TAG, "SharedPrefManager block title respone iiiis ======> "
//                                    + SharedPrefManager.getInstance(getApplicationContext()));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check for Internet Connection
                        if (!Connection.InternetConnection(MainActivity.this)) {
                            Connection.Dialog(MainActivity.this,"Please check your Internet Connection to use the App","No Internet Connection");

                        }
                        bloctitle.setText("Click to Refresh data");
                        bloctitle.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkBlock(userEmail,token);
                            }
                        });
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + Token);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void Dialog(String title , String message){

        //set title and message of dialog
        builder.setMessage(message) .setTitle(title);

        // dialog actions on YES and NO
        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }


}
