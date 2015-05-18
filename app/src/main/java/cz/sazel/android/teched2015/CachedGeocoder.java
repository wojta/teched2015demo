package cz.sazel.android.teched2015;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created on 15.5.15.
 */
public class CachedGeocoder {
    Geocoder mGeocoder;
    static HashMap<Integer, Address> mCachedResults=new HashMap<>();

    public CachedGeocoder(Context context, Locale locale) {
        mGeocoder = new Geocoder(context, locale);
    }

    public CachedGeocoder(Context context) {
        mGeocoder = new Geocoder(context);
    }


    @Nullable
    public Address getFromLocation(double latitude, double longitude) throws IOException {
        int hash = Double.valueOf(latitude).hashCode() * 31 + Double.valueOf(longitude).hashCode();
        Address address = null;
        if (!mCachedResults.containsKey(hash)) {
            List<Address> addresses = mGeocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                address = addresses.get(0);
                mCachedResults.put(hash, address);
            }

        } else {
            address = mCachedResults.get(hash);
        }
        return address;
    }

}
