package peterfajdiga.guituner.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import peterfajdiga.guituner.general.General;

public class BeepThread extends Thread {

    private static final int SAMPLE_RATE = 44100;
    private static final double BASE_AMPLITUDE = Short.MAX_VALUE * 0.2;
    private static final double MAX_AMPLITUDE  = Short.MAX_VALUE * 0.9;

    private final double frequency;
    private final int n_samples;

    /**
     * @param frequency in Hz
     * @param duration  in s
     */
    public BeepThread(final double frequency, final double duration) {
        this.frequency = frequency;
        this.n_samples = (int)(SAMPLE_RATE * duration);
    }

    @Override
    public void run() {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize, AudioTrack.MODE_STREAM);

        double amplitude = BASE_AMPLITUDE / dbaGain(frequency);
        if (amplitude > MAX_AMPLITUDE) {
            amplitude = MAX_AMPLITUDE;
        }

        // Sine wave
        short[] buffer = new short[n_samples];
        for (int i = 0; i < n_samples; i++) {
            double sustain = 1.0 - (double)i / n_samples;
            sustain *= sustain;
            buffer[i] = (short)(Math.sin(2.0*Math.PI * i * frequency / SAMPLE_RATE) * sustain * amplitude);
        }

        if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audioTrack.setVolume(AudioTrack.getMaxVolume());
            audioTrack.play();
            audioTrack.write(buffer, 0, n_samples);

            audioTrack.stop();
            audioTrack.release();
        } else {
            System.err.println("AudioTrack not initialized");
        }
    }

    private static final double DBA_N1 = 12200.0 * 12200.0;
    private static final double DBA_N2 = 20.6 * 20.6;
    private static final double DBA_N3 = 107.7 * 107.7;
    private static final double DBA_N4 = 737.9 * 737.9;
    /**
     * @param frequency frequency for which to calculate gain
     * @return          A-weighted gain. Converting this value to decibels would give us dB(A)
     */
    private double dbaGain(final double frequency) {
        final double freqSq = frequency * frequency;
        return DBA_N1 * freqSq * freqSq /
                ((freqSq+DBA_N2) * (freqSq+DBA_N1) * Math.sqrt(freqSq+DBA_N3) * Math.sqrt(freqSq+DBA_N4) * 0.79434639580229505);
    }
}
