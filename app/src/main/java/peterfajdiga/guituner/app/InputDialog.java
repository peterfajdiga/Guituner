package peterfajdiga.guituner.app;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

class InputDialog {
    private InputDialog() {}  // make class static

    // TODO: build only once
    static void show(@NonNull final Context context, @NonNull final CharSequence title, @NonNull final OnConfirmListener onConfirmListener) {
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

        dialogBuilder.show();
    }

    interface OnConfirmListener {
        void onConfirm(String input);
    }
}
