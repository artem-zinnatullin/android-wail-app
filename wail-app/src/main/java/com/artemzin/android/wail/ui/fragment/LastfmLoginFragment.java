package com.artemzin.android.wail.ui.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.artemzin.android.bytes.common.StringUtil;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.api.lastfm.LFApiException;
import com.artemzin.android.wail.api.lastfm.LFAuthApi;
import com.artemzin.android.wail.api.lastfm.model.response.LFSessionResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.PlayersDBHelper;
import com.artemzin.android.wail.storage.db.TracksDBHelper;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.ThreadUtil;
import com.artemzin.android.wail.util.validation.TextValidator;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LastfmLoginFragment extends BaseFragment {

    private final String GA_EVENT_LOGIN_LASTFM = "LastfmLogin";

    @InjectView(R.id.lastfm_login_login_edit_text)
    public EditText loginEditText;

    @InjectView(R.id.lastfm_login_password_edit_text)
    public EditText passwordEditText;

    @InjectView(R.id.lastfm_login_login_button)
    public Button loginButton;

    private TextValidator loginValidator, passwordValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lastfm_login, container, false);
    }

    @OnClick(R.id.lastfm_login_login_button)
    public void onLoginButtonClick(View v) {

        final String userName = loginEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        final ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(
                getString(R.string.lastfm_logging_progress_dialog_message)
        );

        progressDialogFragment.setCancelable(false);
        progressDialogFragment.show(getFragmentManager(), "loggingProgressDialog");

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {

            NetworkException networkException;
            LFApiException lfApiException;
            LFSessionResponseModel sessionModel;

            @Override
            protected Void doInBackground(Void... params) {
                final long requestStartTime = SystemClock.elapsedRealtime();

                EasyTracker.getInstance(getActivity()).send(
                        MapBuilder.createEvent(GA_EVENT_LOGIN_LASTFM,
                                "started login",
                                null,
                                1L)
                                .build()
                );

                try {
                    sessionModel = LFAuthApi.getMobileSession(
                            WAILSettings.getLastfmApiKey(),
                            WAILSettings.getLastfmSecret(),
                            userName,
                            password
                    );

                    // if new logged user is not same to previous we should clear all prev user's dat
                    final String prevLastfmUserName = WAILSettings.getLastfmUserName(getActivity());

                    if (!StringUtil.isNullOrEmpty(prevLastfmUserName) && !prevLastfmUserName.equals(userName)) {
                        WAILSettings.setEnabled(getActivity(), false);

                        PlayersDBHelper.getInstance(getActivity()).removeAll();
                        TracksDBHelper.getInstance(getActivity()).deleteAll();
                    }

                } catch (NetworkException e) {
                    networkException = e;
                    EasyTracker.getInstance(getActivity()).send(
                            MapBuilder.createEvent(GA_EVENT_LOGIN_LASTFM,
                                    "failed with NetworkException: " + e.getMessage(),
                                    null,
                                    0L)
                                    .build()
                    );
                } catch (LFApiException e) {
                    lfApiException = e;
                    EasyTracker.getInstance(getActivity()).send(
                            MapBuilder.createEvent(GA_EVENT_LOGIN_LASTFM,
                                    "failed with LFApiException: " + e.getMessage(),
                                    null,
                                    0L)
                                    .build()
                    );
                }

                ThreadUtil.sleepIfRequired(requestStartTime, 2000);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialogFragment.dismiss();

                if (networkException != null) {
                    Toast.makeText(getActivity(), getString(R.string.lastfm_logging_network_error), Toast.LENGTH_LONG).show();
                } else if (lfApiException != null) {
                    if ("4".equals(lfApiException.getError())) {
                        Toast.makeText(getActivity(), getString(R.string.lastfm_logging_api_error_4), Toast.LENGTH_LONG).show();
                    } else if ("11".equals(lfApiException.getError())) {
                        Toast.makeText(getActivity(), getString(R.string.lastfm_api_error_11), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.lastfm_logging_api_error_unknown), Toast.LENGTH_LONG).show();
                    }
                } else {

                    if (sessionModel != null && sessionModel.getKey() != null) {
                        WAILSettings.setLastfmSessionKey(getActivity(), sessionModel.getKey());
                        WAILSettings.setLastfmUserName(getActivity(), userName);
                        WAILSettings.setEnabled(getActivity(), true);
                        getActivity().setResult(Activity.RESULT_OK);

                        EasyTracker.getInstance(getActivity()).send(
                                MapBuilder.createEvent(GA_EVENT_LOGIN_LASTFM,
                                        "login success",
                                        null,
                                        1L)
                                        .build()
                        );

                        getActivity().finish();
                    }
                }
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        loginButton.setEnabled(false);

        loginValidator = new TextValidator(loginEditText) {

            {
                addOnValidationChangedListener(new OnValidationChangedListener() {
                    @Override
                    public void onValidationChanged(boolean isValid, String text, String validationError) {
                        changeLoginButtonState();
                    }
                });
            }

            @Override
            protected String validate(String text) {
                if (StringUtil.isNullOrEmpty(text)) {
                    return getString(R.string.lastfm_login_empty);
                }

                return null;
            }
        };

        passwordValidator = new TextValidator(passwordEditText) {

            {
                addOnValidationChangedListener(new OnValidationChangedListener() {
                    @Override
                    public void onValidationChanged(boolean isValid, String text, String validationError) {
                        changeLoginButtonState();
                    }
                });
            }

            @Override
            protected String validate(String text) {
                if (StringUtil.isNullOrEmpty(text)) {
                    return getString(R.string.lastfm_password_empty);
                }

                return null;
            }
        };
    }

    private void changeLoginButtonState() {
        loginButton.setEnabled(
                loginValidator.isValid() != null && loginValidator.isValid()
                        && passwordValidator.isValid() != null && passwordValidator.isValid()
        );
    }
}
