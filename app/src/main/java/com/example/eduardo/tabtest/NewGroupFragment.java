package com.example.eduardo.tabtest;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.eduardo.tabtest.utils.NetworkUtilities;
import com.example.eduardo.tabtest.utils.Utilities;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewGroupFragment extends Fragment {
    public static final String EXTRA_GROUP_NAME = "name";
    public static final String EXTRA_GROUP_ICON = "icon";
    public static final String EXTRA_GROUP_ADMIN_ID = "admin_id";

    public NewGroupFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_group, container, false);

        Button button = (Button)rootView.findViewById(R.id.continue_button);

        final ImageView groupIcon = (ImageView) rootView.findViewById(R.id.image_group);
        final EditText nameText = (EditText) rootView.findViewById(R.id.name_text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String iconPath = "iconpath";
                long myID = Utilities.getMyID(getActivity());
                //Intent intent = new Intent(getActivity(), AssignSupervisor.class);
                Intent intent = new Intent(getActivity(), SelectMembersActivity.class);
                intent.putExtra(SelectMembersActivity.EXTRA_ACTION, "group");
                intent.putExtra(EXTRA_GROUP_NAME, name);
                intent.putExtra(EXTRA_GROUP_ICON, iconPath);
                intent.putExtra(EXTRA_GROUP_ADMIN_ID, myID);
                startActivity(intent);
            }
        });
        return rootView;
    }

}
