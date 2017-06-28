package lt.aleksandrokiemas;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by Simonas Petkevičius on 2017-06-29.
 */

public class ImageUtils {

    @Nullable

    public static byte[] compressImageFile(Context context, File file) {
        Bitmap bitmap = createBitmap(context, file);
        if (bitmap == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        return byteArrayImage;
    }


    @Nullable

    private static Bitmap createBitmap(Context context, File file) {
        final int requiredWidth = 1600;
        final int requiredHeight = 1600;

        try {
            return Glide.with(context)
                    .load(file)
                    .asBitmap()
                    .fitCenter()
                    .into(requiredWidth, requiredHeight)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Log.v("ImageUtils", "Unable to create bitmap from " + file);

            return null;
        }
    }
}
