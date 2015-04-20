package cz.zcu.kiv.mobile.logger.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import cz.zcu.kiv.mobile.logger.R;


public class DialogUtils {
  
  private DialogUtils() {}
  

  public static void showDeleteDialog(Context context, int messageID, OnClickListener positiveListener) {
    new AlertDialog.Builder(context)
    .setTitle(R.string.dialog_delete_title)
    .setMessage(messageID)
    .setPositiveButton(R.string.dialog_delete_positive_button, positiveListener)
    .setNegativeButton(R.string.dialog_cancel_button, new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      })
    .show();
  }
}
