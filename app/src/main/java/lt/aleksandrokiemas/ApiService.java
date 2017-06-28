package lt.aleksandrokiemas;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Simonas Petkevičius on 2017-06-28.
 */

interface ApiService {
    String HC_TOKEN_HEADER = "Hc-Token: 1rvoMjgZNb7U7sZlQTfkX1DweiqWGsvM8kiep8ueETdM4cqpUDqKyJPCkESdtk2eP2uw4PfMvFTxtQVX28mObQgZAcJobqj6V19APr9tbRZv7qskTcPUhBydK5gkBoavQtIhwLIQJl88OnH34Z9AI5ucHdMwx0kOw00SRKLcfu9CvrunA4hVSzZM3dktaxEKWR2pMNalC5YzJWb8tn2Ap7DR4PBI3zXm9pl17anslBMZ31bTK9JLfuMWZ2l1PQK";

    @Multipart
    @Headers(HC_TOKEN_HEADER)
    @POST("/api/v1/resources")
    Call<ImageUploadResponse> createImage(@Part MultipartBody.Part image);

    @Headers(HC_TOKEN_HEADER)
    @POST("/api/v1/issues")
    Call<ResponseBody> createIssue(@Body IssueRequest postBody);
}

