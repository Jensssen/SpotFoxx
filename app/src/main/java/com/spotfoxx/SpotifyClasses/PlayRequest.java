package com.spotfoxx.SpotifyClasses;

public class PlayRequest {

    private String context_uri;
    private Offset offset;
    private Integer position_ms;

    public String getContextUri() {
        return context_uri;
    }

    public void setContextUri(String contextUri) {
        this.context_uri = contextUri;
    }

    public Offset getOffset() {
        return offset;
    }

    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    public Integer getPositionMs() {
        return position_ms;
    }

    public void setPositionMs(Integer positionMs) {
        this.position_ms = positionMs;
    }
}