package com.example.android.panevezioapp;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class MainMenu extends AppCompatActivity {

    private int DISPLAY_SHOW_CUSTOM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Aleksandro Kiemas");
    }

    public void openMap(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.google.com/maps/d/u/0/viewer?mid=1FZbf1x4LEe514_WMNo2ePvJLn9I&ll=55.44853640706444%2C23.594292999999993&z=9"));
        startActivity(intent);
    }

    public void openAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openProblems(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


    }
}
