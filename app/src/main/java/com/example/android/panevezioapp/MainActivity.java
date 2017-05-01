package com.example.android.panevezioapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

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

import static com.example.android.panevezioapp.MainActivity.photo;

public class MainActivity extends Activity {

    private static int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private FrameLayout btnSelect;
    private ImageView ivImage;
    private String userChoosenTask, name, address;
    private EditText problemAddressField;
    int PLACE_PICKER_REQUEST = 2;


    EditText emailAddressField, descriptionField;
    String problemAddress, emailAddress, description;
    Button btnPost;

    static Bitmap photo;
    static String imageID;
    private static Context context;


    Details details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelect = (FrameLayout) findViewById(R.id.load_photo);

        ivImage = (ImageView) findViewById(R.id.image_placeholder);
        problemAddressField = (EditText) findViewById(R.id.problem_address);

        problemAddressField.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createPicker();
            }
        });


        emailAddressField = (EditText) findViewById(R.id.email_address_field);
        descriptionField = (EditText) findViewById(R.id.description_field);
        btnPost = (Button) findViewById(R.id.btnPost);


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



                    photo = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();

                    if (photo != null) {
                        new AsyncSendImage().execute("http://opendata.dashboard.lt/api/v2/resources");
                    } else {
                        try {
                            throw new RuntimeException("Viskas čia yra blogai...");
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }


                    // Toast.makeText(getBaseContext(), "Neįkėlete nuotraukos!", Toast.LENGTH_LONG).show();


            }
        });



        context = getApplicationContext();

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

           Toast.makeText(getBaseContext(), "Pranešimas nusiųstas!", Toast.LENGTH_LONG).show();



        }
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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == PLACE_PICKER_REQUEST)
                openPlacePicker(data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Fotografuoti"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Pasirinkti iš galerijos"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    private void selectImage() {
        final CharSequence[] items = {"Fotografuoti", "Pasirinkti iš galerijos",
                "Išeiti"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pridėkite nuotrauką!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Fotografuoti")) {
                    userChoosenTask = "Fotografuoti";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Pasirinkti iš galerijos")) {
                    userChoosenTask = "Pasirinkti iš galerijos";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Išeiti")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Pasirinkti nuotrauką"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    private void onCaptureImageResult(Intent data) {

        Cursor cursor = MainActivity.this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.ImageColumns.ORIENTATION
                },
                MediaStore.Images.Media.DATE_ADDED,
                null,
                "date_added DESC");

        Bitmap fullsize = null;
        if (cursor != null && cursor.moveToFirst()) {
            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            String photoPath = uri.toString();
            cursor.close();
            if (photoPath != null) {
                System.out.println("path: " + photoPath); //path from image full size
                fullsize = decodeSampledBitmap(photoPath);//here is the bitmap of image full size
            }
        }

        /**
         * Jeigu nori siųsti tik thumbnail.
         *
         * /Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

         File destination = new File(Environment.getExternalStorageDirectory(),
         System.currentTimeMillis() + ".jpg");

         FileOutputStream fo;
         try {
         destination.createNewFile();
         fo = new FileOutputStream(destination);
         fo.write(bytes.toByteArray());
         fo.close();
         } catch (FileNotFoundException e) {
         e.printStackTrace();
         } catch (IOException e) {
         e.printStackTrace();
         }*/

        ivImage.setImageBitmap(fullsize);
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


    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private Bitmap decodeSampledBitmap(String pathName,
                                       int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    //I added this to have a good approximation of the screen size:
    private Bitmap decodeSampledBitmap(String pathName) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return decodeSampledBitmap(pathName, width, height);
    }



}