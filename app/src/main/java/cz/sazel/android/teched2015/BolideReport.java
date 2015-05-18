package cz.sazel.android.teched2015;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Bolide report record object based on https://data.nasa.gov/resource/mc52-syum.json?$where=altitude_km%3E20
 */
public class BolideReport implements Serializable {
    public BigInteger total_radiated_energy_j;
    public double altitude_km;
    public String date_time_peak_brightness_ut;

    public double calculated_total_impact_energy_kt;
    public String latitude_deg;
    public double velocity_components_km_s_vx;
    public double velocity_components_km_s_vy;
    public double velocity_components_km_s_vz;
    public String longitude_deg;

    public volatile String mGeocodedAddress="";

    public LatLng getLatLng() {
        double latitudeAbs=Double.parseDouble(latitude_deg.substring(0,latitude_deg.length()-2));
        double longitudeAbs=Double.parseDouble(longitude_deg.substring(0, longitude_deg.length() - 2));
        int latitudeMinus=latitude_deg.substring(latitude_deg.length()-1).equals("S")?-1:1;
        int longitudeMinus=longitude_deg.substring(longitude_deg.length()-1).equals("W")?-1:1;
        return new LatLng(latitudeAbs*latitudeMinus,longitudeAbs*longitudeMinus);
    }

    public String getGeocodedAddress() {
        return mGeocodedAddress;
    }

    public void setGeocodedAddress(String geocodedAddress) {
        mGeocodedAddress = geocodedAddress;
    }
}
