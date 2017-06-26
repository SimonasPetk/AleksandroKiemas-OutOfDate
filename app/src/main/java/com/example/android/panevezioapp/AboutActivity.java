package com.example.android.panevezioapp;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Aleksandro kiemas");
    }

    public void openFacebook(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.fb.me/Aleksandrokiemas"));
        startActivity(intent);
    }

    public void openInstagram(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.instagram.com/aleksandrokiemas"));
        startActivity(intent);
    }

    public void openEmail(View view) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"aleksandro.kiemas@gmail.com"});
        startActivity(Intent.createChooser(emailIntent, "Siųsti laišką naudojant..."));
    }
}
