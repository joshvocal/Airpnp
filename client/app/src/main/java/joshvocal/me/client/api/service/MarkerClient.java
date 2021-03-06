package joshvocal.me.client.api.service;

import java.util.List;

import joshvocal.me.client.api.model.Marker;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface MarkerClient {

    public static final String BASE_URL = "http://192.168.1.78:3000/";

    @GET("markers")
    Call<List<Marker>> getMarkers();

    @GET("markers/distance")
    Call<List<Marker>> getAllMarkersWithinDistance(
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("radius") String radius
    );

    @PUT("marker")
    Call<Marker> putMarker(
            @Query("lat") String lat,
            @Query("lng") String lng
    );

    @DELETE("marker")
    Call<Void> deleteMarker(
            @Query("lat") String lat,
            @Query("lng") String lng
    );
}
