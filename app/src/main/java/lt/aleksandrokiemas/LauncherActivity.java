package lt.aleksandrokiemas;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class LauncherActivity extends AppCompatActivity {

    private int DISPLAY_SHOW_CUSTOM = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_launcher);


        if (!isConnected()) {
            Toast.makeText(getBaseContext(), "Prisijunkite prie interneto!", Toast.LENGTH_LONG).show();
        }


        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("Aleksandro kiemas");
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
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.kulturapanevezys.lt/"));
        startActivity(intent);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
