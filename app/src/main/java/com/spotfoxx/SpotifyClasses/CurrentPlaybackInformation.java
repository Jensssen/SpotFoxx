package com.spotfoxx.SpotifyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentPlaybackInformation {
    @SerializedName("device")
    @Expose
    private Device device;
    @SerializedName("shuffle_state")
    @Expose
    private Boolean shuffleState;
    @SerializedName("repeat_state")
    @Expose
    private String repeatState;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("context")
    @Expose
    private Context context;
    @SerializedName("progress_ms")
    @Expose
    private Integer progressMs;
    @SerializedName("item")
    @Expose
    private Item_ item;
    @SerializedName("currently_playing_type")
    @Expose
    private String currentlyPlayingType;
    @SerializedName("actions")
    @Expose
    private Actions actions;
    @SerializedName("is_playing")
    @Expose
    private Boolean isPlaying;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Boolean getShuffleState() {
        return shuffleState;
    }

    public void setShuffleState(Boolean shuffleState) {
        this.shuffleState = shuffleState;
    }

    public String getRepeatState() {
        return repeatState;
    }

    public void setRepeatState(String repeatState) {
        this.repeatState = repeatState;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Integer getProgressMs() {
        return progressMs;
    }

    public void setProgressMs(Integer progressMs) {
        this.progressMs = progressMs;
    }

    public Item_ getItem() {
        return item;
    }

    public void setItem(Item_ item) {
        this.item = item;
    }

    public String getCurrentlyPlayingType() {
        return currentlyPlayingType;
    }

    public void setCurrentlyPlayingType(String currentlyPlayingType) {
        this.currentlyPlayingType = currentlyPlayingType;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public Boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(Boolean playing) {
        isPlaying = playing;
    }
}
