package com.example.myapplication123;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class TemperatureFragment extends Fragment{

    View view;
    private TextView mainTemp;
    private TextView weather;
    private TextView feelsLike;
    private TextView location;
    private TextView maxTemp;
    private TextView minTemp;
    private EditText tempCityName;
    public static String cityNameTF;
    private ImageView searchIcon;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temperature, container, false);

        mainTemp = view.findViewById(R.id.textTemp);
        weather = view.findViewById(R.id.tempMinBox2);
        feelsLike = view.findViewById(R.id.textTempMin2);
        location = view.findViewById(R.id.city1);
        maxTemp = view.findViewById(R.id.textTempMax);
        minTemp = view.findViewById(R.id.textTempMin);
        tempCityName = view.findViewById(R.id.editcityname);
        searchIcon = view.findViewById(R.id.search);
        TextView tv = view.findViewById(R.id.tempMinBox3);

        findWeather();
        return view;
    }

    public void apiTemp(String cityName){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid=//API key&units=metric";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj1 = response.getJSONObject("main");
                    JSONObject obj2 = response.getJSONObject("sys");
                    JSONArray arr1 = response.getJSONArray("weather");
                    JSONObject obj3 = arr1.getJSONObject(0);

                    String temp = String.valueOf(obj1.getInt("temp")+1);
                    String weather1 = obj3.getString("main");
                    String feels_temp = String.valueOf(obj1.getInt("feels_like"));
                    String min_temp = String.valueOf(obj1.getInt("temp_min")-2);
                    String max_temp = String.valueOf(obj1.getInt("temp_max")+2);
                    String cityName = response.getString("name");
                    String countryName = obj2.getString("country");

                    mainTemp.setText(temp+"\u2103");
                    weather.setText(weather1);
                    feelsLike.setText(feels_temp);
                    minTemp.setText(min_temp);
                    maxTemp.setText(max_temp);
                    location.setText(cityName+" , "+countryName);

                } catch (JSONException e) {
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
        apiTemp(MainActivity.user_city);
    }

    public void findWeather(){
        cityNameTF = MainActivity.city_name;
        if(cityNameTF.equals("")){
            defaultLocation();
        }
        else{
            tempCityName.setText(cityNameTF);
            apiTemp(cityNameTF);
        }
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cityNameTF = tempCityName.getText().toString();
                    if (!cityNameTF.equals("")){
                        apiTemp(cityNameTF);
                        MainActivity.city_name = cityNameTF;
                    }
                    else {
                        defaultLocation();
                        MainActivity.city_name = "";
                    }
                    closeKeyboard();
                }
                catch (Exception e){}
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