package com.creapple.tms.mobiledriverconsole.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.creapple.tms.mobiledriverconsole.MDCMainActivity;
import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.model.vo.TransactionVO;
import com.creapple.tms.mobiledriverconsole.print.PrinterAdapter;
import com.creapple.tms.mobiledriverconsole.utils.Constants;

import java.util.Map;

/**
 * Created by jinseo on 2016. 7. 17..
 */
//public class PrintConfirmationDialog extends Dialog {
public class PrintConfirmationDialog extends ImmersiveDialogFragment {
    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;

    private Context mContext;

    private MDCMainActivity mMainActivity;

    private PrinterAdapter mPrinterAdapter;

    private SpannableStringBuilder mConfirm;

    private Map mData;


    public PrintConfirmationDialog() {
    }

    @SuppressLint("ValidFragment")
    public PrintConfirmationDialog(Context context, PrinterAdapter printerAdapter, final SpannableStringBuilder confirm, final Map data) {
        mContext = context;
        mPrinterAdapter = printerAdapter;
        mConfirm = confirm;
        mData = data;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.print_confirmation, container, false);

        mConfirmTxt = (TextView) view.findViewById(R.id.print_confimation_txt);
        mConfirmTxt.setText(mConfirm);
        mConfirmBtn = (Button) view.findViewById(R.id.print_confirmation_btn);
        mConfirmBtn.setText(mContext.getString(R.string.dialog_confirm));
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrinterAdapter.printTicket(mData);

                Map params = mData;
                int adultCount = Integer.parseInt(params.get(Constants.PRINT_ADULT_NUMBER_OF_PERSON).toString());
                int adultFare = Integer.parseInt(params.get(Constants.PRINT_ADULT_TOTAL).toString());
                int seniorCount = Integer.parseInt(params.get(Constants.PRINT_SENIOR_NUMBER_OF_PERSON).toString());
                int seniorFare = Integer.parseInt(params.get(Constants.PRINT_SENIOR_TOTAL).toString());
                int studentCount = Integer.parseInt(params.get(Constants.PRINT_STUDENT_NUMBER_OF_PERSON).toString());
                int studentFare = Integer.parseInt(params.get(Constants.PRINT_STUDENT_TOTAL).toString());

                // add transactions under trips
                TransactionVO transactionVO = new TransactionVO();
                transactionVO.setUUID(MDCMainActivity.fareTransactionId);
                transactionVO.setAdultNo(adultCount);
                transactionVO.setAdultPrice(adultFare);
                transactionVO.setSeniorNo(seniorCount);
                transactionVO.setSeniorPrice(seniorFare);
                transactionVO.setStudentNo(studentCount);
                transactionVO.setStudentPrice(studentFare);
                transactionVO.setOriginName(params.get(Constants.PRINT_FROM)+"");
                transactionVO.setOriginId(mMainActivity.getStopId(params.get(Constants.PRINT_FROM)+""));
                transactionVO.setDestinationName(params.get(Constants.PRINT_TO)+"");
//                transactionVO.setDestinationId(mMainActivity.getStopId(params.get(Constants.PRINT_TO)+""));

                mMainActivity.logTransaction(transactionVO);

                MDCMainActivity.mPassengerCountSum += (adultCount + seniorCount + studentCount);
                MDCMainActivity.mFareCashSum += (adultFare + seniorFare + studentFare);
                MDCMainActivity.fareTransactionId++;

                // update total count & fare
                mMainActivity.updateTotalFare();

                dismiss();
            }
        });
        mCancelBtn = (Button) view.findViewById(R.id.print_cancel_btn);
        mCancelBtn.setText(mContext.getString(R.string.dialog_cancel));
        mCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    public void setMainActivity(MDCMainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }
}
