
package com.fsck.k9.ui.messageview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fsck.k9.R;

import org.openintents.openpgp.OpenPgpSignatureResult;
import org.openintents.openpgp.util.OpenPgpUtils;

public class OpenPgpHeaderView extends LinearLayout {
    private OpenPgpSignatureResult signatureResult;
    private boolean encrypted;

    private Context mContext;

    private ImageView mResultEncryptionIcon;
    private TextView mResultEncryptionText;
    private ImageView mResultSignatureIcon;
    private TextView mResultSignatureText;
    private LinearLayout mResultSignatureLayout;
    private TextView mResultSignatureName;
    private TextView mResultSignatureEmail;
    private Button mResultSignatureButton;

//    private PendingIntent mMissingKeyPI;
//    private static final int REQUEST_CODE_DECRYPT_VERIFY = 12;

    public OpenPgpHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setOpenPgpData(OpenPgpSignatureResult signatureResult,
                               boolean encrypted) {
        this.signatureResult = signatureResult;
        this.encrypted = encrypted;

        displayOpenPgpView();
    }

    public void displayOpenPgpView() {
        mResultEncryptionIcon = (ImageView) findViewById(R.id.result_encryption_icon);
        mResultEncryptionText = (TextView) findViewById(R.id.result_encryption_text);
        mResultSignatureIcon = (ImageView) findViewById(R.id.result_signature_icon);
        mResultSignatureText = (TextView) findViewById(R.id.result_signature_text);
        mResultSignatureLayout = (LinearLayout) findViewById(R.id.result_signature_layout);
        mResultSignatureName = (TextView) findViewById(R.id.result_signature_name);
        mResultSignatureEmail = (TextView) findViewById(R.id.result_signature_email);
        mResultSignatureButton = (Button) findViewById(R.id.result_signature_button);

//        mGetKeyButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getMissingKey();
//            }
//        });

//    public void setFragment(Fragment fragment) {
//        mFragment = (MessageViewFragment) fragment;
//    }


        if (encrypted) {
            setStatusImage(mContext, mResultEncryptionIcon, mResultEncryptionText, STATE_ENCRYPTED);
            mResultEncryptionText.setText(R.string.openpgp_result_encrypted);
        } else {
            setStatusImage(mContext, mResultEncryptionIcon, mResultEncryptionText, STATE_NOT_ENCRYPTED);
            mResultEncryptionText.setText(R.string.openpgp_result_not_encrypted);
        }

        if (signatureResult == null) {
            setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_NOT_SIGNED);
            mResultSignatureText.setText(R.string.openpgp_result_no_signature);
            mResultSignatureLayout.setVisibility(View.GONE);
        } else {
            switch (signatureResult.getStatus()) {
                case OpenPgpSignatureResult.SIGNATURE_ERROR: {
                    setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_INVALID);
                    mResultSignatureText.setText(R.string.openpgp_result_invalid_signature);

                    mResultSignatureButton.setVisibility(View.GONE);
                    mResultSignatureLayout.setVisibility(View.GONE);
                    break;
                }
                case OpenPgpSignatureResult.SIGNATURE_SUCCESS_CERTIFIED: {
                    setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_VERIFIED);
                    mResultSignatureText.setText(R.string.openpgp_result_signature_certified);

                    setUserId(signatureResult);
                    mResultSignatureButton.setVisibility(View.VISIBLE);
                    mResultSignatureLayout.setVisibility(View.VISIBLE);

                    break;
                }
                case OpenPgpSignatureResult.SIGNATURE_KEY_MISSING: {
                    setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_UNKNOWN_KEY);
                    mResultSignatureText.setText(R.string.openpgp_result_signature_missing_key);

                    setUserId(signatureResult);
                    mResultSignatureButton.setVisibility(View.VISIBLE);
                    mResultSignatureLayout.setVisibility(View.VISIBLE);

