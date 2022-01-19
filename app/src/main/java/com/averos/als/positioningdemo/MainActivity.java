package com.averos.als.positioningdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new AsyncHttpClient();

        alsManager = new ALSManager(MainActivity.this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Initializing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //editTextScanWindow = findViewById(R.id.editTxt);
        scanButton = findViewById(R.id.btn);
        postButton = findViewById(R.id.viewAttend);
        textView = findViewById(R.id.txtView);
        //scanTypeSpinner = findViewById(R.id.scanType);
        attendButton = findViewById(R.id.attend);
        recyclerView = findViewById(R.id.beacon_list);
        setupBeaconListView();
        getBuildingInfo(APIKey, companyId, new ServerCallback() {
            @Override
            public void onResponse(boolean status, String response) {
                buildings = new Buildings(getApplicationContext(), response);
                getBeacons(APIKey, companyId, new ServerCallback() {
                    @Override
                    public void onResponse(boolean status, String response) {
                        beacons = new Beacons(getApplicationContext(), response);
                        progressDialog.dismiss();
                        if (buildings.message.equals(ALSConstants.SUCCESS_RESPONSE) && beacons.message.equals(ALSConstants.SUCCESS_RESPONSE)) {
                            Toast.makeText(MainActivity.this, "successfully initialized", Toast.LENGTH_SHORT).show();
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

  //      TextView seekbarValue = findViewById(R.id.seekbar_rssi_value);
//        seekbarValue.setText("-120 dbm");
  //      SeekBar rssiSeekbar = findViewById(R.id.seekBar_rssi);
      //  rssiSeekbar.setMax(120);
   //     rssiSeekbar.setProgress(rssiSeekbar.getMax());
//        rssiSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
//                int value = (progress)*-1;
//                seekbarValue.setText(value+" dBm");
//                alsManager.setMinimumRSSI(value);
//            }
//        });

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



        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(myIntent);
                //setContentView(R.layout.attendance_list);

//                TableLayout tl = (TableLayout)findViewById(R.id.tableLayout1);
//                TableRow row = new TableRow(this);
//                TextView tv = new TextView(this);
//                tv.setText("This is text");
//
//                tl.addView(row);
//                row.addView(tv);


 //               OkHttpClient client = new OkHttpClient();
//                RequestBody formBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("user_email", userID)
//                        .build();
  //              HttpUrl url = HttpUrl.parse("https://attend.ksauhs.com/api/attendance/user/"+userID).newBuilder()
                        //.addQueryParameter("user_email", userID)
//                        .build();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .method("GET", null)
//                        .build();
//
//                Call call = client.newCall(request);
//                call.enqueue(new Callback() {
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    final Toast toast = Toast.makeText(MainActivity.this,response.body().toString(),Toast.LENGTH_LONG);
//                                    toast.show();
//                                    try {
//                                        String jsonData=response.body().string();
//                                        JSONArray Jobject = new JSONArray(jsonData);
//                                        for(int i=0; i < 10; i++) {
//                                            JSONObject jsonobject = Jobject.getJSONObject(i);
//                                            int id = jsonobject.getInt("id");
//                                            System.out.println(id);
//                                        }
//                                    } catch (IOException | JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                    }
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                final Toast toast = Toast.makeText(MainActivity.this, "failed to fetch api",Toast.LENGTH_LONG);
//                                toast.show();
//                            }
//                        });
//                        System.out.println("failed to fetch api");
//                    }
//
//                });
           }
        });


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
            textView.setText(
                    "Location X : " + location.x +
                            "\nLocation Y : " + location.y +
                            "\n\nBuilding ID : " + location.buildingId +
                            "\n\nBuilding Name : " + location.buildingName +
                            "\n\nFloor ID : " + location.floor +
                            string);
            attendButton.setVisibility(View.VISIBLE);
            attendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("user_email", userID)
                            .addFormDataPart("beacon_name", regionName)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://attend.ksauhs.com/api/attendance/")
                            .post(formBody)
                            .build();

                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        final Toast toast = Toast.makeText(MainActivity.this, response.message(),Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                                System.out.println("attendance was logged");
                            }
                            else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        final Toast toast = Toast.makeText(MainActivity.this, "Error submitting the attendance",Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                                System.out.println("Error submitting the attendance");
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    final Toast toast = Toast.makeText(MainActivity.this, "failed to fetch api",Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });
                            System.out.println("failed to fetch api");
                        }

                    });
                }
            });
        } else {
            textView.setText("No location available");
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(beaconListAdapter);
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

    private class BeaconViewHolder extends RecyclerView.ViewHolder {
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
}