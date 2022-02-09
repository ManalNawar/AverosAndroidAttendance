package com.averos.als.positioningdemo;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.MsalServiceException;
import com.microsoft.identity.client.MsalUiRequiredException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import androidx.core.app.ActivityCompat;

//import com.averos.als.positioningdemo.databinding.ActivityMainBinding;


public class LoginActivity extends AppCompatActivity {

    final static String CLIENT_ID = "cf3a6b35-96d8-40c4-956a-1e4ef077bbdf";
    final static String SCOPES [] = {"https://graph.microsoft.com/User.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";



    /* UI & Debugging Variables */
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button callGraphButton;
    Button signOutButton;
    Button attendenceLog;

    /* Azure AD Variables */
    public static PublicClientApplication sampleApp;
    public static AuthenticationResult authResult;

    public static String userID;
    String userName;
    BottomNavigationView nav_bottom;
    ImageView img;


    List<User> users;
    AlertDialog.Builder builder;

    Intent myIntent;
    Users user;
    //Toolbar toolbar;
    //LinearLayout beaconsUI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        builder = new AlertDialog.Builder(this);

        // Check for Internet Connection
        if (!checkInternetConnection()) {
            internetPermissionDialog("Please check your Internet Connection to use the App","No Internet Connection");
        }

        // location permission
        if (!checkLocationPermission()) {
        internetPermissionDialog("Location Should be turn on to use the App","Location turned off");
        }

        //check if user is loggedin
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Log.d(TAG, "user user is loggedin => : ");

        }


        ////////////////////////////tool bar ////////////////////////////////////
       // toolbar = findViewById(R.id.main_toolbar);
        //beaconsUI = findViewById(R.id.beaconsUI);
        // providing title for the ActionBar
       // toolbar.setVisibility(View.INVISIBLE);
       // beaconsUI.setVisibility(View.INVISIBLE);



        callGraphButton = (Button) findViewById(R.id.callGraph);
        //signOutButton = (Button) findViewById(R.id.clearCache);
        attendenceLog = (Button) findViewById(R.id.submitattendance);
       // nav_bottom = findViewById(R.id.bottomNavigationView);
        img = findViewById(R.id.icon);
        //Intent myIntent = new Intent(this, MainActivity.class);
        Intent myIntent = new Intent(this, MainActivity.class);
       // myIntent.putExtra("authResult", authResult.getUser().getName());



        callGraphButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!checkInternetConnection()) {
                    internetPermissionDialog("Please check your Internet Connection to use the App","No Internet Connection");
                    // location permission
                    if (!checkLocationPermission()) {
                        internetPermissionDialog("Location Should be turn on to use the App","Location turned off");
                    }
                }else {
                    onCallGraphClicked();
                }
            }
        });

//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onSignOutClicked();
//            }
//        });

        attendenceLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ///////////////passing data to main activity//////////////////////
                myIntent.putExtra("authResult", authResult.getUser().getName());
                Log.d(TAG, "sampleApp contain this => : " + sampleApp);
                myIntent.putExtra("sampleApp", String.valueOf(sampleApp));
                startActivity(myIntent);
            }
        });

        /* Configure your sample app and save state for this activity */
        sampleApp = null;
        if (sampleApp == null) {
            sampleApp = new PublicClientApplication(
                    this.getApplicationContext(),
                    CLIENT_ID);
        }

        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        //List<User> users = null;

        try {
            users = sampleApp.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */

                sampleApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
            } else {
                /* We have no user */

                /* Let's do an interactive request */
                sampleApp.acquireToken(this, SCOPES, getAuthInteractiveCallback());
            }
        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }

        // status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.border_color));
        }


//// ...
//
//
//// Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="https://attend.itjed.com/api/token";
//
//// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: "+ response.substring(0,500));
//                        Log.d(TAG, "Response is =======> " + authResult.getUser().getDisplayableId());
//                        System.out.print("Response is =======> " + response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
//            }
//        }){
//            protected Map<String, String> getparams(){
//                Map<String,String> params = new HashMap<>();
//                params.put("userEmail", authResult.getUser().getDisplayableId());
//                params.put("apiAppKey", "3D3679B75DF6F1987AB688C3B4493A97B908FB3F454B36D93FCDDC2CEC34DE84");
//                params.put("deviceID", "Attend's");
//                return params;
//            }
//        };
//
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);

    }


    //
