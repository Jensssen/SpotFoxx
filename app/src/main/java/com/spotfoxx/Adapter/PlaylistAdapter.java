package com.spotfoxx.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spotfoxx.Classes.Playlist_item;
import com.spotfoxx.Classes.SpotifyControl;
import com.spotfoxx.Classes.User;
import com.spotfoxx.R;
import com.spotfoxx.SpotifyClasses.Episode;
import com.spotfoxx.SpotifyClasses.Track;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

import java.util.List;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
    private List<Playlist_item> playList;
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

    public PlaylistAdapter(List<Playlist_item> playList) {
        this.playList = playList;
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
        Playlist_item playlist_item = playList.get(position);
            if(playlist_item.getTrack() != null){
                Track track = playlist_item.getTrack();
                holder.tv_track_name.setText(track.getName());
                holder.tv_song_author.setText(track.getArtists().get(0).getName());
                SpotifyAppRemote mSpotifyAppRemote = SpotifyControl.getmSpotifyAppRemote(User.getContext());

                // Set album image if it is not null, else place placeholder image
                if (track.getAlbum().getImages().get(0) != null){
                    List<com.spotfoxx.SpotifyClasses.Image> album_images = track.getAlbum().getImages();
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
            }else if (playlist_item.getEpisode() != null){
                Episode episode = playlist_item.getEpisode();
                holder.tv_track_name.setText(episode.getName());
                holder.tv_song_author.setText(episode.getShow().getName());
                SpotifyAppRemote mSpotifyAppRemote = SpotifyControl.getmSpotifyAppRemote(User.getContext());

                // Set album image if it is not null, else place placeholder image
                if (episode.getImages().get(0) != null){
                    List<com.spotfoxx.SpotifyClasses.Image> show_images = episode.getImages();
                    int show_img_length = show_images.size();
                    ImageUri imageUri = new ImageUri(show_images.get(show_img_length - 1).getUrl());
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
            }else {
            holder.tv_track_name.setText("Empty");
            holder.tv_song_author.setText("Empty");
            holder.mSpotifyImage.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        if (playList!=null) {
            return playList.size();
        } else {
            return 0;
        }
    }


}

