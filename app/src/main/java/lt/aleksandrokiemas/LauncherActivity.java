package lt.aleksandrokiemas;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class LauncherActivity extends AppCompatActivity {

    private int DISPLAY_SHOW_CUSTOM = 1;

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int SELECT_FILE = 10;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_launcher);

        if (!isConnected()) {
            Toast.makeText(getBaseContext(), "Prisijunkite prie interneto!", Toast.LENGTH_LONG).show();
        }

        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SELECT_FILE);
        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Aleksandro kiemas");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted

                } else {

                    // permission denied
                }
                return;
            }

            case SELECT_FILE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted

                } else {

                    // permission denied
                }
                return;
            }


        }
    }

    public void openProblems(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void openAsks(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://goo.gl/VEvw7P"));
        startActivity(intent);
    }

    public void openInvite(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/Visit-Panevezys-886397051475920/"));
        startActivity(intent);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