// App callbacks for MSAL
// ======================
// getActivity() - returns activity so we can acquireToken within a callback
// getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
// getAuthInteractiveCallback() - callback defined to handle acquireToken() case
//



    public AppCompatActivity getActivity() {
        return this;
    }

    /* Callback method for acquireTokenSilent calls
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;



//                Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
//                        .show();
//                System.out.print(authResult);

                /* call graph */
                callGraphAPI();
                //login("yaho@yaho.com");

                /* update the UI to post call Graph state */
                updateSuccessUI();

                //login(authResult.getUser().getDisplayableId());

                //startActivity(myIntent);
               // startActivity(new Intent(getActivity(),MainActivity.class));



            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());


                /* Store the auth result */
                authResult = authenticationResult;
                Log.d(TAG, "auth user information string is "+authResult.toString());
                Log.d(TAG, "auth user name information name is "+ authResult.getUser().getName());
                Log.d(TAG, "auth user email information id is "+ authResult.getUser().getDisplayableId());
                /* call Graph */
                callGraphAPI();

                /* update the UI to post call Graph state */
                //login("yaho@yaho.com");
                updateSuccessUI();
                //login(authResult.getUser().getDisplayableId());
                //startActivity(myIntent);
              //  startActivity(new Intent(getActivity(),MainActivity.class));







            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {

//        //check if user is loggedin
//        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//            Log.d(TAG, "user user is loggedin => : ");
//
//        }
        callGraphButton.setVisibility(View.INVISIBLE);
        //signOutButton.setVisibility(View.VISIBLE);
        attendenceLog.setVisibility(View.VISIBLE);
//        findViewById(R.id.welcome).setVisibility(View.VISIBLE);
//        ((TextView) findViewById(R.id.welcome)).setText("Welcome, " +
//                authResult.getUser().getName());
        //nav_bottom.setVisibility(View.VISIBLE);
        img.setVisibility(View.INVISIBLE);

//        toolbar.setVisibility(View.VISIBLE);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Home");
//
//        beaconsUI.setVisibility(View.VISIBLE);


        //findViewById(R.id.graphData).setVisibility(View.VISIBLE);

    }

    /* Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    private void onCallGraphClicked() {
        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
    }

    /* Handles the redirect from the System Browser */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());
                try {
                    userID=response.getString("mail");
                    userName = response.getString("displayName");
                   //
                     login(userID,userName);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateGraphUI(response);
               // login(userID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /* Sets the Graph response */
    private void updateGraphUI(JSONObject graphResponse) {
       // TextView graphText = (TextView) findViewById(R.id.graphData);
       // graphText.setText(graphResponse.toString());
    }

    /* Clears a user's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     */
    private void onSignOutClicked() {

        /* Attempt to get a user and remove their cookies from cache */
       // List<User> users = null;

        try {
            users = sampleApp.getUsers();

            if (users == null) {
                /* We have no users */

            } else if (users.size() == 1) {
                /* We have 1 user */
                /* Remove from token cache */
                sampleApp.remove(users.get(0));
                updateSignedOutUI();

            }
            else {
                /* We have multiple users */
                for (int i = 0; i < users.size(); i++) {
                    sampleApp.remove(users.get(i));
                }
            }

            Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                    .show();

        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    /* Set the UI for signed-out user */
    private void updateSignedOutUI() {
        callGraphButton.setVisibility(View.VISIBLE);
       // signOutButton.setVisibility(View.INVISIBLE);
        attendenceLog.setVisibility(View.INVISIBLE);
        //findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
        img.setVisibility(View.VISIBLE);
//        toolbar.setVisibility(View.INVISIBLE);
//        beaconsUI.setVisibility(View.INVISIBLE);
        // findViewById(R.id.graphData).setVisibility(View.INVISIBLE);
        //((TextView) findViewById(R.id.graphData)).setText("No Data");

    }

    public void login(String userEmail,String userName){

        //final TextView textView = (TextView) findViewById(R.id.respon);
        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.VISIBLE);

                        try {
                            //converting string response to json object
                            JSONObject obj = new JSONObject(response);

                            JSONObject jsondata = obj.getJSONObject("data");
                            String email = jsondata.getString("email");
                            String token = jsondata.getString("token");

                            Log.d(TAG, "json respons respone is ======> " + obj);
                            Log.d(TAG, "ison string email respone is ======> " + email);
                            Log.d(TAG, "ison string token respone is ======> " + token);


                            //if no error in response
                            if (obj.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();

//                                //getting the user from the response
//                                JSONObject userJson = obj.getJSONObject("data");
//                                Log.d(TAG, "userJson respone is ======> " + userJson);

                                //creating a new user object store email, name and token
//                                Users user = new Users(
////                                        userEmail,
////                                        userName,
//                                        jsondata.getString("token")
//                                );

                                Users user = new Users(
                                        userName,
                                        userEmail,
                                        jsondata.getString("token")
                                );


                                //storing the user token in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                Log.d(TAG, "SharedPrefManager email respone is ======> "
                                        + SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail());
                                Log.d(TAG, "SharedPrefManager name respone is ======> "
                                        + SharedPrefManager.getInstance(getApplicationContext()).getUser().getName());
                                Log.d(TAG, "SharedPrefManager token respone is ======> "
                                        + SharedPrefManager.getInstance(getApplicationContext()).getToken().getToken());
                                checkBlock(userEmail,SharedPrefManager.getInstance(getApplicationContext()).getToken().getToken());

//                                //starting the MainActivity activity
//                                finish();
//                                startActivity(myIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "faild due to ======> "
                                        + response);
                                Log.d(TAG, "json respons respone is ======> " + response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("userEmail", userEmail);
                params.put("apiAppKey", URLs.API_APP_KEY);
                params.put("deviceID", "attend");
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void checkBlock(String userEmail, String token){

        //final TextView textView = (TextView) findViewById(R.id.respon);
        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_BLOCKS+userEmail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.VISIBLE);

                        try {
                            //converting string response to json object
                            JSONObject obj = new JSONObject(response);

                           // JSONObject jsondata = obj.getJSONObject("data");


                            Log.d(TAG, "checkBlock json respons respone is ======> " + obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public boolean checkInternetConnection() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();


            return connected;

        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

//    public boolean checkLocationPermission(){
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED){
//            fusedLocationClint.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//
//                    if(location != null){
//                        System.out.print(location);
//                    }else {
//                        LocationPermission();
//                    }
//                }
//            });
//        }
//        return false;
//    }
    public boolean checkLocationPermission(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }

    }

    public void internetPermissionDialog(String message, String title){

        //set title and message of dialog
        builder.setMessage(message) .setTitle(title);

        // dialog actions on YES and NO
        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //builder.setTitle("No Internet Connection");
        alert.show();
    }

}
