package com.spotfoxx.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.spotfoxx.R;

public class AlertDialogCreator extends DialogFragment {


    // Stop Listening if you want to broadcast Alert Dialog
    public AlertDialog createNoWifiAlert(final Context context, final Intent loginIntent) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getResources().getString(R.string.no_internet_dialog_title));
        alertDialog.setMessage(context.getResources().getString(R.string.no_internet_dialog_message));

        // back to Backster
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                context.getResources().getString(R.string.no_internet_dialog_cta),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // restart login activity
                        ((Activity) context).finish();
                        context.startActivity(loginIntent);
                        dialog.dismiss();
                    }
                });
        // cancel logic
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_stop_app),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        // close Backster
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        context.startActivity(intent);
                    }
                });

        return alertDialog;
    }

    // Ask user to install spotify
    public AlertDialog createNoSpotifyAlert(final Context context, final String appPackageName, final String referrer) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getResources().getString(R.string.no_spotify_installed_title));
        alertDialog.setMessage(context.getResources().getString(R.string.no_spotify_installed_message));

        // Go to Spotify inside Playstore
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                context.getResources().getString(R.string.no_spotify_installed_cta), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        connectToPlayStore(appPackageName, referrer, context);

                        dialog.dismiss();
                    }
                });
        // cancel logic
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_stop_app),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        // close Backster
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        context.startActivity(intent);
                    }
                });

        return alertDialog;
    }

    private void connectToPlayStore(String appPackageName, String referrer, Context context) {
        try {
            Uri uri = Uri.parse("market://details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException ignored) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
