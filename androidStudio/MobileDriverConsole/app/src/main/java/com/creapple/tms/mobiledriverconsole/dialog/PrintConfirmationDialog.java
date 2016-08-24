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
                mPrinterAdapter.print(map);
                Map params = map;
                int count = Integer.parseInt(params.get(Constants.PRINT_NUMBER_OF_PERSON).toString());
                int fare = Integer.parseInt(params.get(Constants.PRINT_TOTAL).toString());

//                MDCMainActivity.mPassengerCount += count;
//                MDCMainActivity.mPassengerCountSum += count;
//                MDCMainActivity.mFareCash += fare;
//                MDCMainActivity.mFareCashSum += fare;
//                MDCMainActivity.fareTransactionId++;

                // add transactions under trips
                TransactionVO transactionVO = new TransactionVO();
                transactionVO.setUUID(MDCMainActivity.fareTransactionId);
                transactionVO.setAdultNo(count);
                transactionVO.setAdultPrice(fare);
                transactionVO.setOriginName(params.get(Constants.PRINT_FROM)+"");
                transactionVO.setDestinationName(params.get(Constants.PRINT_TO)+"");

                mMainActivity.logTransaction(transactionVO);

                MDCMainActivity.mPassengerCount += count;
                MDCMainActivity.mPassengerCountSum += count;
                MDCMainActivity.mFareCash += fare;
                MDCMainActivity.mFareCashSum += fare;
                MDCMainActivity.fareTransactionId++;

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

    }

    public void setMainActivity(MDCMainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }
}
