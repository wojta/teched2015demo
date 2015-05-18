package cz.sazel.android.teched2015;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    private static String EXTRA_REPORT = "EXTRA_REPORT";
    @InjectView(R.id.tvDate)
    TextView mTvDate;
    @InjectView(R.id.tvAltitude)
    TextView mTvAltitude;
    @InjectView(R.id.tvImpactEnergy)
    TextView mTvImpactEnergy;
    @InjectView(R.id.tvLocation)
    TextView mTvLocation;

    BolideReport mReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        mReport= (BolideReport) getIntent().getSerializableExtra(EXTRA_REPORT);
        mTvDate.setText(mReport.date_time_peak_brightness_ut);
        mTvAltitude.setText(String.format("%.2fkm",mReport.altitude_km));
        mTvImpactEnergy.setText(String.format("%.2fkt",mReport.calculated_total_impact_energy_kt));
        final CachedGeocoder cachedGeocoder=new CachedGeocoder(this);
        AsyncTask<Void,Void,Void> atask=new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Address address=cachedGeocoder.getFromLocation(mReport.getLatLng().latitude,mReport
                            .getLatLng()
                            .longitude);
                    if (address!=null && address.getMaxAddressLineIndex()>-1) {
                        mReport.setGeocodedAddress(address.getAddressLine(0)+", "+address.getCountryName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mTvLocation.setText(mReport.getGeocodedAddress());
            }
        };
        atask.execute();



    }

    public static void startActivity(Context ctx, BolideReport report) {
        Intent intent = new Intent(ctx, DetailActivity.class);
        intent.putExtra(EXTRA_REPORT, report);
        ctx.startActivity(intent);
    }

    @OnClick(R.id.btNavigate)
    protected void btNavigate() {
        Uri.Builder builder = Uri.parse("http://maps.google.com/maps").buildUpon();
        //?f=d&daddr=51.448,-0.972
        builder.appendQueryParameter("f", "d");
        builder.appendQueryParameter("daddr",
                mReport.getLatLng().latitude +
                        "," +
                        mReport.getLatLng().longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(builder.build());

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application could handle Directions.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
