package cz.sazel.android.teched2015;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Query;

/**
 * Created on 15.5.15.
 */
public class NASADataAPI implements INASADataAPI {

    private final RestAdapter mRestAdapter;
    private final INASADataAPI mNasaDataAPI;

    public NASADataAPI() {
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint("https://data.nasa.gov/")
                .build();
        mNasaDataAPI = mRestAdapter.create(INASADataAPI.class);
    }

    @Override
    public void getBolideReportWhere(@Query("$where") String where, Callback<List<BolideReport>> cb) {
        mNasaDataAPI.getBolideReportWhere(where, cb);
    }


}
