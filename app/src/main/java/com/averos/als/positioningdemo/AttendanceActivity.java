package com.averos.als.positioningdemo;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


public class AttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static RecyclerViewAdapter RecyclerViewAdapter;
    private List<AttendanceList> attendancelist;
    private static final String TAG = AttendanceList.class.getSimpleName();
    String token;
    String userEmail;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_attandance_list);

        token = SharedPrefManager.getInstance(getApplicationContext()).getToken().getToken();
        userEmail = SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();
        //toolbar
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("my Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statuscolor));
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendancelist = new ArrayList<>();
        attendancelist = getdata(token);
        RecyclerViewAdapter = new RecyclerViewAdapter(this,attendancelist);
        recyclerView.setAdapter(RecyclerViewAdapter);


    }

    public static void notifyRecyclerViewAdapter(){
        RecyclerViewAdapter.notifyDataSetChanged();

    }

    public List<AttendanceList> getdata(String token){
        attendancelist.clear();
        ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Loading...");
        progressdialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_ATTENDANCE+userEmail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.VISIBLE);

                        try {
                            //converting string response to json object
                            JSONArray objA = new JSONArray(response);


                            JSONObject obj =  objA.getJSONObject(0);


                            String beacon = obj.getString("beacon");
                            String time = obj.getString("created_at");
                            Log.d(TAG, "======BEACON IS ============>: " + beacon);
                            Log.d(TAG, "======time IS ============>: " + time);

                            AttendanceList list = new AttendanceList();
                            list.setBeacon(obj.getString("beacon"));
                            list.setCreated_at(obj.getString("created_at"));
                            Log.d(TAG, "======list BEACON IS ============>: " + list.getBeacon());

                            attendancelist.add(list);
                            RecyclerViewAdapter.notifyDataSetChanged();
                            progressdialog.dismiss();

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
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        return attendancelist;
    }
}
