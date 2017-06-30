package lt.aleksandrokiemas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class MainActivity extends Activity {


    String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    int PLACE_PICKER_REQUEST = 2;
    EditText reporterEmailEditText, descriptionEditText;
    Button btnPost;
    ApiService service;
    private FrameLayout btnSelect;
    private ImageView ivImage;
    private EditText addressEditText;
    private long mLastClickTime = 0;
    private File imageFile;
    double latitude, longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Nammu.init(getApplicationContext());


        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl("http://opendata.dashboard.lt/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ApiService.class);

        btnSelect = (FrameLayout) findViewById(R.id.load_photo);
        btnPost = (Button) findViewById(R.id.btnPost);

        ivImage = (ImageView) findViewById(R.id.image_placeholder);
        addressEditText = (EditText) findViewById(R.id.problem_address);
        reporterEmailEditText = (EditText) findViewById(R.id.email_address_field);
        descriptionEditText = (EditText) findViewById(R.id.description_field);


        addressEditText.setOnClickListener(new OnClickListener() {
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
                if (imageFile != null) {
                    Toast.makeText(getBaseContext(), "Pranešimas siunčiamas. Prašome palaukti.", Toast.LENGTH_SHORT).show();

                    new CreateImageAsyncTask().execute();
                } else {
                    Toast.makeText(getBaseContext(), "Pranešimas siunčiamas. Prašome palaukti.", Toast.LENGTH_SHORT).show();

                    createIssue(null);
                }
            }
        });
    }

    public void createIssue(String imageID) {
        List<String> resources = new ArrayList<String>();

        if (imageID != null) {
            resources = Arrays.asList(imageID);
        }

        IssueRequest issuerequest = new IssueRequest(
                resources,
                reporterEmailEditText.getText().toString(),
                addressEditText.getText().toString()+"\n"+descriptionEditText.getText().toString(),
                addressEditText.getText().toString(),
                latitude,
                longitude
        );

        service.createIssue(issuerequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Toast.makeText(getBaseContext(), "Pranešimas nusiųstas!", Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(getBaseContext(), "Pranešimo nusiųsti nepavyko...", Toast.LENGTH_LONG).show();
            }
        });

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
                MainActivity.this.imageFile = imageFile;
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
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void openPlacePicker(Intent data) {
        // The user has selected a place. Extract the name and address.
        final Place place = PlacePicker.getPlace(this, data);

        final CharSequence address = place.getAddress();

        addressEditText.setText(address);
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;

    }

    private class CreateImageAsyncTask extends AsyncTask<Void, Void, byte[]> {

        @Override
        protected byte[] doInBackground(Void... voids) {

            return ImageUtils.compressImageFile(MainActivity.this, imageFile);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", imageFile.getName(), RequestBody.create(MediaType.parse("image"), bytes));



            service.createImage(body).enqueue(new Callback<ImageUploadResponse>() {
                @Override
                public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {

                    createIssue(response.body().getId());
                }

                @Override
                public void onFailure(Call<ImageUploadResponse> call, Throwable t) {

                    Toast.makeText(getBaseContext(), "Nepavyko įkelti nuotraukos...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}