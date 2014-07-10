package com.artemzin.android.wail.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.ui.activity.LastfmLoginActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class NonAuthorizedMainFragment extends BaseFragment {

    public static final int REQUEST_CODE_LASTFM_LOGIN_ACTIVITY_INTENT = 2;

    private final String GA_CATEGORY = "non_authorized";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_non_authorized, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.non_authorized_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(
                        new Intent(getActivity(), LastfmLoginActivity.class),
                        REQUEST_CODE_LASTFM_LOGIN_ACTIVITY_INTENT
                );

                EasyTracker.getInstance(getActivity())
                        .send(MapBuilder.createEvent(GA_CATEGORY, "signInClicked", null, 1L).build());
            }
        });

        view.findViewById(R.id.non_authorized_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.last.fm/join")));

                EasyTracker.getInstance(getActivity())
                        .send(MapBuilder.createEvent(GA_CATEGORY, "signUpClicked", null, 2L).build());
            }
        });
    }
}
