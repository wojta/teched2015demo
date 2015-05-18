package cz.sazel.android.teched2015;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created on 15.5.15.
 */
public interface INASADataAPI {

    @GET("/resource/mc52-syum.json")
    void getBolideReportWhere(@Query("$where") String where, Callback<List<BolideReport>> cb);
}
