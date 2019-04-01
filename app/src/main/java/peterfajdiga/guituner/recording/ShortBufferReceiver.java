package peterfajdiga.guituner.recording;

public interface ShortBufferReceiver {
    void putBuffer(short[] buffer);
    void setSampleRate(int sampleRate);
}
