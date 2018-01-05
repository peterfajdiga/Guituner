package peterfajdiga.guituner.fourier;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.Arrays;

public class Recorder extends StoppableThread {

    private static final int SAMPLE_RATE = 6400;
    private final ShortBufferReceiver receiver;
    private int bufferSize;  // in bytes
    private final short[] buffer;

    public Recorder(final ShortBufferReceiver receiver) {
        this.receiver = receiver;
        receiver.setSampleRate(SAMPLE_RATE);

        bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            throw new RuntimeException("bufferSize: " + bufferSize);  // TODO: remove
//            bufferSize = SAMPLE_RATE * 2;
        }

        buffer = new short[bufferSize / 2];
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        final AudioRecord record = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
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
}
