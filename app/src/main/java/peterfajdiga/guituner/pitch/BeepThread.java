package peterfajdiga.guituner.pitch;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class BeepThread extends Thread {

    private static final int SAMPLE_RATE = 44100;

    private final double frequency;
    private final int n_samples;

    private AudioTrack audioTrack;

    // frequency in Hz
    // duration in s
    public BeepThread(final double frequency, final double duration) {
        this.frequency = frequency;
        this.n_samples = (int)(SAMPLE_RATE * duration);
    }

    @Override
    public void run() {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize, AudioTrack.MODE_STREAM);

        // Sine wave
        short[] buffer = new short[n_samples];
        for (int i = 0; i < n_samples; i++) {
            double amplitude = 1.0 - (double)i / n_samples;
            amplitude *= amplitude;
            buffer[i] = (short)(Math.sin(2.0*Math.PI * i * frequency / SAMPLE_RATE) * amplitude * Short.MAX_VALUE);
        }

        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        audioTrack.play();
        audioTrack.write(buffer, 0, n_samples);

        audioTrack.stop();
        audioTrack.release();
    }
}
