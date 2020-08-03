package com.spotfoxx.Classes;

import java.util.List;
import java.util.Map;

public class MyLocation {
    private String name;
    private int geo_restricted;
    private String spotify_uri;
    private String user_name;
    private List<Double> l;
    private Map<String, Object> like;
    private Map<String, Object> dislike;
    private int like_rate;


    public MyLocation(String name, int geo_restricted, String spotify_uri, String user_name, List<Double> l, Map<String, Object> like, Map<String, Object> dislike) {
        this.name = name;
        this.geo_restricted = geo_restricted;
        this.spotify_uri = spotify_uri;
        this.l = l;
        this.user_name = user_name;
        this.like = like;
        this.dislike = dislike;
    }

    public MyLocation() {

    }

    public List<Double> getL() {
        return l;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGeo_restricted() {
        return geo_restricted;
    }

    public void setGeo_restricted(int geo_restricted) {
        this.geo_restricted = geo_restricted;
    }

    public String getSpotify_uri() {
        return spotify_uri;
    }

    public void setSpotify_uri(String spotify_uri) {
        this.spotify_uri = spotify_uri;
    }

    public Map<String, Object> getLike() {
        return like;
    }

    public void setLike(Map<String, Object> like) {
        this.like = like;
    }

    public Map<String, Object> getDislike() {
        return dislike;
    }

    public void setDislike(Map<String, Object> dislike) {
        this.dislike = dislike;
    }

    public int getLike_rate() {
        return like_rate;
    }

    public void setLike_rate(int like_rate) {
        this.like_rate = like_rate;
    }
}
