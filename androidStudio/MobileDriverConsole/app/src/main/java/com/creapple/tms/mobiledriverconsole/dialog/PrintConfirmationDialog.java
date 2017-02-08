package com.creapple.tms.mobiledriverconsole.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
public class PrintConfirmationDialog extends Dialog {

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;

    private MDCMainActivity mMainActivity;

    private PrinterAdapter mPrinterAdapter;

    public PrintConfirmationDialog(Context context, PrinterAdapter printerAdapter, final SpannableStringBuilder confirm, final Map map) {
        super(context);

        mPrinterAdapter = printerAdapter;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.print_confirmation);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        mConfirmTxt = (TextView) findViewById(R.id.print_confimation_txt);
        mConfirmTxt.setText(confirm);

        mConfirmBtn = (Button) findViewById(R.id.print_confirmation_btn);
        mConfirmBtn.setText(context.getString(R.string.dialog_confirm));
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrinterAdapter.printTicket(map);

                Map params = map;
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
        mCancelBtn = (Button) findViewById(R.id.print_cancel_btn);
        mCancelBtn.setText(context.getString(R.string.dialog_cancel));
        mCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //              Immersive mode for Dialog
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //Set the dialog to immersive
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


    }

    public void setMainActivity(MDCMainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }
}
