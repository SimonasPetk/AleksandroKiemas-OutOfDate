package lt.aleksandrokiemas;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Simonas Petkevičius on 2017-06-28.
 */

interface Service {
    @Multipart
    @POST("/resources")
    Call<ImageUploadResponse> postImage(@Part MultipartBody.Part image);

    @POST("/issues")
    Call<ResponseBody> postData(@Field("resources") String[] resource, @Field("reporter_email") String emailAddress, @Field("comment") String last);
}

