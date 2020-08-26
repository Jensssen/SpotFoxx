package com.spotfoxx.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spotfoxx.Classes.SpotifyControl;
import com.spotfoxx.Classes.User;
import com.spotfoxx.R;
import com.spotfoxx.SpotifyClasses.Track;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

import java.util.List;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyViewHolder> {
    private List<Track> track_list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_track_name;
        private TextView tv_song_author;
        private RelativeLayout mRadioItem;
        private ImageView mSpotifyImage;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tv_track_name = itemView.findViewById(R.id.track_name);
            tv_song_author = itemView.findViewById(R.id.song_author);
            mRadioItem = itemView.findViewById(R.id.friend_item);
            mSpotifyImage = itemView.findViewById(R.id.image);

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

    public TrackAdapter(List<Track> playList) {
        this.track_list = playList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This method is called once for each item, the recycler view holds
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Track track_item = track_list.get(position);

        holder.tv_track_name.setText(track_item.getName());
        holder.tv_song_author.setText(track_item.getArtists().get(0).getName());
        SpotifyAppRemote mSpotifyAppRemote = SpotifyControl.getmSpotifyAppRemote(User.getContext());

        // Set album image if it is not null, else place placeholder image
        if (track_item.getAlbum().getImages().get(0) != null){
            List<com.spotfoxx.SpotifyClasses.Image> album_images = track_item.getAlbum().getImages();
            int album_img_length = album_images.size();
            ImageUri imageUri = new ImageUri(album_images.get(album_img_length - 1).getUrl());
            mSpotifyAppRemote
                    .getImagesApi()
                    .getImage(imageUri, Image.Dimension.THUMBNAIL)
                    .setResultCallback(
                            bitmap -> {
                                holder.mSpotifyImage.setImageBitmap(bitmap);

                            });
        }
        else{
            holder.mSpotifyImage.setImageResource(R.mipmap.ic_launcher);
        }


    }

    @Override
    public int getItemCount() {
        if (track_list !=null) {
            return track_list.size();
        } else {
            return 0;
        }
    }


}

