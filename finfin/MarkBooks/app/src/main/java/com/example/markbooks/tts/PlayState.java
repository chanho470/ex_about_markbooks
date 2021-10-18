package com.example.markbooks.tts;

public enum PlayState {
    PLAY("PLAY"), WAIT("PAUSE"), STOP("STOP");

    private String state;

    PlayState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public boolean isStopping() {
        return this == STOP;
    }

    public boolean isWaiting() {
        return this == WAIT;
    }

    public boolean isPlaying() {
        return this == PLAY;
    }
}
