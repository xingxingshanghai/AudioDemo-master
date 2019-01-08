package com.jhuster.audiodemo.tester;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;

import com.jhuster.audiodemo.api.audio.AudioCapturer;
import com.jhuster.audiodemo.api.audio.AudioPlayer;
import com.jhuster.audiodemo.api.wav.WavFileWriter;

import java.io.IOException;

public class AudioCaptureTester extends Tester implements AudioCapturer.OnAudioFrameCapturedListener {

    private static final String DEFAULT_TEST_FILE = Environment.getExternalStorageDirectory() + "/test.wav";

    private AudioCapturer mAudioCapturer;
    private WavFileWriter mWavFileWriter;
    private AudioPlayer mAudioPlayer;
    @Override
    public boolean startTesting() {
        mAudioCapturer = new AudioCapturer();
        mWavFileWriter = new WavFileWriter();
        mAudioPlayer = new AudioPlayer();
        try {
            mWavFileWriter.openFile(DEFAULT_TEST_FILE, 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        mAudioCapturer.setOnAudioFrameCapturedListener(this);
        mAudioPlayer.startPlayer(AudioManager.STREAM_MUSIC,44100,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        return mAudioCapturer.startCapture(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }

    @Override
    public boolean stopTesting() {
        mAudioCapturer.stopCapture();
        try {
            mAudioPlayer.stopPlayer();
            mWavFileWriter.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onAudioFrameCaptured(byte[] audioData) {
        mWavFileWriter.writeData(audioData, 0, audioData.length);
        mAudioPlayer.play(audioData, 0, audioData.length);
    }
}
