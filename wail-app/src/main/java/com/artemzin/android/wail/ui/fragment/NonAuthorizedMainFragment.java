package com.artemzin.android.wail.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.api.lastfm.LFApiCommon;
import com.artemzin.android.wail.api.lastfm.LFApiException;
import com.artemzin.android.wail.api.lastfm.LFAuthApi;
import com.artemzin.android.wail.api.lastfm.model.response.LFSessionResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.storage.WAILSettings;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NonAuthorizedMainFragment extends BaseFragment {

    private static final String ERROR_NOT_AUTHORIZED_TOKEN = "14";
    private static final String ERROR_TOKEN_EXPIRED = "15";
    private static final String ERROR_SERVICE_OFFLINE = "11";

    private final String GA_CATEGORY = "non_authorized";

    private AlertDialog progressDialog;

    @OnClick(R.id.non_authorized_sign_in_button)
    public void onSignInButtonClick() {
        getToken();
        EasyTracker.getInstance(getActivity())
                .send(MapBuilder.createEvent(GA_CATEGORY, "signInClicked", null, 1L).build());
    }

    @OnClick(R.id.non_authorized_sign_up_button)
    public void onSignUpButtonClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.last.fm/join")));

        EasyTracker.getInstance(getActivity())
                .send(MapBuilder.createEvent(GA_CATEGORY, "signUpClicked", null, 2L).build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_non_authorized, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (WAILSettings.getKeyLastfmToken(getActivity()) != null) {
            getSessionKey();
        }
    }

    private AlertDialog getProgressDialog() {
        if (progressDialog == null) {
            View view = getActivity().getLayoutInflater()
                    .inflate(R.layout.fragment_progress_dialog, null);
            ((TextView) view.findViewById(R.id.progress_dialog_message))
                    .setText(getString(R.string.lastfm_logging_progress_dialog_message));
            progressDialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .setCancelable(false)
                    .create();
        }

        return progressDialog;
    }

    private void getToken() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            private Exception exception;

            @Override
            protected void onPreExecute() {
                getProgressDialog().show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return LFAuthApi.getToken(
                            WAILSettings.getLastfmApiKey(),
                            WAILSettings.getLastfmSecret()
                    ).getToken();
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String token) {
                getProgressDialog().dismiss();
                if (exception == null) {
                    WAILSettings.setKeyLastfmToken(getActivity(), token);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(String.format(
                            LFApiCommon.AUTHORIZATION_URL,
                            WAILSettings.getLastfmApiKey(),
                            token
                    )));

                    startActivity(intent);
                } else {
                    handleException(exception);
                }
            }
        };
        task.execute();
    }

    private void getSessionKey() {
        new AsyncTask<Void, Void, Void>() {
            private Exception exception;

            @Override
            protected void onPreExecute() {
                getProgressDialog().show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    LFSessionResponseModel sessionModel = LFAuthApi.getSession(
                            WAILSettings.getLastfmApiKey(),
                            WAILSettings.getLastfmSecret(),
                            WAILSettings.getKeyLastfmToken(getActivity())
                    );

                    WAILSettings.setLastfmSessionKey(getActivity(), sessionModel.getKey());
                    WAILSettings.setLastfmUserName(getActivity(), sessionModel.getName());
                    WAILSettings.setEnabled(getActivity(), true);
                } catch (NetworkException | LFApiException e) {
                    exception = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                getProgressDialog().dismiss();
                WAILSettings.setKeyLastfmToken(getActivity(), null);

                if (exception == null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    handleException(exception);
                }
            }
        }.execute();
    }

    private void handleException(Exception exception) {
        if (exception instanceof NetworkException) {
            Toast.makeText(
                    getActivity(),
                    getString(R.string.lastfm_logging_network_error),
                    Toast.LENGTH_LONG
            ).show();
        } else if (exception instanceof LFApiException) {
            LFApiException lfApiException = (LFApiException) exception;

            if (ERROR_TOKEN_EXPIRED.equals(lfApiException.getError())) {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.lastfm_logging_api_error_15),
                        Toast.LENGTH_LONG
                ).show();
            } else if (ERROR_NOT_AUTHORIZED_TOKEN.equals(lfApiException.getError())) {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.lastfm_logging_api_error_14),
                        Toast.LENGTH_LONG
                ).show();
            } else if (ERROR_SERVICE_OFFLINE.equals(lfApiException.getError())) {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.lastfm_api_error_11),
                        Toast.LENGTH_LONG
                ).show();
            } else {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.lastfm_logging_api_error_unknown),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
