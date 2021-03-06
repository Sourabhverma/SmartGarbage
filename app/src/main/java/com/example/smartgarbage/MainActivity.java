package com.example.smartgarbage;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    // creating variables for our edittext,
    // button, textview and progressbar.
    private Button reset_steps;
    private Button refresh;
    private Button close;
    private TextView city;
    private TextView binStatus;
    public  static TextView baseUrl;
    public  Button fit_app;
    public  Button mapView_app;
    private TableLayout tblrslt;
    private TableLayout tblrslt_header;
    private List<String> headerRow = List.of("bin_id "," region "," status "," geolocation");
    AlertDialog.Builder builder;
    List<BinModal> BinsFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = findViewById(R.id.city);
        binStatus = (TextView)findViewById(R.id.binStatus);
        baseUrl = findViewById(R.id.serverUrl);
        close = findViewById(R.id.exit);
        refresh = findViewById(R.id.refresh);
        mapView_app = findViewById(R.id.mapView);
        tblrslt = (TableLayout) findViewById(R.id.tblrslt);
        tblrslt_header = (TableLayout) findViewById(R.id.tblrslt);

        // adding on click listener to our button.
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validating if the text field is empty or not.
                if (baseUrl.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Server IP or DNS Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling a method to post the data and passing our name and job.
                Log.i("Action_Done","Refresh Started");
                String url = "http://"+baseUrl.getText()+"/";
                try{
                    BinsFull = getData(url,String.valueOf(city.getText()),String.valueOf(binStatus.getText()));
                }catch (Exception e) {
                    Log.i("Server Exception","Not Reachable : Please check Server IP or DNS Details"+e);
                    Toast.makeText(MainActivity.this, "Not Reachable : Please check Server IP or DNS Details", Toast.LENGTH_LONG).show();
                }
            }
        });

        mapView_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);

                // start the activity connect to the specified class
                HashMap Bins = new HashMap();
                if(BinsFull.size() > 0) {
                    for (int i = 0; i < BinsFull.size(); i++) {
                        Bins.put(BinsFull.get(i).getBin_id().toString(), BinsFull.get(i).getgelocation());
                    }
                    intent.putExtra("BinsFull", (Serializable) Bins);
                }
                else{
                    intent.putExtra("BinsFull", (Serializable) new HashMap());
                }
                startActivity(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
//        reset_steps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "http://"+baseUrl.getText()+"/";
//                postRestSteps(url);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    public List<BinModal> getData(String baseUrl, String city, String status){
        Log.i("Base Server URL --",baseUrl);
        List<BinModal> BinsFull = new ArrayList<BinModal>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> jsonCall;
        if(city != null && !city.trim().isEmpty()){
            jsonCall = retrofitAPI.getBins(city,status);
        }else {
            Toast.makeText(getApplicationContext(), "No City /Region passed , getting all Bin Status", Toast.LENGTH_SHORT).show();
            jsonCall = retrofitAPI.getBinAll();
        }

        jsonCall.enqueue(new Callback<JsonObject>(){
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonString = response.body();
                if(jsonString!= null){
                    Log.i("onResponse",jsonString.toString());
                }

                if (jsonString.get("result").isJsonArray() == false){
                    Log.i("No Result",jsonString.toString());
                    return;
                }


                Type BinlistType = new TypeToken<List<BinModal>>() {}.getType();
                List<BinModal> binList = new Gson().fromJson(jsonString.get("result"), BinlistType);

                Log.i("BinList Count:",":"+binList.size());
                String msg ="Refresh successful , Count of Bins Record retrieved is "+binList.size();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

                tblrslt.setStretchAllColumns(true);
//                tblrslt.bringToFront();


                tblrslt_header.setStretchAllColumns(true);
                TableRow head_tr =  new TableRow(tblrslt_header.getContext());
                for(int j = 0; j < headerRow.size(); j++){
                    TextView h1 = new TextView(tblrslt_header.getContext());
                    h1.setText(headerRow.get(j));
                    h1.setTextColor(0xFFFF3300);
                    head_tr.addView(h1);
                }
                tblrslt_header.setBackgroundResource(R.color.white);
                tblrslt_header.addView(head_tr);

                tblrslt.removeViews(1, Math.max(0, tblrslt.getChildCount() - 1));
                for(int i = 0; i < binList.size(); i++){
                    TableRow tr =  new TableRow(tblrslt.getContext());
//                    TextView c1 = new TextView(tblrslt.getContext());
//                    c1.setText(binList.get(i).get_id());
                    TextView c2 = new TextView(tblrslt.getContext());
                    c2.setText(" "+String.valueOf(binList.get(i).getBin_id()));
                    TextView c3 = new TextView(tblrslt.getContext());
                    c3.setText(" "+String.valueOf(binList.get(i).getRegion()));
                    TextView c4 = new TextView(tblrslt.getContext());
                    c4.setText(" "+String.valueOf(binList.get(i).getStatus()));
                    TextView c5 = new TextView(tblrslt.getContext());
                    c5.setText(" "+String.valueOf(binList.get(i).getgelocation()));
//                    tr.addView(c1);
                    tr.addView(c2);
                    tr.addView(c3);
                    tr.addView(c4);
                    tr.addView(c5);
                    if(binList.get(i).getStatus().equalsIgnoreCase("full")){
                        BinsFull.add(binList.get(i));
                    }
                    tblrslt.addView(tr);
                }

                if (BinsFull.size() >0){
                    String msg1 = new String("Details \n");
                    for(int i = 0; i < BinsFull.size(); i++){
                        msg1 = msg1+" S.No :"+(i+1)+" Bin_id :"+BinsFull.get(i).getBin_id() +" Region :"+BinsFull.get(i).getRegion()+" Latitude/Longitude :"+BinsFull.get(i).getgelocation()+"\n";
                    }
                    builder.setMessage(msg1)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("CRITICAL ALERT DUMPBINS FULL");
                    alert.show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return BinsFull;
    }

    public void postRestSteps(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
//        Call<JsonObject> reset_steps = retrofitAPI.stepReset();
//        reset_steps.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                JsonObject step_reset_rsp  = response.body();
////                Log.i("Reset Steps :",step_reset_rsp.toString());
//                Toast.makeText(getApplicationContext(), "Steps Counter Reset Successfully", Toast.LENGTH_SHORT).show();
//                getData(baseUrl);
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                t.printStackTrace();
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
//10.104.70.88/