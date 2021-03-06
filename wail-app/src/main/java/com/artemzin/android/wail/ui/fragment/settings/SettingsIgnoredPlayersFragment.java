package com.artemzin.android.wail.ui.fragment.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.db.IgnoredPlayersDBHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class SettingsIgnoredPlayersFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.settings_ignored_players_list_view)
    public ListView listView;

    @InjectView(R.id.settings_ignored_players_empty)
    public LinearLayout noPlayersContainer;

    private IgnoredPlayersDBHelper dbHelper;
    private PackageManager packageManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_ignored_players, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = IgnoredPlayersDBHelper.getInstance(getActivity());
        packageManager = getActivity().getPackageManager();

        ButterKnife.inject(this, view);

        List<ApplicationInfo> players = dbHelper.getAll();

        if (players.size() == 0) {
            noPlayersContainer.setVisibility(View.VISIBLE);
        } else {
            ArrayAdapter<ApplicationInfo> adapter = new ArrayAdapter<ApplicationInfo>(
                    getActivity(),
                    R.layout.settings_ignored_players_item_layout,
                    players
            ) {
                @Override
                public View getView(int position, View view, ViewGroup parent) {
                    ViewHolder holder;
                    View rowView = view;

                    if (rowView == null) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        rowView = inflater.inflate(R.layout.settings_ignored_players_item_layout, parent, false);
                        holder = new ViewHolder();
                        holder.textView = (TextView) rowView
                                .findViewById(R.id.settings_ignored_players_list_view_text);
                        holder.imageView = (ImageView) rowView
                                .findViewById(R.id.settings_ignored_players_list_view_image);
                        rowView.setTag(holder);
                    } else {
                        holder = (ViewHolder) rowView.getTag();
                    }

                    holder.textView.setText(packageManager.getApplicationLabel(getItem(position)));
                    holder.imageView.setImageDrawable(
                            packageManager.getApplicationIcon(getItem(position))
                    );

                    return rowView;
                }
            };

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    @OnItemClick(R.id.settings_ignored_players_list_view)
    public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
        final ApplicationInfo applicationInfo = (ApplicationInfo) adapterView.getAdapter().getItem(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View titleView = View.inflate(getActivity(), R.layout.dialog_fragment_title, null);
        ((TextView) titleView.findViewById(R.id.dialog_fragment_title_text_view))
                .setText(String.format(getString(R.string.settings_confirm_unignoring_player),
                        packageManager.getApplicationLabel(applicationInfo)));

        builder.setCustomTitle(titleView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.delete(applicationInfo.packageName);
                        getActivity().startService(
                                new Intent(getActivity(), WAILService.class)
                                        .setAction(WAILService.INTENT_ACTION_HANDLE_PREVIOUSLY_IGNORED_TRACK));
                        onViewCreated(getView(), null);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
