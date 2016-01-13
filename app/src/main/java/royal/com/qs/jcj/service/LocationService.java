package royal.com.qs.jcj.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.List;

public class LocationService extends Service {
    private LocationManager lm;
    private MyLocationLinstener linstener;
    private SharedPreferences mPref;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config",MODE_PRIVATE);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        final List<String> allProviders = lm.getAllProviders();
        System.out.println(allProviders);

        linstener = new MyLocationLinstener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,linstener);
    }

    class MyLocationLinstener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            final String longitude ="经度" +location.getLongitude();
            final String latitude = "纬度"+location.getLatitude();
            final String accuracy = "精确度"  +location.getAccuracy();
            final String  altitude ="海拔" +location.getAltitude();

            mPref.edit().putString("location","经度是"+longitude+",纬度是"+latitude).commit();
            //存储位置信息后，为了节约内存和电量，需关闭服务
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(linstener);
    }
}