                    break;
                }
                case OpenPgpSignatureResult.SIGNATURE_SUCCESS_UNCERTIFIED: {
                    setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_UNVERIFIED);
                    mResultSignatureText.setText(R.string.openpgp_result_signature_uncertified);

                    setUserId(signatureResult);
                    mResultSignatureButton.setVisibility(View.VISIBLE);
                    mResultSignatureLayout.setVisibility(View.VISIBLE);

                    break;
                }
                case OpenPgpSignatureResult.SIGNATURE_KEY_EXPIRED: {
                    setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_EXPIRED);
                    mResultSignatureText.setText(R.string.openpgp_result_signature_expired_key);

                    setUserId(signatureResult);
                    mResultSignatureButton.setVisibility(View.VISIBLE);
                    mResultSignatureLayout.setVisibility(View.VISIBLE);

                    break;
                }
                case OpenPgpSignatureResult.SIGNATURE_KEY_REVOKED: {
                    setStatusImage(mContext, mResultSignatureIcon, mResultSignatureText, STATE_REVOKED);
                    mResultSignatureText.setText(R.string.openpgp_result_signature_revoked_key);

                    setUserId(signatureResult);
                    mResultSignatureButton.setVisibility(View.VISIBLE);
                    mResultSignatureLayout.setVisibility(View.VISIBLE);

                    break;
                }

                default:
                    break;
            }

        }
    }

    private void setUserId(OpenPgpSignatureResult signatureResult) {
        String[] splitUserId = OpenPgpUtils.splitUserId(signatureResult.getPrimaryUserId());
        if (splitUserId[0] != null) {
            mResultSignatureName.setText(splitUserId[0]);
        } else {
            mResultSignatureName.setText("no name");
        }
        if (splitUserId[1] != null) {
            mResultSignatureEmail.setText(splitUserId[1]);
        } else {
            mResultSignatureEmail.setText("no email");
        }
    }

    public static final int STATE_REVOKED = 1;
    public static final int STATE_EXPIRED = 2;
    public static final int STATE_VERIFIED = 3;
    public static final int STATE_UNAVAILABLE = 4;
    public static final int STATE_ENCRYPTED = 5;
    public static final int STATE_NOT_ENCRYPTED = 6;
    public static final int STATE_UNVERIFIED = 7;
    public static final int STATE_UNKNOWN_KEY = 8;
    public static final int STATE_INVALID = 9;
    public static final int STATE_NOT_SIGNED = 10;

    public static void setStatusImage(Context context, ImageView statusIcon, int state) {
        setStatusImage(context, statusIcon, null, state);
    }

    /**
     * Sets status image based on constant
     */
    public static void setStatusImage(Context context, ImageView statusIcon, TextView statusText,
                                      int state) {
        switch (state) {
            /** GREEN: everything is good **/
            case STATE_VERIFIED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_verified_cutout));
                int color = R.color.openpgp_green;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            case STATE_ENCRYPTED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_lock_closed));
                int color = R.color.openpgp_green;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            /** ORANGE: mostly bad... **/
            case STATE_UNVERIFIED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_unverified_cutout));
                int color = R.color.openpgp_orange;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            case STATE_UNKNOWN_KEY: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_unknown_cutout));
                int color = R.color.openpgp_orange;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            /** RED: really bad... **/
            case STATE_REVOKED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_revoked_cutout));
                int color = R.color.openpgp_red;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            case STATE_EXPIRED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_expired_cutout));
                int color = R.color.openpgp_red;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            case STATE_NOT_ENCRYPTED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_lock_open));
                int color = R.color.openpgp_red;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            case STATE_NOT_SIGNED: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_unknown_cutout));
                int color = R.color.openpgp_red;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
            case STATE_INVALID: {
                statusIcon.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.status_signature_invalid_cutout));
                int color = R.color.openpgp_red;
                statusIcon.setColorFilter(context.getResources().getColor(color),
                        PorterDuff.Mode.SRC_IN);
                if (statusText != null) {
                    statusText.setTextColor(context.getResources().getColor(color));
                }
                break;
            }
        }
    }

//    private void getMissingKey() {
//        try {
//            mFragment.getActivity().startIntentSenderForResult(
//                    mMissingKeyPI.getIntentSender(),
//                    REQUEST_CODE_DECRYPT_VERIFY, null, 0, 0, 0);
//        } catch (SendIntentException e) {
//            Log.e(K9.LOG_TAG, "SendIntentException", e);
//        }
//    }

}
