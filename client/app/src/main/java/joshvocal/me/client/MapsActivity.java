package joshvocal.me.client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import joshvocal.me.client.api.model.Marker;
import joshvocal.me.client.api.service.MarkerClient;
import joshvocal.me.client.util.ViewUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final float DEFAULT_ZOOM = 17f;

    private GoogleMap mMap;
    private ImageView mSearchBarCloseIcon;
    private ImageView mSearchBarSearchIcon;
    private RelativeLayout mSearchBar;
    private FloatingActionButton mLocationFab;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSearchBar = findViewById(R.id.search_bar);
        mSearchBarCloseIcon = findViewById(R.id.search_bar_close_icon);
        mSearchBarSearchIcon = findViewById(R.id.search_bar_search_icon);
        mLocationFab = findViewById(R.id.fab_location);
        mLocationFab.setOnClickListener(this);

        setViewPaddingForTransparentStatusBar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = ((Location) task.getResult());
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    moveMapCamera(latLng, DEFAULT_ZOOM);

                    MarkerOptions options = new MarkerOptions()
                            .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    mMap.addMarker(options);
                    Log.d("pLS", "onComplete: " + latLng.toString());
                }
            }
        });

    }

    private void moveMapCamera(LatLng latLng, float zoom) {
        CameraUpdate current = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(current);
    }

    private void populateMarkWithMarkers() {

        final Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MarkerClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MarkerClient markerClient = retrofit.create(MarkerClient.class);
        Call<List<Marker>> markerCall = markerClient.getMarkers();

        markerCall.enqueue(new Callback<List<Marker>>() {

            @Override
            public void onResponse(Call<List<Marker>> call, Response<List<Marker>> response) {
                if (response.isSuccessful()) {
                    List<Marker> markerList = response.body();

                    for (Marker marker : markerList) {

                        Double lat = marker.getLat();
                        Double lng = marker.getLng();

                        MarkerOptions options = new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .snippet(marker.toString());

                        mMap.addMarker(options);

                        Log.d("YOLO", "onResponse: " + marker.toString());
                    }
                }

                Toast.makeText(MapsActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Marker>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

            ActivityCompat.requestPermissions(this, permissions, 1);

            return;
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        getDeviceLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setViewPaddingForTransparentStatusBar() {

        int statusBarHeight = ViewUtil.getStatusBarHeight(this);

        RelativeLayout.MarginLayoutParams searchBarParams =
                (RelativeLayout.MarginLayoutParams) mSearchBar.getLayoutParams();

        searchBarParams.setMargins(searchBarParams.leftMargin,
                searchBarParams.topMargin + statusBarHeight,
                searchBarParams.rightMargin,
                searchBarParams.bottomMargin);

        mSearchBar.setLayoutParams(searchBarParams);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_location:
                getDeviceLocation();
                populateMarkWithMarkers();
                break;
            default:
                break;
        }
    }
}
