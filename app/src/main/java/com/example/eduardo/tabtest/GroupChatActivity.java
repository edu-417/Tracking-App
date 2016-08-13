package com.example.eduardo.tabtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.Inet4Address;

public class GroupChatActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_CLOUD_ID = "groupCloudID";
    public static final String EXTRA_GROUP_ID = "groupID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra(GroupTabFragment.EXTRA_TITLE));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_add_user){
            Intent intent = getIntent();
            long groupID = intent.getLongExtra(GroupTabFragment.EXTRA_GROUP_ID, 0);
            long groupCloudID = intent.getLongExtra(GroupTabFragment.EXTRA_GROUP_CLOUD_ID, 0);
            Intent memberIntent = new Intent(this, SelectMembersActivity.class);
            memberIntent.putExtra(SelectMembersActivity.EXTRA_ACTION, "member");
            memberIntent.putExtra(EXTRA_GROUP_ID, groupID);
            memberIntent.putExtra(EXTRA_GROUP_CLOUD_ID, groupCloudID);
            startActivity(memberIntent);

            return true;
        }

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

