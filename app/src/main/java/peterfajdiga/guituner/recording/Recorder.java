package peterfajdiga.guituner.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.Arrays;

public class Recorder extends StoppableThread {

    private final ShortBufferReceiver receiver;
    private final int sampleRate;
    private int bufferSize;  // in bytes
    private final short[] buffer;

    public Recorder(final ShortBufferReceiver receiver, final int sampleRate) {
        this.receiver = receiver;
        this.sampleRate = sampleRate;

        /*bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            throw new RuntimeException("bufferSize: " + bufferSize);  // TODO: remove
//            bufferSize = sampleRate * 2;
        }
        bufferSize = General.ceilPow2(bufferSize);*/
        bufferSize = 4096;

        buffer = new short[bufferSize / 2];
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
}
