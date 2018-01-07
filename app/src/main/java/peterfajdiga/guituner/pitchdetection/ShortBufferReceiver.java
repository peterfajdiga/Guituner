package peterfajdiga.guituner.pitchdetection;

interface ShortBufferReceiver {
    void putBuffer(short[] buffer);
    void setSampleRate(int sampleRate);
}
