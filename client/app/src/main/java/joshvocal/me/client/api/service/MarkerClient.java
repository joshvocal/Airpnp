package joshvocal.me.client.api.service;

import java.util.List;

import joshvocal.me.client.api.model.Marker;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MarkerClient {

    public static final String BASE_URL = "http://192.168.1.78:3000/";

    @GET("markers")
    Call<List<Marker>> getMarkers();
}
