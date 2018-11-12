package joshvocal.me.client.api.service;

import java.util.List;

import joshvocal.me.client.api.model.Marker;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MarkerClient {

    public static final String BASE_URL = "http://32ee96ad.ngrok.io/";

    @GET("markers")
    Call<List<Marker>> getMarkers();
}
