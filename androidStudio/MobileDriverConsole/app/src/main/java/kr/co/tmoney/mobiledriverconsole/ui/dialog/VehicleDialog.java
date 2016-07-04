package kr.co.tmoney.mobiledriverconsole.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import kr.co.tmoney.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class VehicleDialog extends DialogFragment{

    // Bind the value between user selection in dialog and vehicle name on TripOffActivity
    public interface PassValueFromVehicleDialogListener{
        void sendVehicleName(String routeName);
    }

    public VehicleDialog() {
    }

    /**
     * This interface hooks up FareFragment and StopsDialog by calling
     * 1. calling setPassValueFromVehicleDialogListener() from TripOffActivity
     * 2. passing value via sendVehicleName() from VehicleDialog
     */
    PassValueFromVehicleDialogListener mPassValueFromVehicleDialogListener;

    public VehicleDialog(String[] names) {
        this.mNames = names;
    }

    String[] mNames;

    public void setPassValueFromVechicleDialogListener(PassValueFromVehicleDialogListener passValueFromVehicleDialogListener){
        mPassValueFromVehicleDialogListener = passValueFromVehicleDialogListener;
    }

    @NonNull
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
        View view = inflater.inflate(R.layout.vehicles_dialog, container, false);

            VehicleCustomAdapter vehicleCustomAdapter = new VehicleCustomAdapter(getActivity(), mNames);
            GridView gridView = (GridView) view.findViewById(R.id.vehicle_grid_view);
            gridView.setAdapter(vehicleCustomAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    // update TexView in FareFragment according to user's choice
                    mPassValueFromVehicleDialogListener.sendVehicleName(mNames[pos]);
                    dismiss();
                }
            });
        return view;
    }
}
