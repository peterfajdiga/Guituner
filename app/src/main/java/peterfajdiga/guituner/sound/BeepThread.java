package peterfajdiga.guituner.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import peterfajdiga.guituner.general.General;

public class BeepThread extends Thread {

    private static final int SAMPLE_RATE = 44100;
    private static final double BASE_AMPLITUDE = Short.MAX_VALUE / 10.0f;

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

        double amplitude = BASE_AMPLITUDE / dbaGain(frequency);
        if (amplitude > Short.MAX_VALUE) {
            amplitude = Short.MAX_VALUE;
        }

        // Sine wave
        short[] buffer = new short[n_samples];
        for (int i = 0; i < n_samples; i++) {
            double sustain = 1.0 - (double)i / n_samples;
            sustain *= sustain;
            buffer[i] = (short)(Math.sin(2.0*Math.PI * i * frequency / SAMPLE_RATE) * sustain * amplitude);
        }

        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        audioTrack.play();
        audioTrack.write(buffer, 0, n_samples);

        audioTrack.stop();
        audioTrack.release();
    }

    private static final int DBA_N1 = 12200 * 12200;
    private static final double DBA_N2 = 20.6 * 20.6;
    private static final double DBA_N3 = 107.7 * 107.7;
    private static final double DBA_N4 = 737.9 * 737.9;
    private double dbaGain(final double frequency) {
        final double freqSq = frequency * frequency;
        return DBA_N1 * freqSq * freqSq /
                ((freqSq+DBA_N2) * (freqSq+DBA_N1) * Math.sqrt(freqSq+DBA_N3) * Math.sqrt(freqSq+DBA_N4)) /
                0.79434639580229505;
    }
    private double dba(final double frequency) {
        return General.db(dbaGain(frequency));
    }
}