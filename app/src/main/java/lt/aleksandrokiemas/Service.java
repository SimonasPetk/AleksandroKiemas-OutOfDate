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
    @POST("/Resources")
    Call<ImageUploadResponse> postImage(@Part MultipartBody.Part image);

    @POST("/issues")
    Call<ResponseBody> postData(@Field("resources") String[] resource, @Field("reporter_email") String emailAddress, @Field("comment") String last);
}

class ImageUploadResponse {
    private String id;

    public String getId() {
        return id;
    }
}

class IssueRequest{

    private List<String> resources;

    private String reporterEmail;

    private String comment;

    private String lat;

    public IssueRequest(List<String> resources, String reporterEmail, String comment, String lat) {
        super();
        this.resources = resources;
        this.reporterEmail = reporterEmail;
        this.comment = comment;
        this.lat = lat;
    }

}