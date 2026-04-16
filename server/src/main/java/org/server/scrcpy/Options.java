package org.server.scrcpy;

public class Options {
    public static final String VIDEO_CODEC_H264 = "h264";
    public static final String VIDEO_CODEC_H265 = "h265";
    public static final String VIDEO_CODEC_AV1 = "av1";
    public static final String AUDIO_CODEC_AAC = "aac";
    public static final String AUDIO_CODEC_OPUS = "opus";
    public static final String AUDIO_CODEC_FLAC = "flac";
    public static final String AUDIO_CODEC_RAW = "raw";

    private int maxSize;
    private int bitRate;
    private int audioBitRate = 128000;
    private int maxFps = 60;
    private boolean tunnelForward;
    private boolean audioForward = true;
    private boolean turnScreenOff;
    private boolean keepAwake;
    private String videoCodec = VIDEO_CODEC_H264;
    private String audioCodec = AUDIO_CODEC_AAC;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(int audioBitRate) {
        this.audioBitRate = audioBitRate;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public boolean isTunnelForward() {
        return tunnelForward;
    }

    public void setTunnelForward(boolean tunnelForward) {
        this.tunnelForward = tunnelForward;
    }

    public boolean isAudioForward() {
        return audioForward;
    }

    public void setAudioForward(boolean audioForward) {
        this.audioForward = audioForward;
    }

    public boolean isTurnScreenOff() {
        return turnScreenOff;
    }

    public void setTurnScreenOff(boolean turnScreenOff) {
        this.turnScreenOff = turnScreenOff;
    }

    public boolean isKeepAwake() {
        return keepAwake;
    }

    public void setKeepAwake(boolean keepAwake) {
        this.keepAwake = keepAwake;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        if (VIDEO_CODEC_H265.equals(videoCodec)) {
            this.videoCodec = VIDEO_CODEC_H265;
        } else if (VIDEO_CODEC_AV1.equals(videoCodec)) {
            this.videoCodec = VIDEO_CODEC_AV1;
        } else {
            this.videoCodec = VIDEO_CODEC_H264;
        }
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        if (AUDIO_CODEC_OPUS.equals(audioCodec)
                || AUDIO_CODEC_FLAC.equals(audioCodec)
                || AUDIO_CODEC_RAW.equals(audioCodec)
                || AUDIO_CODEC_AAC.equals(audioCodec)) {
            this.audioCodec = audioCodec;
        } else {
            this.audioCodec = AUDIO_CODEC_AAC;
        }
    }
}
