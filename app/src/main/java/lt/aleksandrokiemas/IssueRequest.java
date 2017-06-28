package lt.aleksandrokiemas;

import java.util.List;

/**
 * Created by Simonas Petkevičius on 2017-06-28.
 */

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