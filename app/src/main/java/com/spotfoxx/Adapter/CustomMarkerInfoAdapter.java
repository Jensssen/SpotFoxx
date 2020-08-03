package com.spotfoxx.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.spotfoxx.R;

public class CustomMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private TextView tv_title;
    private TextView tv_like;
    private TextView tv_dislike;

    public CustomMarkerInfoAdapter(Context context) {
        this.mContext = context;
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.custom_marker_info, null);
    }

    private void renderWindowText(Marker marker, View view) {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_like = (TextView) view.findViewById(R.id.tv_like);
        tv_dislike = (TextView) view.findViewById(R.id.tv_dislike);

        String title = marker.getTitle();

        String[] likes_dislikes = marker.getSnippet().split("/");
        String like = likes_dislikes[0];
        String dislike = likes_dislikes[1];


        if (title != null) {
            tv_title.setText(title);
        }

        tv_like.setText(like);
        tv_dislike.setText(dislike);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
