package kr.co.tmoney.mobiledriverconsole.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.PrinterAdapter;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class PrintConfirmationDialog extends Dialog {

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;


    private PrinterAdapter mPrinterAdapter;

    public PrintConfirmationDialog(Context context, PrinterAdapter printerAdapter, final SpannableStringBuilder confirm, final int count) {
        super(context);

        mPrinterAdapter = printerAdapter;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.print_confirmation);
//        setTitle("Print Receipt");

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        mConfirmTxt = (TextView) findViewById(R.id.print_confimation_txt);
        mConfirmTxt.setText(confirm);

        mConfirmBtn = (Button) findViewById(R.id.print_confirmation_btn);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrinterAdapter.print(confirm.toString());
                mPrinterAdapter.fullCut();
                MDCMainActivity.passengerCount += count;
                MDCMainActivity.fareTransactionId++;
                dismiss();
            }
        });
        mCancelBtn = (Button) findViewById(R.id.print_cancel_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
