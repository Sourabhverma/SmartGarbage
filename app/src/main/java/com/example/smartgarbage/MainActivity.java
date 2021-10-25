package com.example.smartgarbage;
import android.os.Bundle;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableLayout.LayoutParams;

import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
    private List<String> headerRow = List.of("_id", "bin_id","geolocation","region","status");




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
        tblrslt.setStretchAllColumns(true);
        tblrslt.bringToFront();


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
//                Log.i("Base Server URL",url);

                getData(url,String.valueOf(city.getText()),String.valueOf(binStatus.getText()));

            }
        });

        mapView_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainActivity.class);

                // start the activity connect to the specified class
                startActivity(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void getData(String baseUrl,String city,String status){
        Log.i("Base Server URL --",baseUrl);

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

                Type BinlistType = new TypeToken<List<BinModal>>() {}.getType();
                List<BinModal> binList = new Gson().fromJson(jsonString.get("result"), BinlistType);

                Log.i("BinList Count:",":"+binList.size());
                String msg ="Refresh successful , Count of Bins Record retrieved is "+binList.size();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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