package peterfajdiga.guituner.app;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class CustomTuningDialog {
    private CustomTuningDialog() {}  // make class static

    static void show(@NonNull final Context context, @NonNull final OnConfirmListener onConfirmListener) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Custom tuning"); // TODO: localize

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dialogBuilder.setView(editText);

        // TODO: Localize "OK"
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                onConfirmListener.onConfirm(editText.getText().toString());
            }
        });

        // TODO: Localize "Cancel"
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
