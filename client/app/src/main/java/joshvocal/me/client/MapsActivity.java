package joshvocal.me.client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import joshvocal.me.client.api.model.Marker;
import joshvocal.me.client.api.service.MarkerClient;
import joshvocal.me.client.ui.CustomInfoWindowAdapter;
import joshvocal.me.client.util.SharedPreferencesHelper;
import joshvocal.me.client.util.ViewUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final float DEFAULT_ZOOM = 17f;

    private GoogleMap mMap;
    private ImageView mSearchBarCloseIcon;
    private ImageView mSearchBarSearchIcon;
    private RelativeLayout mSearchBar;
    private FloatingActionButton mLocationFab;
    private FloatingActionButton mAddFab;
    private ImageView mMarkerPlacementPreview;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SharedPreferencesHelper mSharedPreferencesHelper;

    LinearLayout mBottomSheet;
    BottomSheetBehavior mBottomSheetBehavior;

    private boolean mFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mSharedPreferencesHelper = new SharedPreferencesHelper(this);

        mSearchBar = findViewById(R.id.search_bar);
        mSearchBarCloseIcon = findViewById(R.id.search_bar_close_icon);
        mSearchBarSearchIcon = findViewById(R.id.search_bar_search_icon);
        mLocationFab = findViewById(R.id.fab_location);
        mLocationFab.setOnClickListener(this);
        mAddFab = findViewById(R.id.fab_add);
        mAddFab.setOnClickListener(this);
        mMarkerPlacementPreview = findViewById(R.id.marker_preview);

        setUpBottomSheet();
        setViewPaddingForTransparentStatusBar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpBottomSheet() {
        mBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    mAddFab.animate().scaleX(0).scaleY(0).setDuration(100).start();
                    mLocationFab.animate().scaleX(0).scaleY(0).setDuration(100).start();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    mAddFab.animate().scaleX(1).scaleY(1).setDuration(100).start();
                    mLocationFab.animate().scaleX(1).scaleY(1).setDuration(100).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void getCurrentDeviceLocation() {
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
                    animateMapCamera(latLng, DEFAULT_ZOOM);
                }
            }
        });

    }

    private LatLng getDeviceLastLocation() {
        double lat = Double.parseDouble(mSharedPreferencesHelper.getLastLat());
        double lng = Double.parseDouble(mSharedPreferencesHelper.getLastLng());
        return new LatLng(lat, lng);
    }

    private void animateMapCamera(LatLng latLng, float zoom) {
        CameraUpdate current = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(current);
    }

    private void moveMapCamera(LatLng latLng, float zoom) {
        CameraUpdate current = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(current);
    }

    private void putMarkerOnMap(String lat, String lng) {

        final Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MarkerClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MarkerClient markerClient = retrofit.create(MarkerClient.class);
        Call<Marker> markerCall = markerClient.putMarker(lat, lng);

        markerCall.enqueue(new PutMarkerResponseCallback());
    }

    private void populateMapWithMarkers() {

        final Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MarkerClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MarkerClient markerClient = retrofit.create(MarkerClient.class);
        Call<List<Marker>> markerCall = markerClient.getMarkers();

        markerCall.enqueue(new PopulateMarkerResponseCallback());
    }


    private class PutMarkerResponseCallback implements Callback<Marker> {

        @Override
        public void onResponse(Call<Marker> call, Response<Marker> response) {
            if (response.isSuccessful()) {
                Toast.makeText(MapsActivity.this, "Added to database", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Marker> call, Throwable t) {
            Toast.makeText(MapsActivity.this, "Fail", Toast.LENGTH_SHORT).show();
        }
    }

    private class DeleteMarkerResponseCallback implements Callback<Void> {

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                Toast.makeText(MapsActivity.this, "Deleted marker", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Toast.makeText(MapsActivity.this, "Fail", Toast.LENGTH_SHORT).show();
        }
    }

    private class PopulateMarkerResponseCallback implements Callback<List<Marker>> {

        @Override
        public void onResponse(Call<List<Marker>> call, Response<List<Marker>> response) {
            if (response.isSuccessful()) {
                List<Marker> markerList = response.body();

                for (Marker marker : markerList) {

                    Double lat = marker.getLat();
                    Double lng = marker.getLng();

                    MarkerOptions options = new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("Hello")
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
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

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

        populateMapWithMarkers();

        if (getDeviceLastLocation() != new LatLng(0, 0)) {
            moveMapCamera(getDeviceLastLocation(), DEFAULT_ZOOM);
        } else {
            getCurrentDeviceLocation();
        }
    }

    @Override
    public void onInfoWindowClick(com.google.android.gms.maps.model.Marker marker) {
        Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }

        animateMapCamera(marker.getPosition(), DEFAULT_ZOOM);

//        final Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(MarkerClient.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        MarkerClient markerClient = retrofit.create(MarkerClient.class);
//        Call<Void> markerCall = markerClient.deleteMarker(
//                String.valueOf(marker.getPosition().latitude),
//                String.valueOf(marker.getPosition().longitude));
//
//        markerCall.enqueue(new DeleteMarkerResponseCallback());

        return true;
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
                getCurrentDeviceLocation();
                break;
            case R.id.fab_add:
                if (mFlag) {
                    mFlag = false;

                    LatLng center = mMap.getCameraPosition().target;
                    mMarkerPlacementPreview.setVisibility(View.GONE);
                    MarkerOptions options = new MarkerOptions()
                            .title("Hello")
                            .snippet("World")
                            .position(center);
                    mMap.addMarker(options);

                    putMarkerOnMap(Double.toString(center.latitude), Double.toString(center.longitude));
                } else {
                    mFlag = true;

                    mMarkerPlacementPreview.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        String latString = String.valueOf(mMap.getCameraPosition().target.latitude);
        String lngString = String.valueOf(mMap.getCameraPosition().target.longitude);

        mSharedPreferencesHelper.setLastLatPrefKey(latString);
        mSharedPreferencesHelper.setLastLngPrefKey(lngString);
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
