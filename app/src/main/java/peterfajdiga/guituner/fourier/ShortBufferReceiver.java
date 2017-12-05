package peterfajdiga.guituner.fourier;

interface ShortBufferReceiver {
    void putBuffer(short[] buffer);
    void setSampleRate(int sampleRate);
}
