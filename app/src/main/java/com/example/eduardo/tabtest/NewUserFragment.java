package com.example.eduardo.tabtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.utils.NetworkUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A placeholder fragment containing a simple view.
 */
public class NewUserFragment extends Fragment {

    private static final String TAG = NewUserFragment.class.getSimpleName();
    public static final String EXTRA_CONTACT_PHONE_NUMBER = "phone_number";
    public static final String EXTRA_CONTACT_COUNTRY_CODE = "country_code";
    public static final String EXTRA_CONTACT_IS_SUPERVISOR = "is_supervisor";

    public NewUserFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button button = (Button) rootView.findViewById(R.id.continue_button);

        final EditText countryCodeText = (EditText) rootView.findViewById(R.id.country_code_text);
        final EditText phoneNumberText = (EditText) rootView.findViewById(R.id.phone_number_text);
        final CheckBox supervisorCheck = (CheckBox) rootView.findViewById(R.id.is_supervisor_check);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = countryCodeText.getText().toString();
                String phoneNumber = phoneNumberText.getText().toString();
                boolean isSupervisor = supervisorCheck.isChecked();

                Intent intent = getActivity().getIntent();
                long subscriptionID = intent.getLongExtra(SelectSubscriptionFragment.EXTRA_SUBSCRIPTION_ID, 0);
                long subscriptionCloudID = intent.getLongExtra(SelectSubscriptionFragment.EXTRA_SUBSCRIPTION_CLOUD_ID, 0);

                Intent monitoringIntent = new Intent(getActivity(), Monitoring.class);
                monitoringIntent.putExtra(EXTRA_CONTACT_COUNTRY_CODE, countryCode);
                monitoringIntent.putExtra(EXTRA_CONTACT_PHONE_NUMBER, phoneNumber);
                monitoringIntent.putExtra(EXTRA_CONTACT_IS_SUPERVISOR, isSupervisor);
                monitoringIntent.putExtra(SelectSubscriptionFragment.EXTRA_SUBSCRIPTION_ID, subscriptionID);
                monitoringIntent.putExtra(SelectSubscriptionFragment.EXTRA_SUBSCRIPTION_CLOUD_ID, subscriptionCloudID);
                startActivity(monitoringIntent);
            }
        });
        return rootView;
    }
}
