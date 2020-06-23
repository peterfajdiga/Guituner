package peterfajdiga.guituner.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.Arrays;

import peterfajdiga.guituner.general.Stoppable;

public class Recorder extends Thread implements Stoppable {
    private final ShortBufferReceiver receiver;
    private final int sampleRate;
    private final int bufferSize;  // in bytes
    private final short[] buffer;
    private boolean threadEnabled = true;

    public Recorder(final ShortBufferReceiver receiver, final int sampleRate) {
        this.receiver = receiver;
        this.sampleRate = sampleRate;
        this.bufferSize = getBufferSizeForSampleRate(sampleRate);
        this.buffer = new short[bufferSize / 2];
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        final AudioRecord record = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
        );
        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new RuntimeException("Error initializing AudioRecord");
        }

        record.startRecording();
        while (threadEnabled) {
            final int sampleCount = record.read(buffer, 0, buffer.length);
            if (sampleCount > buffer.length) {
                throw new RuntimeException("Read more samples than buffer.length");
            }
            receiver.putBuffer(Arrays.copyOfRange(buffer, 0, sampleCount));
        }
        record.stop();
        record.release();
    }

    public static int getLowestSupportedSampleRate() {
        for (final int sampleRate : SAMPLE_RATES) {
            if (isSupportedSampleRate(sampleRate)) {
                return sampleRate;
            }
        }
        throw new RuntimeException("No sampling rate supported");  // TODO: handle differently
    }

    private static boolean isSupportedSampleRate(final int sampleRate) {
        final int result = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        return result != AudioRecord.ERROR_BAD_VALUE && result != AudioRecord.ERROR;
    }

    private static final int[] SAMPLE_RATES = {
            4000,
            8000,
            11025,
            16000,
            22050,
            44100,
            48000
    };

    private static int getBufferSizeForSampleRate(final int sampleRate) {
        if (sampleRate < 8000) {
            return 4096;
        }
        if (sampleRate < 16000) {
            return 8192;
        }
        if (sampleRate < 32000) {
            return 16384;
        }
        if (sampleRate < 64000) {
            return 32768;
        }
        return 65536;
    }

    @Override
    public void signalStop() {
        threadEnabled = false;
    }
}
