package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.LovedTracksDBHelper;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.AsyncTaskExecutor;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class TrackActionsDialog extends DialogDecorator {
    private Track track;

    public static TrackActionsDialog newInstance(Track track) {
        TrackActionsDialog dialog = new TrackActionsDialog();
        dialog.track = track;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .items(R.array.track_actions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view,
                                            int i, CharSequence charSequence) {
                        switch (i) {
                            case 0: // Love
                                loveTrack();
                                break;
                        }
                    }
                })
                .theme(Theme.DARK)
                .build();
    }

    private void loveTrack() {
        if (track != null) {
            AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... objects) {
                    LovedTracksDBHelper.getInstance(getActivity()).add(track);

                    Intent intent = new Intent(getActivity(), WAILService.class);
                    intent.setAction(WAILService.INTENT_ACTION_HANDLE_LOVED_TRACKS);
                    getActivity().startService(intent);

                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    Toast.makeText(
                            getActivity(),
                            getString(R.string.main_track_loved),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

            dismiss();
        }
    }
}
