package peterfajdiga.guituner.beep;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import androidx.annotation.NonNull;

public class Beeper {
    private static final int SAMPLE_RATE = 44100;  // in Hz
    private static final double DURATION = 2.0;  // in seconds
    private static final int BUFFER_SIZE = (int)(SAMPLE_RATE * DURATION);

    private final AudioTrack audioTrack;
    private double lastFrequency;

    public Beeper() {
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE * 2,  // in bytes
                AudioTrack.MODE_STATIC
        );
    }

    public void play(final double frequency) {
        if (frequency != lastFrequency) {
            setFrequency(frequency);
        }

        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            System.err.println("AudioTrack not initialized");
            return;
        }
        audioTrack.stop();
        audioTrack.setPlaybackHeadPosition(0);
        audioTrack.play();
    }

    private void setFrequency(final double frequency) {
        final short[] wave = generateAttenuatedSineWave(frequency);
        audioTrack.write(wave, 0, wave.length);
        lastFrequency = frequency;
    }

    private static short[] generateAttenuatedSineWave(final double frequency) {
        final double amplitude = Short.MAX_VALUE;
        final short[] buffer = new short[BUFFER_SIZE];
        for (int i = 0; i < SAMPLE_RATE; i++) {
            final double sineSample = Math.sin(2.0*Math.PI * i * frequency / SAMPLE_RATE);
            double attenuation = 1.0 - (double)i / BUFFER_SIZE;
            attenuation *= attenuation;
            buffer[i] = (short)(sineSample * attenuation * amplitude);
        }
        return buffer;
    }
}
