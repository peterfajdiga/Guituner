package peterfajdiga.guituner.pitch;

interface ShortBufferReceiver {
    void putBuffer(short[] buffer);
    void setSampleRate(int sampleRate);
}
