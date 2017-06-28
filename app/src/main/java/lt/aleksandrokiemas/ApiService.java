package lt.aleksandrokiemas;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Simonas Petkevičius on 2017-06-28.
 */

interface ApiService {
    @Multipart
    @POST("/api/v1/resources")
    Call<ImageUploadResponse> createImage(@Part MultipartBody.Part image);

    @POST("/api/v1/issues")
    Call<ResponseBody> createIssue(@Body IssueRequest postBody);
}

