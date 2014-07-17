package com.artemzin.android.wail.ui.fragment.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artemzin.android.bytes.common.StringUtil;
import com.artemzin.android.bytes.ui.ViewUtil;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.db.TracksDBHelper;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TracksListFragment extends BaseFragment {

    private final String GA_EVENT_TRACKS_LIST_FRAGMENT = "TracksListFragment";

    private View tracksListLoading, tracksListEmpty, tracksListContainer;
    private ListView tracksListView;
    private final TracksListDataProvider tracksListDataProvider = new TracksListDataProvider();
    private TracksListAdapter tracksListAdapter;
    private TracksSearchHandler tracksSearchHandler = new TracksSearchHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setTitle(getString(R.string.tracks_actionbar_title));
        tracksListAdapter = new TracksListAdapter(tracksListDataProvider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracks_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tracksListLoading = view.findViewById(R.id.tracks_list_loading);
        tracksListEmpty = view.findViewById(R.id.track_list_empty);
        tracksListContainer = view.findViewById(R.id.tracks_list_container);

        setUIStateLoading();

        tracksListView = (ListView) view.findViewById(R.id.tracks_list_view);
        tracksListView.setAdapter(tracksListAdapter);
    }

    private void setUIStateLoading() {
        ViewUtil.setVisibility(tracksListLoading, true);
        ViewUtil.setVisibility(tracksListEmpty, false);
        ViewUtil.setVisibility(tracksListContainer, false);
    }

    private void setUIStateEmpty(String emptyText) {
        TextView emptyTextView = (TextView) tracksListEmpty.findViewById(R.id.tracks_list_empty_text_view);
        emptyTextView.setText(emptyText);

        ViewUtil.setVisibility(tracksListLoading, false);
        ViewUtil.setVisibility(tracksListEmpty, true);
        ViewUtil.setVisibility(tracksListContainer, false);
    }

    private void setUIStateShowTracks() {
        ViewUtil.setVisibility(tracksListLoading, false);
        ViewUtil.setVisibility(tracksListEmpty, false);
        ViewUtil.setVisibility(tracksListContainer, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_TRACKS_LIST_FRAGMENT, "started", null, 1L).build());
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeForDBUpdates();
        reloadTracksAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        unsubscribeFromDBUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_TRACKS_LIST_FRAGMENT, "stopped", null, 0L).build());
    }

    private void subscribeForDBUpdates() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(tracksChangedBroadcastReceiver, new IntentFilter(TracksDBHelper.INTENT_TRACKS_CHANGED));
    }

    private void unsubscribeFromDBUpdates() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(tracksChangedBroadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_tracks, menu);

        tracksSearchHandler.setSearchItem(menu.findItem(R.id.main_tracks_ab_search));
    }

    private void reloadTracksAsync() {
        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                try {
                    setUIStateLoading();
                } catch (Exception e) {
                    Loggi.e("TracksListFragment.reloadTracksAsync().onPreExecute()" + e);
                }
            }

            @Override
            protected Cursor doInBackground(Void... params) {
                try {
                    return TracksDBHelper.getInstance(getActivity()).getAllDesc();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Cursor tracksCursor) {
                super.onPostExecute(tracksCursor);

                if (!isDetached()) {
                    try {
                        if (tracksCursor == null || tracksCursor.getCount() == 0) {
                            setUIStateEmpty(getString(R.string.tracks_list_empty_motivation_text));
                        } else {
                            setUIStateShowTracks();
                        }

                        tracksListDataProvider.setDataSource(tracksCursor);
                    } catch (Exception e) {
                        Loggi.e("TracksListFragment reloadTracksAsync() exception: " + e.getMessage());
                    }
                }
            }
        });
    }

    private static class TracksListDataProvider {
        interface Listener {
            void onDataSourceChanged();
        }

        private Cursor tracksCursor;
        private List<Track> tracksList;
        private Listener listener;

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        public void setDataSource(Cursor tracksCursor) {
            if (this.tracksCursor != null) {
                this.tracksCursor.close();
            }

            this.tracksCursor = tracksCursor;
            tracksList = null;

            notifyOnDataSourceChanged();
        }

        public void setDataSource(List<Track> tracksList) {
            if (this.tracksCursor != null) {
                this.tracksCursor.close();
            }

            this.tracksList = tracksList;
            tracksCursor = null;

            notifyOnDataSourceChanged();
        }

        public int getCount() {
            if (tracksCursor != null) {
                return tracksCursor.getCount();
            } else if (tracksList != null) {
                return tracksList.size();
            } else {
                return 0;
            }
        }

        public Track getAtPos(int pos) {
            if (tracksCursor != null) {
                tracksCursor.moveToPosition(pos);
                return TracksDBHelper.parseFromCursor(tracksCursor);
            } else if (tracksList != null) {
                return tracksList.get(pos);
            }

            return null;
        }

        private void notifyOnDataSourceChanged() {
            if (listener != null) {
                listener.onDataSourceChanged();
            }
        }
    }

    private class TracksListAdapter extends BaseAdapter implements TracksListDataProvider.Listener {

        private final int[] mTrackStateColors;
        private TracksListDataProvider mTracksListDataProvider;

        private final DateFormat mDateFormatWithYear  = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());

        public TracksListAdapter(TracksListDataProvider tracksListDataProvider) {
            this.mTracksListDataProvider = tracksListDataProvider;
            this.mTracksListDataProvider.setListener(this);
            mTrackStateColors = loadTrackStateColors();
        }

        private int[] loadTrackStateColors() {
            int[] colors = new int[5];

            colors[0] = getResources().getColor(R.color.dark_theme_track_state_waiting_for_scrobble);
            colors[1] = getResources().getColor(R.color.dark_theme_track_state_scrobbling);
            colors[2] = getResources().getColor(R.color.dark_theme_track_state_error);
            colors[3] = getResources().getColor(R.color.dark_theme_track_state_scrobble_success);
            colors[4] = getResources().getColor(R.color.dark_theme_track_state_unknown);

            return colors;
        }

        @Override
        public int getCount() {
            return mTracksListDataProvider.getCount();
        }

        @Override
        public Track getItem(int position) {
            return mTracksListDataProvider.getAtPos(position);
        }

        @Override
        public long getItemId(int position) {
            return mTracksListDataProvider.getAtPos(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TrackViewHolder trackViewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.track_list_item, parent, false);
                trackViewHolder = new TrackViewHolder(convertView);
                convertView.setTag(trackViewHolder);
            } else {
                trackViewHolder = (TrackViewHolder) convertView.getTag();
            }

            drawTrackView(convertView, trackViewHolder, position);

            return convertView;
        }

        private void drawTrackView(View convertView, TrackViewHolder trackViewHolder, int position) {
            final Track track = mTracksListDataProvider.getAtPos(position);

            trackViewHolder.trackTextView.setText(track.getTrack());

            String artistAndAlbum;

            if (!StringUtil.isNullOrEmpty(track.getArtist())) {
                artistAndAlbum = track.getArtist();

                if (!StringUtil.isNullOrEmpty(track.getAlbum())) {
                    artistAndAlbum += " — " + track.getAlbum();
                }
            } else if (!StringUtil.isNullOrEmpty(track.getAlbum())) {
                artistAndAlbum = track.getAlbum();
            } else {
                artistAndAlbum = getString(R.string.track_artist_and_album_no_data);
            }

            trackViewHolder.artistAndAlbumTextView.setText(artistAndAlbum);

            switch (track.getState()) {
                case Track.STATE_WAITING_FOR_SCROBBLE:
                    trackViewHolder.statusTextView.setText(R.string.track_status_waiting_for_scrobble);
                    trackViewHolder.statusTextView.setTextColor(mTrackStateColors[0]);
                    break;
                case Track.STATE_SCROBBLING:
                    trackViewHolder.statusTextView.setText(R.string.track_status_scrobbling);
                    trackViewHolder.statusTextView.setTextColor(mTrackStateColors[1]);
                    break;
                case Track.STATE_SCROBBLE_ERROR:
                    trackViewHolder.statusTextView.setText(R.string.track_status_scrobble_error);
                    trackViewHolder.statusTextView.setTextColor(mTrackStateColors[2]);
                    break;
                case Track.STATE_SCROBBLE_SUCCESS:
                    trackViewHolder.statusTextView.setText(R.string.track_status_scrobble_success);
                    trackViewHolder.statusTextView.setTextColor(mTrackStateColors[3]);
                    break;

                default:
                    trackViewHolder.statusTextView.setText(R.string.track_status_unknown);
                    trackViewHolder.statusTextView.setTextColor(mTrackStateColors[4]);
                    break;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(track.getTimestamp());

            final String dateText = mDateFormatWithYear.format(calendar.getTime());

            trackViewHolder.dateTextView.setText(dateText);
        }

        @Override
        public void onDataSourceChanged() {
            notifyDataSetChanged();
        }
    }

    private static class TrackViewHolder {
        public final TextView trackTextView;
        public final TextView artistAndAlbumTextView;
        public final TextView statusTextView;
        public final TextView dateTextView;

        public TrackViewHolder(View convertView) {
            trackTextView          = (TextView) convertView.findViewById(R.id.track_list_item_track);
            artistAndAlbumTextView = (TextView) convertView.findViewById(R.id.track_list_item_artist_and_album);
            statusTextView         = (TextView) convertView.findViewById(R.id.track_list_item_status);
            dateTextView           = (TextView) convertView.findViewById(R.id.track_list_item_date);
        }
    }

    private final BroadcastReceiver tracksChangedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadTracksAsync();
        }
    };

    private class TracksSearchHandler implements TextWatcher, MenuItem.OnActionExpandListener, View.OnFocusChangeListener {

        private EditText searchEditText;
        private String lastSearchText;

        public void setSearchItem(MenuItem menuItem) {
            menuItem.setOnActionExpandListener(this);

            searchEditText = ((EditText) menuItem.getActionView().findViewById(R.id.ab_main_tracks_search_edit_text));
            searchEditText.addTextChangedListener(this);
            searchEditText.setOnFocusChangeListener(this);

            (menuItem.getActionView().findViewById(R.id.ab_main_tracks_search_clear_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchEditText.setText("");
                }
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            final String text = s.toString();
            if (lastSearchText != null && lastSearchText.equals(text)) return;

            if (text.isEmpty()) {
                reloadTracksAsync();
                subscribeForDBUpdates();
            } else {
                searchAsync(text);
            }

            lastSearchText = text;
        }

        public void searchAsync(String text) {
            AsyncTaskExecutor.executeConcurrently(new AsyncTask<String, Void, List<Track>>() {

                private String text;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    try {
                        unsubscribeFromDBUpdates();
                        setUIStateLoading();
                    } catch (Exception e) {
                        Loggi.e("TracksListFragment.TracksSearchHandler.searchAsync().onPreExecute() " + e);
                    }
                }

                @Override
                protected List<Track> doInBackground(String... params) {
                    try {
                        text = params[0].toLowerCase(Locale.getDefault());

                        Cursor cursor = TracksDBHelper.getInstance(getActivity()).getAllDesc();

                        final List<Track> tracks = new ArrayList<Track>();

                        if (cursor.moveToFirst()) {
                            do {
                                Track track = TracksDBHelper.parseFromCursor(cursor);

                                if (track.getTrack() != null && track.getTrack().toLowerCase(Locale.getDefault()).contains(text)) {
                                    tracks.add(track);
                                } else if (track.getArtist() != null && track.getArtist().toLowerCase(Locale.getDefault()).contains(text)) {
                                    tracks.add(track);
                                } else if (track.getAlbum() != null && track.getAlbum().toLowerCase(Locale.getDefault()).contains(text)) {
                                    tracks.add(track);
                                }
                            } while (cursor.moveToNext());
                        }

                        return tracks;
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<Track> tracks) {
                    super.onPostExecute(tracks);
                    try {
                        if (tracks == null) {
                            Toast.makeText(getActivity(), R.string.tracks_search_error_toast, Toast.LENGTH_LONG).show();
                        } else if (tracks.size() == 0) {
                            setUIStateEmpty(getString(R.string.tracks_search_no_results, text));
                        } else {
                            setUIStateShowTracks();
                            tracksListDataProvider.setDataSource(tracks);
                            tracksListView.smoothScrollToPosition(0);
                        }
                    } catch (Exception e) {
                        Loggi.e("TracksListFragment.TracksSearchHandler.onPostExecute() " + e);
                    }
                }
            }, text);
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            try {
                searchEditText.requestFocus();
            } catch (Exception e) {
            }
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            searchEditText.setText("");
            return true;
        }

        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            searchEditText.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (hasFocus) {
                            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    } catch (Exception e) {
                        Loggi.e("TracksListFragment.TracksSearchHandler.onFocusChange() " + e);
                    }
                }
            });
        }
    }
}
