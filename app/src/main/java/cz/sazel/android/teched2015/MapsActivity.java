package cz.sazel.android.teched2015;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = "MapsActivity";
    @InjectView(R.id.sbYear)
    SeekBar mSbYear;
    @InjectView(R.id.tvYear)
    TextView mTvYear;
    @InjectView(R.id.llFilter)
    LinearLayout mLlFilter;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private RestAdapter mRestAdapter;
    private INASADataAPI mNasaDataAPI;
    HashMap<String, BolideReport> markerMap = new HashMap <String, BolideReport>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this); //this fills the instances
        setUpMapIfNeeded();
        mSbYear.setMax(100);
        mSbYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateData(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mNasaDataAPI = new NASADataAPI();
        mSbYear.setProgress(20);
        updateData(20);
    }

    /**
     * Method loads data from NASA Api after slider changes.
     *
     * @param progress progress from slider
     */
    private void updateData(int progress) {
        mTvYear.setText(String.format("altitude >%d km", progress));
        mNasaDataAPI.getBolideReportWhere(String.format("altitude_km>%d", progress), new Callback<List<BolideReport>>
                () {
            @Override
            public void success(final List<BolideReport> bolideReports, Response response) {
                Log.w(TAG, bolideReports.toString());
                    displayOnMap(bolideReports);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayOnMap(List<BolideReport> bolideReports) {
        mMap.clear();
        for (BolideReport bolideReport : bolideReports) {
            MarkerOptions markerOptions = new MarkerOptions();
            BitmapDescriptor ds;
            if (bolideReport.calculated_total_impact_energy_kt < 1) {
                ds = BitmapDescriptorFactory
                        .fromResource(R.mipmap.bolid_small);
            } else if (bolideReport.calculated_total_impact_energy_kt < 100) {
                ds = BitmapDescriptorFactory.fromResource(R
                        .mipmap.bolid_medium);
            } else {
                ds = BitmapDescriptorFactory.fromResource(R
                        .mipmap.bolid_big);
            }
            markerOptions.anchor(0, 1).icon(ds).position(bolideReport.getLatLng())
                    .title(bolideReport.date_time_peak_brightness_ut).snippet
                    (String.format("%s\n%skt", bolideReport.getGeocodedAddress(), bolideReport
                            .calculated_total_impact_energy_kt));
            Marker marker=mMap.addMarker(markerOptions);
            markerMap.put(marker.getId(), bolideReport);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly installed) and the
     * map has not already been instantiated.. This will ensure that we only ever call {@link #setUpMap()} once when
     * {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and {@link MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly installing/updating/enabling
     * the Google Play services. Since the FragmentActivity may not have been completely destroyed during this process
     * (it is likely that it would only be stopped or paused), {@link #onCreate(Bundle)} may not be called again so we
     * should call this method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we just add a marker
     * near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                BolideReport report=markerMap.get(marker.getId());
                if (report!=null) DetailActivity.startActivity(MapsActivity.this,report);
            }
        });

    }


}
