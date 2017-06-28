package lt.aleksandrokiemas;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class MainActivity extends Activity {


    String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private FrameLayout btnSelect;
    private ImageView ivImage;
    private String userChoosenTask, name, address;
    private EditText problemAddressField;
    int PLACE_PICKER_REQUEST = 2;
    private long mLastClickTime = 0;

    EditText emailAddressField, descriptionField;
    String problemAddress, emailAddress, description;
    Button btnPost;

    ApiService service;

    static Bitmap photo;
    static String imageID;
    private Context context;


    Details details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        Nammu.init(getApplicationContext());



        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl("http://opendata.dashboard.lt/api/v1")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ApiService.class);

        btnSelect = (FrameLayout) findViewById(R.id.load_photo);
        btnPost = (Button) findViewById(R.id.btnPost);

        ivImage = (ImageView) findViewById(R.id.image_placeholder);
        problemAddressField = (EditText) findViewById(R.id.problem_address);
        emailAddressField = (EditText) findViewById(R.id.email_address_field);
        descriptionField = (EditText) findViewById(R.id.description_field);

        problemAddressField.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                createPicker();
            }
        });


        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        btnPost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                problemAddress = problemAddressField.getText().toString();
                emailAddress = emailAddressField.getText().toString();
                description = descriptionField.getText().toString();


                boolean hasDrawable = (ivImage.getDrawable() != null);
                if (hasDrawable) {
                    // imageView has image in it
                    photo = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();

                    if (photo != null) {
                        new AsyncSendImage().execute("http://opendata.dashboard.lt/api/v1/resources");

                    } else {
                        try {
                            throw new RuntimeException("Viskas čia yra blogai...");
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // no image assigned to image view
                    Toast.makeText(getBaseContext(), "Neįkėlete nuotraukos!", Toast.LENGTH_LONG).show();
                }

                // Toast.makeText(getBaseContext(), "Neįkėlete nuotraukos!", Toast.LENGTH_LONG).show();


            }
        });


    }

    

    private class AsyncSendImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST_IMAGE(urls[0], photo);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                imageID = jsonObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Toast.makeText(getBaseContext(), imageID, Toast.LENGTH_LONG).show();

            new AsyncSendComment().execute("http://opendata.dashboard.lt/api/v1/issues");
        }
    }

    public static String POST_IMAGE(String url, Bitmap bitmap) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            // 3. set json to StringEntity

            File file = new File(context.getFilesDir(), "image.jpg");
            FileOutputStream fileos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileos);

            org.apache.http.entity.mime.MultipartEntity me = new org.apache.http.entity.mime.MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            me.addPart("file", new FileBody(file));

            if (!file.exists()) {
                try {
                    throw new RuntimeException("Tu dorns...");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            // 4. set httpPost Entity
            httpPost.setEntity(me);


            // 4.1 Set some headers to inform server about the type of the content
            httpPost.setHeader("Hc-Token", "1rvoMjgZNb7U7sZlQTfkX1DweiqWGsvM8kiep8ueETdM4cqpUDqKyJPCkESdtk2eP2uw4PfMvFTxtQVX28mObQgZAcJobqj6V19APr9tbRZv7qskTcPUhBydK5gkBoavQtIhwLIQJl88OnH34Z9AI5ucHdMwx0kOw00SRKLcfu9CvrunA4hVSzZM3dktaxEKWR2pMNalC5YzJWb8tn2Ap7DR4PBI3zXm9pl17anslBMZ31bTK9JLfuMWZ2l1PQK");

            httpPost.toString();

            // 5. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 6. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // 7. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else
                result = "Nesuveikė!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 8. return result

        return result;
    }

    private class AsyncSendComment extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            details = new Details();
            details.setProblemAddress(problemAddress);
            details.setEmailAddress(emailAddress);
            details.setDescription(description);

            return POST(urls[0], details);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            // Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            //

            openAbout(btnPost);

            Toast.makeText(getBaseContext(), "Pranešimas nusiųstas!", Toast.LENGTH_LONG).show();


        }
    }

    public void openAbout(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public static String POST(String url, Details details) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "{";

            // 3. build jsonObject

            json += "\"resources\":[\"";
            json += imageID;
            json += "\"],\"";
            json += "reporter_email\":\"";
            json += details.getEmailAddress();
            json += "\",\"comment\":\"";
            json += details.getDescription();
            json += "\",\"lat\":\"";
            json += "0";
            json += "\"}";


            // 4. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 5. set httpPost Entity
            httpPost.setEntity(se);

            // 6. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Hc-Token", "1rvoMjgZNb7U7sZlQTfkX1DweiqWGsvM8kiep8ueETdM4cqpUDqKyJPCkESdtk2eP2uw4PfMvFTxtQVX28mObQgZAcJobqj6V19APr9tbRZv7qskTcPUhBydK5gkBoavQtIhwLIQJl88OnH34Z9AI5ucHdMwx0kOw00SRKLcfu9CvrunA4hVSzZM3dktaxEKWR2pMNalC5YzJWb8tn2Ap7DR4PBI3zXm9pl17anslBMZ31bTK9JLfuMWZ2l1PQK");


            // 7. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 8. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // 9. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else
                result = "Nesuveikė!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 10. return result

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Glide.with(MainActivity.this).load(imageFile).into(ivImage);
            }


            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST)
                openPlacePicker(data);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void selectImage() {
        Nammu.askForPermission(this, PERMISSIONS, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                EasyImage.openChooserWithGallery(MainActivity.this, "Pasirinkite įkėlimo būdą", 0);
            }

            @Override
            public void permissionRefused() {
            }
        });


    }


    /**
     * Called when the user clicks the Map button
     */

    public void createPicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void openPlacePicker(Intent data) {


        // The user has selected a place. Extract the name and address.
        final Place place = PlacePicker.getPlace(data, this);

        final CharSequence name = place.getName();
        final CharSequence address = place.getAddress();
        final LatLng location = place.getLatLng();

        String locations = null;


        problemAddressField.setText(address);

        //problemAddressField.setText(name);
        //Toast.makeText(getBaseContext(),  data, Toast.LENGTH_LONG).show();

        String attributions = PlacePicker.getAttributions(data);
        if (attributions == null) {
            attributions = "";
        }


    }


}