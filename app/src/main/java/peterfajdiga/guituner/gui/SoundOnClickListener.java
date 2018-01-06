package peterfajdiga.guituner.gui;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.View;

public class SoundOnClickListener implements View.OnClickListener {

    private BeepThread beepThread;
    private double frequency = 440.0;

    @Override
    public void onClick(View view) {
        if (beepThread == null || !beepThread.isAlive()) {
            beepThread = new BeepThread(frequency);
            beepThread.start();
        }
    }

    public void setFrequency(final double frequency) {
        this.frequency = frequency;
    }



    private static class BeepThread extends Thread {

        private static final int SAMPLE_RATE = 44100;
        private static final int N_SAMPLES = SAMPLE_RATE * 2;  // 2s

        private final double frequency;

        BeepThread(final double frequency) {
            this.frequency = frequency;
        }

        @Override
        public void run() {
            int mBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);

            AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    mBufferSize, AudioTrack.MODE_STREAM);

            // Sine wave
            short[] mBuffer = new short[N_SAMPLES];
            for (int i = 0; i < N_SAMPLES; i++) {
                double amplitude = 1.0 - (double)i / N_SAMPLES;
                amplitude *= amplitude;
                mBuffer[i] = (short)(Math.sin(2.0*Math.PI * i * frequency / SAMPLE_RATE) * amplitude * Short.MAX_VALUE);
            }

            mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
            mAudioTrack.play();

            mAudioTrack.write(mBuffer, 0, N_SAMPLES);
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }
}
