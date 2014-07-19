package com.artemzin.android.wail.ui.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.ui.TypefaceTextView;

public class MainDrawerFragment extends BaseFragment {

    private static final String STATE_BUNDLE_LAST_SELECTED_ITEM_POS = "STATE_BUNDLE_LAST_SELECTED_ITEM_POS";

    public interface MainDrawerListener {
        void onItemsSelected(int position);
    }

    private MainDrawerListener listener;

    private int lastSelectedItemPos = -1;

    private View[] itemsView = new View[3];
    private TypefaceTextView[] itemsTitlesTextView = new TypefaceTextView[3];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemsView[0] = view.findViewById(R.id.main_drawer_main_item);
        itemsView[1] = view.findViewById(R.id.main_drawer_tracks_item);
        itemsView[2] = view.findViewById(R.id.main_drawer_settings_item);

        for (int i = 0; i < itemsView.length; i++) {
            final int position = i;

            itemsTitlesTextView[position] = (TypefaceTextView) itemsView[position].findViewById(R.id.main_drawer_item_text_view);

            itemsView[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelected(position);
                }
            });
        }

        if (savedInstanceState != null) {
            lastSelectedItemPos = savedInstanceState.getInt(STATE_BUNDLE_LAST_SELECTED_ITEM_POS, -1);
        }

        if (lastSelectedItemPos == -1) {
            selectItem(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUNDLE_LAST_SELECTED_ITEM_POS, lastSelectedItemPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (lastSelectedItemPos != -1) {
            setItemAsSelected(lastSelectedItemPos);
        }
    }

    public void setListener(MainDrawerListener listener) {
        this.listener = listener;
    }

    private void setItemAsSelected(int position) {
        if (lastSelectedItemPos != -1) {
            itemsTitlesTextView[lastSelectedItemPos].setTypefaceFromAssets("fonts/Roboto-Light.ttf");
        }

        itemsTitlesTextView[position].setTypefaceFromAssets("fonts/Roboto-Medium.ttf");
    }

    private void onItemSelected(final int position) {
        setItemAsSelected(position);
        notifyOnItemSelected(position);
        lastSelectedItemPos = position;
    }

    private void notifyOnItemSelected(int position) {
        MainDrawerListener listenerRefCopy = listener;
        if (listenerRefCopy != null) listenerRefCopy.onItemsSelected(position);
    }

    public int getLastSelectedItemPos() {
        return lastSelectedItemPos;
    }

    public void selectItem(int position) {
        onItemSelected(position);
    }
}
