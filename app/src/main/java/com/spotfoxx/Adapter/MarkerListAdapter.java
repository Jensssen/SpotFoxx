package com.spotfoxx.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spotfoxx.Classes.FirebaseControl;
import com.spotfoxx.Classes.MyLocation;
import com.spotfoxx.Classes.User;
import com.spotfoxx.R;

import java.util.ArrayList;


public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListAdapter.MyViewHolder> {
    private ArrayList<MyLocation> mLocations;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_playlist_name;
        private TextView tv_like_dislike_ratio;
        private ImageView mSpotifyImage;
        private ImageButton mDeleteBtn;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tv_playlist_name = itemView.findViewById(R.id.tv_playlist_name);
            tv_like_dislike_ratio = itemView.findViewById(R.id.tv_like_dislike_ratio);

            mSpotifyImage = itemView.findViewById(R.id.image);
            mDeleteBtn = (ImageButton) itemView.findViewById(R.id.btn_delete_marker);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                }
            });
        }
    }

    public MarkerListAdapter(ArrayList<MyLocation> mLocations) {
        this.mLocations = mLocations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This method is called once for each item, the recycler view holds
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_placed_marker_item, parent, false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyLocation location_item = mLocations.get(position);
        // get number of likes
        int number_of_likes = 0;
        if (location_item.getLike() != null) {
            number_of_likes = location_item.getLike().size();
        }
        // get number of dislikes
        int number_of_dislikes = 0;
        if (location_item.getDislike() != null) {
            number_of_dislikes = location_item.getDislike().size();
        }
        // Set location playlist name
        if (location_item.getName() != null) {
            String playlist_name = location_item.getName();
            holder.tv_playlist_name.setText(playlist_name);
            holder.mSpotifyImage.setImageResource(R.mipmap.spotfoxxicon);
            holder.tv_like_dislike_ratio.setText("Likes: " + number_of_likes + " | " + "Dislikes" + number_of_dislikes);
        }

        holder.mDeleteBtn.setImageResource(R.drawable.ic_delete_black);

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location_item.getKey() != null){
                    removeItem(position, location_item.getKey());
                    User.removeUriFromAlreadyPlacedspotifyPlaylistUris(location_item.getSpotify_uri());
                }
            }
        });

    }

        @Override
    public int getItemCount() {
        if (mLocations != null) {
            return mLocations.size();
        } else {
            return 0;
        }
    }

    public void removeItem(int position, String marker_id) {
        mLocations.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mLocations.size());
        // Remove marker / location from db
        FirebaseControl.delete_location(marker_id);
    }

}

