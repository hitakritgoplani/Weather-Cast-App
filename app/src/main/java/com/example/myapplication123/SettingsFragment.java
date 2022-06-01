package com.example.myapplication123;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_settings, container, false);

        loadLocale();

        Button lang = view.findViewById(R.id.changeLang);
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLangDialog();
            }
        });

        return view;
    }

    private void showChangeLangDialog(){
        final String[] lang = {"English", "हिन्दी", "Français"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Choose a Language....");
        builder.setSingleChoiceItems(lang, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    setLocale("en");
                    requireActivity().recreate();
                }
                else if (which == 1){
                    setLocale("hi");
                    requireActivity().recreate();
                }
                else if (which == 2){
                    setLocale("fr");
                    requireActivity().recreate();
                }
                dialog.dismiss();
                replaceFragment(new TemperatureFragment(), "TemperatureFragment");
            }
        });
        builder.create();
        builder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        requireActivity().getResources().updateConfiguration(configuration, requireActivity().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("App_lang", lang);
        MainActivity.app_lang = lang;
        editor.apply();
    }

    private void loadLocale(){
        SharedPreferences preferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String default_lang = preferences.getString("App_lang", "");
        Toast.makeText(requireActivity(), default_lang, Toast.LENGTH_SHORT).show();
        setLocale(default_lang);
    }

    public void replaceFragment(Fragment fragment, String fragmentName) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment, fragmentName);
        fragmentTransaction.commit();
    }

}