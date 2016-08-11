package kr.co.tmoney.mobiledriverconsole.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.print.PrinterAdapter;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class PrintConfirmationDialog extends Dialog {

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;


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
}
