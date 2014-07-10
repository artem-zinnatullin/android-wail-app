package com.artemzin.android.wail.util.validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.artemzin.android.bytes.common.StringUtil;

import java.util.LinkedList;
import java.util.List;

public abstract class TextValidator implements TextWatcher, View.OnFocusChangeListener {

    private final TextView textView;
    private boolean validateAfterTextChanged, validateOnFocusChange;

    private Boolean isValid;

    private final List<OnValidationChangedListener> onValidationChangedListeners = new LinkedList<OnValidationChangedListener>();

    public TextValidator(TextView textView) {
        this(textView, true, true);
    }

    public TextValidator(TextView textView, boolean validateAfterTextChanged, boolean validateOnFocusChange) {
        this.textView = textView;
        textView.addTextChangedListener(this);
        this.validateAfterTextChanged = validateAfterTextChanged;
        this.validateOnFocusChange    = validateOnFocusChange;

        if (validateOnFocusChange) {
            textView.setOnFocusChangeListener(this);
        }
    }

    public TextView getTextView() {
        return textView;
    }

    protected boolean isValidateAfterTextChanged() {
        return validateAfterTextChanged;
    }

    protected boolean isValidateOnFocusChange() {
        return validateOnFocusChange;
    }

    public Boolean isValid() {
        return isValid;
    }

    private void setIsValid(String validationError, boolean show) {
        isValid = isNullOrEmpty(validationError);
        if (show) {
            textView.setError(validationError);
        } else {
            textView.setError(null);
        }
    }

    protected abstract String validate(String text);

    /**
     * Force call validation for the TextView, OnValidationChangedListeners will be notified with the result of validation
     * @param showError if true and validation fails then the error message is displayed
     * @return true if validation succeed, false otherwise
     */
    public final boolean validate(boolean showError) {
        setIsValid(validate(textView.getText().toString()), showError);

        return isValid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (validateAfterTextChanged) {
            final String text = textView.getText().toString();
            final String validationError = validate(text);
            setIsValid(validationError, false);

            notifyOnValidationChangedListeners(isValid, text, validationError);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (validateOnFocusChange && !hasFocus) {
            final String text = textView.getText().toString();
            final String validationError = validate(text);
            setIsValid(validationError, true);

            notifyOnValidationChangedListeners(isValid, text, validationError);
        }
    }

    protected static boolean isNullOrEmpty(String string) {
        return StringUtil.isNullOrEmpty(string);
    }

    public final void addOnValidationChangedListener(OnValidationChangedListener listener) {
        onValidationChangedListeners.add(listener);
    }

    public final boolean removeOnValidationChangedListener(OnValidationChangedListener listener) {
        return onValidationChangedListeners.remove(listener);
    }

    private final void notifyOnValidationChangedListeners(boolean isValid, String textValue, String validationErrorText) {
        for (OnValidationChangedListener listener : onValidationChangedListeners) {
            if (listener != null) listener.onValidationChanged(isValid, textValue, validationErrorText);
        }
    }

    public interface OnValidationChangedListener {
        void onValidationChanged(boolean isValid, String text, String validationError);
    }
}
