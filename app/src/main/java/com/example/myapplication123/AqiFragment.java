package com.example.myapplication123;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AqiFragment extends Fragment {

    View view;
    public static String cityNameAUF;
    private TextView aqiRange;
    private TextView uviRange;
    private TextView mainAqi;
    private TextView mainUVI;
    private EditText tempCityName;
    private ImageView searchIcon;
    private TextView location1;
    private TextView location2;

    private static String latitude;
    private static String longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_aqi, container, false);
        mainAqi = view.findViewById(R.id.textAqi);
        mainUVI = view.findViewById(R.id.textUVI);
        location1 = view.findViewById(R.id.city1);
        location2 = view.findViewById(R.id.city2);
        tempCityName = view.findViewById(R.id.editcityname);
        searchIcon = view.findViewById(R.id.search);
        cityNameAUF = MainActivity.city_name;
        aqiRange = view.findViewById(R.id.aqidescription);
        uviRange = view.findViewById(R.id.uvidescription);

        findAQI_UVI();
        return view;
    }

    public void getCoords(String CityName){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+CityName+"&appid=API Key";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj1 = response.getJSONObject("coord");
                    JSONObject obj2 = response.getJSONObject("sys");
                    latitude = obj1.getString("lat");
                    longitude = obj1.getString("lon");
                    apiUvi();

                    String cityName = response.getString("name");
                    String countryName = obj2.getString("country");
                    location1.setText(cityName+" , "+countryName);
                    location2.setText(cityName+" , "+countryName);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        queue.add(jor);
    }

    public void apiAqi(String CityName){
        String url = "https://api.waqi.info/feed/"+CityName+"/?token=//Token id";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = response.getJSONObject("data");
                    String aqi = obj.getString("aqi");
                    mainAqi.setText(aqi);
                    int aqiNumber;
                    aqiNumber = Integer.parseInt(aqi);
                    if(aqiNumber >= 0 && aqiNumber <= 50)
                        aqiRange.setText("Category: Good\nHealth Message: None");
                    else if(aqiNumber>=51 && aqiNumber <=100)
                        aqiRange.setText("Category: Moderate\nHealth Message: Unusually sensitive people should reduce prolonged or heavy exertion");
                    else if(aqiNumber>=101 && aqiNumber<= 150)
                        aqiRange.setText("Category: Unhealthy for sensitive groups\nHealth message: Sensitive groups should reduce prolonged or heavy exertion ");
                    else if(aqiNumber>=151 && aqiNumber<= 200)
                        aqiRange.setText("Category: Unhealthy\nHealth Message:Sensitive groups should avoid prolonged or heavy exertion; general public should reduce prolonged or heavy exertion");
                    else if(aqiNumber>=201 && aqiNumber<= 300)
                        aqiRange.setText("Category: Very Unhealthy\n Health Message: Sensitive groups should avoid all physical activity outdoors; general public should avoid prolonged or heavy exertion");
                    else
                        aqiRange.setText("Category: Hazardous\n Health Message: Everyone should avoid all physical activity outdoors");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        queue.add(jor);
    }

    public void apiUvi(){
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&exclude=hourly,daily&appid=//API Key";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = response.getJSONObject("current");
                    String uvi = obj.getString("uvi");
                    mainUVI.setText(uvi);
                    int uviNumber;
                    uviNumber = Integer.parseInt(uvi);
                    if(uviNumber >=0 && uviNumber<=2)
                        uviRange.setText("Risk: Low\nYou can safely stay outside!\nIf you burn easily, cover up and use sunscreen");
                    else if(uviNumber >= 3 && uviNumber <= 5)
                        uviRange.setText("Risk: Moderate\nSeek shade during midday hours!\nGenerously apply broad spectrum SPF 15+ sunscreen every 1.5 hours.");
                    else if(uviNumber >=6 && uviNumber <=7)
                        uviRange.setText("Risk: High\nSeek shade during midday hours!\nReduce time in the sun between 10 a.m. and 4 p.m.Generously apply broad spectrum SPF 15+ sunscreen every 1.5 hours. Wear sunglasses");
                    else if(uviNumber>=8 && uviNumber<=10)
                        uviRange.setText("Risk: Very High\nAvoid being outside during midday hours!\nMinimize sun exposure between 10 a.m. and 4 p.m.If outdoors, seek shade.UV-blocking sunglasses");
                    else
                        uviRange.setText("Risk: Extreme\nAvoid being outside during midday hours!\nTry to avoid sun exposure between 10 a.m. and 4 p.m.If outdoors, seek shade and wear sun-protective clothing, a wide-brimmed hat, and UV-blocking sunglasses. Generously apply broad spectrum SPF 15+ sunscreen every 1.5 hours");

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        queue.add(jor);
    }

    public void defaultLocation(){
        tempCityName.setText(MainActivity.user_city);
        getCoords(MainActivity.user_city);
        apiAqi(MainActivity.user_city);
    }

    public void findAQI_UVI(){
        cityNameAUF = MainActivity.city_name;
        if(cityNameAUF.equals("")){
            defaultLocation();
        }
        else{
            tempCityName.setText(cityNameAUF);
            getCoords(cityNameAUF);
            apiAqi(cityNameAUF);
        }
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cityNameAUF = tempCityName.getText().toString();
                    if (!cityNameAUF.equals("")){
                        getCoords(cityNameAUF);
                        apiAqi(cityNameAUF);
                        MainActivity.city_name = cityNameAUF;
                    }
                    else {
                        defaultLocation();
                        MainActivity.city_name = "";
                    }
                    closeKeyboard();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}