/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename CartOrderFragment.java
 */

package com.camerarental.crc.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.camerarental.crc.CartActivity;
import com.camerarental.crc.R;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnSubmitListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Config;
import com.camerarental.crc.utils.Constant;
import com.camerarental.crc.utils.mail.GMailSender;

import java.util.HashMap;

public class CartOrderFragment extends Fragment implements OnSubmitListener {

    private static final String TAG = CartOrderFragment.class.getSimpleName();

    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPhone;
    private EditText mEditPassportNo;
    private EditText mEditComments;

    private CartActivity mActivity;

    private GMailSender mMailSender;

    private int mProcessedMailCount;
    private int mSuccessMailCount;


    public static CartOrderFragment newInstance() {
        return new CartOrderFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (CartActivity) activity;
        mActivity.setSubmitListener(this);

        mActivity.initProgressDialog(mActivity);

        mMailSender = new GMailSender(Constant.kUserNameForMail, Constant.kPasswordForMail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_order, container, false);

        mEditName = (EditText) view.findViewById(R.id.edit_name);
        mEditEmail = (EditText) view.findViewById(R.id.edit_email);
        mEditPhone = (EditText) view.findViewById(R.id.edit_contact_number);
        mEditPassportNo = (EditText) view.findViewById(R.id.edit_passport_no);
        mEditComments = (EditText) view.findViewById(R.id.edit_comments);

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        mEditName.setText(sharedPreferences.getString(Constant.kUserNameKey, ""));
        mEditEmail.setText(sharedPreferences.getString(Constant.kUserMailKey, ""));
        mEditPhone.setText(sharedPreferences.getString(Constant.kUserContactNumKey, ""));
        mEditPassportNo.setText(sharedPreferences.getString(Constant.kUserNRICKey, ""));

        return view;
    }

    @Override
    public void onSubmit() {
        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String phone = mEditPhone.getText().toString();
        String passportNo = mEditPassportNo.getText().toString();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(passportNo)) {
            CommonUtils.createErrorAlertDialog(mActivity, "", "Please ensure that you have included your name, email and contact number").show();
            return;
        }

        if (!CommonUtils.isEmailValid(email)) {
            CommonUtils.createErrorAlertDialog(mActivity, "", "Your Mail Address is invalid!").show();
            return;
        }

        mActivity.mbSendingMail = false;
        mProcessedMailCount = 0;
        mSuccessMailCount = 0;

        mActivity.showProgress();

