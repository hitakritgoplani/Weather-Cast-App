package com.example.myapplication123;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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
import org.json.JSONException;
import org.json.JSONObject;

public class HumidityFragment extends Fragment {

    View view;
    public static String cityNameHF;
    private TextView humidity;
    private TextView windSpeed;
    private TextView location1;
    private TextView location2;
    private EditText tempCityName;
    private ImageView searchIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_humidity, container, false);
        windSpeed = view.findViewById(R.id.textwindspeed);
        humidity = view.findViewById(R.id.texthumidity);
        tempCityName = view.findViewById(R.id.editcityname);
        searchIcon = view.findViewById(R.id.search);
        location1 = view.findViewById(R.id.countryname1);
        location2 = view.findViewById(R.id.countryname2);
        cityNameHF = MainActivity.city_name;

        findHumidity_WindSpeed();
        return view;
    }

    public void apiHumidity(String CityName){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+CityName+"&appid=//API keydf3&units=metric";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj1 = response.getJSONObject("main");
                    JSONObject obj2 = response.getJSONObject("sys");
                    String humid = obj1.getString("humidity");
                    String cityName = response.getString("name");
                    String countryName = obj2.getString("country");
                    location1.setText(cityName+" , "+countryName);
                    location2.setText(cityName+" , "+countryName);
                    humidity.setText(humid + "%");
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

    public void apiWindSpeed(String CityName){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+CityName+"&appid=//API Key&units=metric";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = response.getJSONObject("wind");
                    String wind = obj.getString("speed");
                    String text = "kmph";
                    String finalText = wind + "\n" +text;
                    SpannableString ss = new SpannableString(finalText);
                    ss.setSpan(new RelativeSizeSpan(0.34f), finalText.length()-4,finalText.length(), 0);
                    windSpeed.setText(ss);

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

    public void defaultLocation(){
        tempCityName.setText(MainActivity.user_city);
        apiHumidity(MainActivity.user_city);
        apiWindSpeed(MainActivity.user_city);
    }

    public void findHumidity_WindSpeed(){
        cityNameHF = MainActivity.city_name;
        if(cityNameHF.equals("")){
            defaultLocation();
        }
        else {
            tempCityName.setText(cityNameHF);
            apiHumidity(cityNameHF);
            apiWindSpeed(cityNameHF);
        }
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cityNameHF = tempCityName.getText().toString();
                    if (!cityNameHF.equals("")){
                        apiHumidity(cityNameHF);
                        apiWindSpeed(cityNameHF);
                        MainActivity.city_name = cityNameHF;
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