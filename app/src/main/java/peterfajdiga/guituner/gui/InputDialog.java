package peterfajdiga.guituner.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class InputDialog {
    private InputDialog() {}  // make class static

    // TODO: build only once
    public static void show(@NonNull final Context context,
                     @NonNull final CharSequence title,
                     @NonNull final CharSequence hint,
                     @NonNull final OnConfirmListener onConfirmListener,
                     @Nullable final Validator validator) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);

        final EditText editText = new EditText(context);
        editText.setHint(hint);
        dialogBuilder.setView(editText);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                onConfirmListener.onConfirm(editText.getText());
            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = dialogBuilder.show();

        if (validator != null) {
            final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {}

                @Override
                public void afterTextChanged(final Editable s) {
                    okButton.setEnabled(validator.isValid(s));
                }
            });
        }
    }

    public interface Validator {
        boolean isValid(CharSequence input);
    }

    public interface OnConfirmListener {
        void onConfirm(CharSequence input);
    }
}
