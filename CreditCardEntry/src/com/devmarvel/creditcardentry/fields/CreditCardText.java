package com.devmarvel.creditcardentry.fields;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardUtil;
import com.devmarvel.creditcardentry.library.CardType;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;

public class CreditCardText extends CreditEntryFieldBase {

    private CardType type;

    private String mHelperText;

    public CreditCardText(Context context) {
        super(context);
        init();
    }

    public CreditCardText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressLint("RtlHardcoded")
    @Override
    void init() {
        super.init();
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

    /* TextWatcher Implementation Methods */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String number = s.toString();
        if (number.length() >= CreditCardUtil.CC_LEN_FOR_TYPE) {
            CardType type = CreditCardUtil.findCardType(number);

            if (type.equals(CardType.INVALID)) {
                delegate.onBadInput(this);
                setValid(false);
                return;
            }

            if (this.type != type) {
                delegate.onCardTypeChange(type);
            }
            this.type = type;

            String formatted = CreditCardUtil.formatForViewing(number, type);
            if (!number.equalsIgnoreCase(formatted)) {
                this.removeTextChangedListener(this);
                this.setText(formatted);
                this.setSelection(formatted.length());
                this.addTextChangedListener(this);
            }

            if (formatted.length() >= CreditCardUtil.lengthOfFormattedStringForType(type)) {
                if (CreditCardUtil.isValidNumber(formatted)) {
                    delegate.onCreditCardNumberValid();
                    setValid(true);
                } else {
                    delegate.onBadInput(this);
                    setValid(false);
                }
            }

        } else {
            if (this.type != null) {
                this.type = null;
                delegate.onCardTypeChange(CardType.INVALID);
            }
        }
    }

    public CardType getType() {
        return type;
    }

    @Override
    public void setHelperText(String helperText) {
        mHelperText = helperText;
    }

    @Override
    public String getHelperText() {
        return (mHelperText != null ? mHelperText : context.getString(R.string.CreditCardNumberHelp));
    }
}
