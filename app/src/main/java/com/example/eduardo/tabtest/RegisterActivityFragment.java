package com.example.eduardo.tabtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.eduardo.tabtest.utils.NetworkUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterActivityFragment extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String USER_ID_PREF = "USER_ID_PREF";

    public static final String EXTRA_USER_PHONE_NUMBER = "PhoneNumber";
    public static final String EXTRA_USER_COUNTRY_CODE = "CountryCode";

    public RegisterActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button button = (Button) rootView.findViewById(R.id.register_button);

        final EditText countryCodeText = (EditText) rootView.findViewById(R.id.country_code_text);

        final EditText phoneNumberText = (EditText) rootView.findViewById(R.id.phone_number_text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = countryCodeText.getText().toString();
                String phoneNumber = phoneNumberText.getText().toString();

                Intent intent = new Intent( getActivity(), ProfileActivity.class);
                intent.putExtra(EXTRA_USER_COUNTRY_CODE, countryCode);
                intent.putExtra(EXTRA_USER_PHONE_NUMBER, phoneNumber);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
