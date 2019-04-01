package peterfajdiga.guituner.pitchdetection;

public interface PitchDetector {
    double findPitch(final short[] buffer) throws NoPitchFoundException;
    void setFocusedFrequency(double focusedFrequency);
    void removeFocusedFrequency();

    class NoPitchFoundException extends Exception {}
}
