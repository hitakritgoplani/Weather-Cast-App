package com.example.myapplication123;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication123.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ActionBarDrawerToggle mtoggle;
    private DrawerLayout mdrawlayout;

    ActivityMainBinding binding;
    LocationManager locationManager;


    public static String city_name = "";
    public static String user_city = "";
    public static String app_lang = "";

    @SuppressLint("NonConstantResourceId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomnavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_temp:
                        replaceFragment(new TemperatureFragment(), "TemperatureFragment");
                        break;

                    case R.id.bottom_nav_aqi:
                        replaceFragment(new AqiFragment(), "AqiFragment");
                        break;

                    case R.id.bottom_nav_windspeed:
                        replaceFragment(new HumidityFragment(), "HumidityFragment");
                        break;
                }
                return true;
            }
        });

        mdrawlayout = findViewById(R.id.drawer_layout);

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        showBottomNavigationView();
                        replaceFragment(new TemperatureFragment(), "TemperatureFragment");
                        break;
                    case R.id.weather_forecast:
                        hideBottomNavigationView();
                        replaceFragment(new ForecastFragment(), "ForecastFragment");
                        break;
                    case R.id.weather_news:
                        hideBottomNavigationView();
                        replaceFragment(new NewsFragment(), "NewsFragment");
                        break;
                    case R.id.weather_compare:
                        hideBottomNavigationView();
                        replaceFragment(new CompareFragment(), "CompareFragment");
                        break;
                    case R.id.nav_settings:
                        hideBottomNavigationView();
                        replaceFragment(new SettingsFragment(), "SettingsFragment");
                        break;
                    case R.id.nav_about:
                        hideBottomNavigationView();
                        replaceFragment(new AboutFragment(), "AboutFragment");
                        break;
                }
                mdrawlayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mtoggle = new ActionBarDrawerToggle(this, mdrawlayout, R.string.open,R.string.close);
        mdrawlayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getLocation();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnected();
    }

    private void showBottomNavigationView() {
        View view = findViewById(R.id.bottomnavigationView);
        view.animate().translationY(0);
    }

    private void hideBottomNavigationView() {
        View view = findViewById(R.id.bottomnavigationView);
        view.animate().translationY(view.getHeight());
    }

    private void getLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (isLocationEnabled() && isOnline()) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 50, MainActivity.this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else if (isOnline() && !isLocationEnabled()) {
            user_city = "Mumbai";
            replaceFragment(new TemperatureFragment(), "TemperatureFragment");
            Toast.makeText(this, "Showing default city as Location Services turned off.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false)
                    .setMessage("Application requires Internet Services to function properly.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("TemperatureFragment");
        if (mdrawlayout.isDrawerOpen(GravityCompat.START)) {
            mdrawlayout.closeDrawer(GravityCompat.START);
        }
        else if(!(f instanceof TemperatureFragment)){
            BottomNavigationView mBottomNavigationView = findViewById(R.id.bottomnavigationView);
            mBottomNavigationView.getMenu().findItem(R.id.bottom_nav_temp).setChecked(true);
            showBottomNavigationView();
            replaceFragment(new TemperatureFragment(), "TemperatureFragment");
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != 0 && requestCode != 1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocation();
        }
    }

    public void replaceFragment(Fragment fragment, String fragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment, fragmentName);
        fragmentTransaction.commit();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mtoggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            user_city = addresses.get(0).getLocality();
            replaceFragment(new TemperatureFragment(), "TemperatureFragment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
