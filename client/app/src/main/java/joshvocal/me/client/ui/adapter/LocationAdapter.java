package joshvocal.me.client.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import joshvocal.me.client.R;
import joshvocal.me.client.api.model.Marker;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationHolder> {

    private Context mContext;
    private List<Marker> mMarkerList;
    private GoogleMap mMap;

    public LocationAdapter(Context context, List<Marker> markerList, GoogleMap map) {
        mContext = context;
        mMarkerList = markerList;
        mMap = map;
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);

        return new LocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        Marker currentMarker = mMarkerList.get(position);

        holder.mLat = currentMarker.getLat();
        holder.mLng = currentMarker.getLng();

        holder.mTitle.setText(String.valueOf(currentMarker.getLat()));
        holder.mSubtitle.setText(String.valueOf(currentMarker.getLng()));
    }

    @Override
    public int getItemCount() {
        return mMarkerList.size();
    }

    public class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mCircle;
        private TextView mInsideCircle;
        private TextView mTitle;
        private TextView mSubtitle;

        private double mLat;
        private double mLng;

        public LocationHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mCircle = itemView.findViewById(R.id.circle);
            mInsideCircle = itemView.findViewById(R.id.inside_circle);
            mTitle = itemView.findViewById(R.id.title_location);
            mSubtitle = itemView.findViewById(R.id.subtitle_location);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            CameraUpdate current = CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLng), 17);
            mMap.animateCamera(current);
        }
    }
}
