package peterfajdiga.guituner.app;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

class InputDialog {
    private InputDialog() {}  // make class static

    // TODO: build only once
    static void show(@NonNull final Context context,
                     @NonNull final CharSequence title,
                     @NonNull final OnConfirmListener onConfirmListener,
                     @Nullable final Validator validator) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);

        final EditText editText = new EditText(context);
        dialogBuilder.setView(editText);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                onConfirmListener.onConfirm(editText.getText().toString());
            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = dialogBuilder.create();
        final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (validator != null) {
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
        dialog.show();
    }

    interface Validator {
        boolean isValid(CharSequence input);
    }

    interface OnConfirmListener {
        void onConfirm(String input);
    }
}