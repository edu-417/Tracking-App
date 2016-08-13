package com.example.eduardo.tabtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eduardo.tabtest.data.DbUtilities;
import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileActivityFragment extends Fragment {

    private static final String TAG = ProfileActivityFragment.class.getSimpleName();
    private static int RESULT_LOAD_IMG = 1;
    private ImageView imageView;

    public ProfileActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final EditText nameText= (EditText)rootView.findViewById(R.id.name_text);

        Button button = (Button)rootView.findViewById(R.id.continue_button);

        imageView = (ImageView) rootView.findViewById(R.id.image_profile);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMG);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                String countryCode = intent.getStringExtra(RegisterActivityFragment.EXTRA_USER_COUNTRY_CODE);
                String phoneNumber = intent.getStringExtra(RegisterActivityFragment.EXTRA_USER_PHONE_NUMBER);
                String name = nameText.getText().toString();

                if( NetworkUtilities.isOnline( getActivity() ) ){
                    new RegisterUserTask().execute(countryCode, phoneNumber, name);
                }
                else{
                    Toast.makeText(getActivity(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utilities.putImageInView(getActivity(), requestCode, resultCode, data, imageView);
    }


    public class RegisterUserTask extends AsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... params) {

            Log.d(TAG, "REGISTERING USER");

            String countryCode = params[0];
            String phoneNumber = params[1];
            String name = params[2];

            JSONObject json = new JSONObject();
            try {
                json.put("country_code", countryCode);
                json.put("phone_number", phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "users/";
            String jsonStr = null;
            Pair<Integer, String> result = NetworkUtilities.postJSON(BASE_URL_POST, json);
            if(result != null){
                jsonStr = result.second;
            }


            Log.d(TAG, "REGISTERING PROFILE USER");

            long userCloudID = 0;

            if(jsonStr != null){
                try {
                    json = new JSONObject(jsonStr);
                    userCloudID = json.getLong("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                json = new JSONObject();
                try {
                    json.put("name", name);
                    json.put("user", userCloudID );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                BASE_URL_POST = NetworkUtilities.BASE_DOMAIN + "profiles/";
                NetworkUtilities.postJSON(BASE_URL_POST, json);
            }

            long contactID = DbUtilities.insertContact(getActivity(), userCloudID, countryCode, phoneNumber);
            DbUtilities.insertContactProfile(getActivity(), contactID, name, "");

            return userCloudID;
        }

        @Override
        protected void onPostExecute(Long userCloudID) {
            if(userCloudID > 0 ){
                SharedPreferences settings = getActivity().getSharedPreferences(RegisterActivityFragment.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(RegisterActivityFragment.USER_ID_PREF, userCloudID);
                editor.putBoolean(RegisterActivity.REGISTERED_PREF, true);
                editor.apply();

                Toast.makeText(getActivity(), "Register sucess", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent( getActivity(), MainActivity.class );
                startActivity(mainIntent);
            }
            else{
                Toast.makeText(getActivity(), "Error in register", Toast.LENGTH_LONG).show();
            }
        }
    }
}