        sendMail(true);
        sendMail(false);
    }

    private void sendMail(boolean toUser) {
        String fromAddress;
        String toAddress;
        String replyAddress;

        if (toUser) {
            replyAddress = fromAddress = mActivity.getString(R.string.our_company_email);
            toAddress = mEditEmail.getText().toString();
        } else {
            replyAddress = fromAddress = mEditEmail.getText().toString();
            toAddress = mActivity.getString(R.string.our_company_email);
        }

        String subject = String.format("Camera Rental Centre - Booking Order (%s)", mEditName.getText().toString());
        String body = mailBody();

        new SendMailTask(subject, body, fromAddress, Constant.kEMailUserNameForCRC,
                toAddress, mEditName.getText().toString(), replyAddress).execute();
    }

    private String mailBody() {
        String userName = mEditName.getText().toString();
        userName = TextUtils.isEmpty(userName) ? "" : userName;

        String contactNum = mEditPhone.getText().toString();
        contactNum = TextUtils.isEmpty(contactNum) ? "" : contactNum;

        String userNRIC = mEditPassportNo.getText().toString();
        userNRIC = TextUtils.isEmpty(userNRIC) ? "" : userNRIC;

        String beginDate = mActivity.mStartDay.format3339(true);
        String endDate = null;
        if (mActivity.mEndDay != null)
            endDate = mActivity.mEndDay.format3339(true);

        String comment = mEditComments.getText().toString();
        comment = TextUtils.isEmpty(comment) ? "" : comment;

        String date;
        String equipList = "";

        String string = Constant.MAIL_TEMPLATE.replace(Constant.USER_NAME, userName);
        string = string.replace(Constant.CONTACT_NUM, contactNum);
        string = string.replace(Constant.USER_NRIC, userNRIC);
        string = string.replace(Constant.COMMENT, comment);

        // equipment list
        if (!TextUtils.isEmpty(beginDate) && !TextUtils.isEmpty(endDate)) {
            date = Constant.DOUBLE_DATE_TEMPLATE.replace(Constant.BEGIN_DATE, beginDate);
            date = date.replace(Constant.END_DATE, endDate);
        } else {
            date = Constant.SINGLE_DATE_TEMPLATE.replace(Constant.BEGIN_DATE, beginDate);
        }

        string = string.replace(Constant.DATE, date);

        // equipment list
        int count = EquipmentData.getInstance().cartCount();

        for (int i = 0; i < count; i++) {
            HashMap cartMap = EquipmentData.getInstance().cartDataWithIndex(i);
            String strEquipment = String.format("%d × %s",
                    Integer.parseInt(cartMap.get(Constant.kEquipmentCountKey).toString()),
                    cartMap.get(Constant.kEquipmentInformationKey));
            String str = Constant.EQUIPMENT_TEMPLATE.replace(Constant.EQUIPMENT, strEquipment);
            equipList = equipList + str;
        }

        string = string.replace(Constant.EQUIPMENT_LIST, equipList);

        return string;
    }

    private void reset() {
        // reset equipment data..
        EquipmentData.getInstance().clearCart();

        // reset calendar
        mActivity.mStartDay = null;
        mActivity.mEndDay = null;

        // save user info
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constant.kUserNameKey, mEditName.getText().toString());
        editor.putString(Constant.kUserMailKey, mEditEmail.getText().toString());
        editor.putString(Constant.kUserContactNumKey, mEditPhone.getText().toString());
        editor.putString(Constant.kUserNRICKey, mEditPassportNo.getText().toString());

        editor.apply();

        mEditComments.setText("");
    }

    class SendMailTask extends AsyncTask<Void, Integer, String> {

        private String subject;
        private String body;
        private String fromAddress;
        private String fromName;
        private String toAddress;
        private String toName;
        private String replyAddress;

        public SendMailTask(String subject,
                            String body,
                            String fromAddress,
                            String fromName,
                            String toAddress,
                            String toName,
                            String replyAddress) {
            this.subject = subject;
            this.body = body;
            this.fromAddress = fromAddress;
            this.fromName = fromName;
            this.toAddress = toAddress;
            this.toName = toName;
            this.replyAddress = replyAddress;
        }

        @Override
        protected void onPreExecute() {
            mActivity.showProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            mActivity.mbSendingMail = true;

            String errMessage = null;

            try {
                mMailSender.sendMail(subject, body, fromAddress, fromName, toAddress, toName, replyAddress);
            } catch (Exception e) {
                if (Config.DEBUG) e.printStackTrace();
                errMessage = e.getMessage();
            }

            return errMessage;
        }

        @Override
        protected void onPostExecute(String errMessage) {
            mProcessedMailCount++;

            if (TextUtils.isEmpty(errMessage)) {
                mSuccessMailCount++;
                Log.d(TAG, "Successfully sent email!");
            } else {
                Log.e(TAG, "Error sending email: " + errMessage);
            }

            if (mProcessedMailCount == 2) {
                mActivity.dismissProgress();

                mActivity.mbSendingMail = false;

                if (mSuccessMailCount == 2) {
                    reset();
                    mActivity.mPager.setCurrentItem(CartActivity.CartViewStyleDone, true);
                } else {
                    CommonUtils.createErrorAlertDialog(mActivity, "",
                            "We are unable to process your booking at the moment as the internet connection seems to be down. Please try again later.").show();
                }

                mProcessedMailCount = 0;
                mSuccessMailCount = 0;
            }
        }
    }

}
