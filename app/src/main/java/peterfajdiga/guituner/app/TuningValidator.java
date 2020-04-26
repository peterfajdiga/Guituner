package peterfajdiga.guituner.app;

import peterfajdiga.guituner.gui.InputDialog;

class TuningValidator implements InputDialog.Validator {
    @Override
    public boolean isValid(final CharSequence input) {
        return TonesString.validateTonesString(input.toString().toUpperCase());
    }
}
