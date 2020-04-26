package peterfajdiga.guituner.app.tuning;

import peterfajdiga.guituner.gui.InputDialog;

public class TuningValidator implements InputDialog.Validator {
    @Override
    public boolean isValid(final CharSequence input) {
        return TonesString.validateTonesString(input.toString().toUpperCase());
    }
}
