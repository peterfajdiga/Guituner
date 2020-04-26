package peterfajdiga.guituner.app;

class TuningValidator implements InputDialog.Validator {
    @Override
    public boolean isValid(final CharSequence input) {
        return TonesString.validateTonesString(input.toString().toUpperCase());
    }
}
