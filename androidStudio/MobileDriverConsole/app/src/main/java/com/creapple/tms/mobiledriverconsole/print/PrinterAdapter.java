package com.creapple.tms.mobiledriverconsole.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import driver.BarcodeType;
import driver.Contants;
import driver.HsBluetoothPrintDriver;

//import java.text.Format;

//import org.apache.log4j.Logger;

/**
 * Created by js278 on 15/07/2016.
 */
public class PrinterAdapter {

    private static final String LOG_TAG = MDCUtils.getLogTag(PrinterAdapter.class);

    private static String CHARSET = "Windows-874";

    private static String UUID_TEST = "00001101-0000-1000-8000-00805F9B34FB";

//    private Logger logger = Logger.getLogger(LOG_TAG);

    PrinterViewAction viewAction;

    BluetoothAdapter bluetoothAdapter;

    ConnStateHandler mConnStateHandler;


    public PrinterAdapter(PrinterViewAction viewAction, BluetoothAdapter bluetoothAdapter){
        this.viewAction = viewAction;
        this.bluetoothAdapter = bluetoothAdapter;
        connectBluetooth(bluetoothAdapter);
    }

    /**
     * This can be called several times to ensure pairing printer
     * @param bluetoothAdapter
     */
    public void connectBluetooth(BluetoothAdapter bluetoothAdapter) {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        outter:for(BluetoothDevice device : devices){
            ParcelUuid[] uuids = device.getUuids();
            for(ParcelUuid parcelUuid: uuids){
                Log.e(LOG_TAG, device.getName() + "\t" + device.getAddress() + "\t" + parcelUuid);
                if(parcelUuid.toString().equalsIgnoreCase(UUID_TEST)){
                    connect(device);
                    Log.d(LOG_TAG, device.getName() + "\t" + device.getAddress() + "\t" + parcelUuid);
                    break outter;
                }
            }
        }
    }

    public void connect(BluetoothDevice bluetoothDevice){
        mConnStateHandler = new ConnStateHandler();
        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.setHandler(mConnStateHandler);
        hsBluetoothPrintDriver.start();
        hsBluetoothPrintDriver.connect(bluetoothDevice);
    }

    public void stopConnection(){
        HsBluetoothPrintDriver.getInstance().stop();
    }

    public void beep() {
        HsBluetoothPrintDriver.getInstance().Beep((byte) 1, (byte) 1);
    }

    public void selfPrinter() {
        HsBluetoothPrintDriver.getInstance().SelftestPrint();
    }

    public void halfCut() {
        HsBluetoothPrintDriver.getInstance().PartialCutPaper();
    }

    public void fullCut() {
        HsBluetoothPrintDriver.getInstance().CutPaper();
    }

    public void printCodeBar(String code) {
        HsBluetoothPrintDriver.getInstance().CODEBAR(code);
    }

    public void print(String msg) {
        HsBluetoothPrintDriver.getInstance().printString(msg);
    }

    public void printQRCode(String code) {
        HsBluetoothPrintDriver.getInstance().AddCodePrint(BarcodeType.QR_CODE, code);
        print();
    }

    private void print() {
        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.Begin();
        hsBluetoothPrintDriver.SetDefaultSetting();
        hsBluetoothPrintDriver.SetPrintRotate((byte) 0);
        hsBluetoothPrintDriver.SetAlignMode((byte) 0x01);
        hsBluetoothPrintDriver.SetHRIPosition((byte) 0x02);
        hsBluetoothPrintDriver.LF();
        hsBluetoothPrintDriver.CR();
        hsBluetoothPrintDriver.LF();
        hsBluetoothPrintDriver.CR();
        hsBluetoothPrintDriver.LF();
        hsBluetoothPrintDriver.CR();
        hsBluetoothPrintDriver.SetPrintRotate((byte) 0);
    }

    private class ConnStateHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getInt("flag")) {
                case Contants.FLAG_STATE_CHANGE:
                    int state = data.getInt("state");
                    break;
                case Contants.FLAG_FAIL_CONNECT:
                    viewAction.showFailed();
                    break;
                case Contants.FLAG_SUCCESS_CONNECT:
                    viewAction.showConnected();
                    break;
            }
        }
    }

    public void printTripOff(Map map){

        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.Begin();
        hsBluetoothPrintDriver.setCharsetName(CHARSET);
        try {

            //TITIE
            String title = "TRIP OFF";
            hsBluetoothPrintDriver.SetUnderline((byte)0x01);
            hsBluetoothPrintDriver.SetBold((byte)0x50);
            hsBluetoothPrintDriver.SetAlignMode((byte) 0x01);
            hsBluetoothPrintDriver.printString(title);

            //line1
            String date = StringUtils.defaultString(map.get(Constants.PRINT_DATE).toString(), Constants.PRINT_NO_DATE);//"26/07/2016 17.17";
            String line1 = "\n" + "         วันที่ " + date + "\n";

            //line 2
            String route = StringUtils.defaultString(map.get(Constants.PRINT_ROUTE).toString(), Constants.PRINT_NO_ROUTE);;//"554F";
            String bus = StringUtils.defaultString(map.get(Constants.PRINT_BUS).toString(), Constants.PRINT_NO_BUS);//"SV580004";
            String line2 = "สาย: " + route + "    " + "หมายเลขรถ: " + bus;
            line2 = getThaiFormat(line2);

            //line 3
            String trip = StringUtils.defaultString(map.get(Constants.TRIP_PATH).toString(), Constants.PRINT_NO_TRIP_PATH);// 201610180615_SV123456
            String line3 = "การเดินทาง: " + trip;
            line3 = getThaiFormat(line3);

            //line 4
            String ticketNum = StringUtils.defaultString(map.get(Constants.PRINT_TICKET_TOTAL_NUMBER).toString(), Constants.PRINT_NO_TICKET_TOTAL_NUMBER);// 89
            String line4 = "                  นับตั๋ว: " + ticketNum;
            line4 = getThaiFormat(line4);

            //line 5
            String fareSum = StringUtils.defaultString(map.get(Constants.PRINT_FARE_TOTAL).toString(), Constants.PRINT_NO_FARE_TOTAL);// 3782
            String line5 = "                  ผลรวมค่าโดยสาร: " + fareSum;
            line5 = getThaiFormat(line5);

            //line 7
            byte[] line7_vowelByteArray0 = (new byte[]{(byte) 0xD8}); //ุ
            byte[] line7_vowelByteArray1 = (new byte[]{(byte) 0x9B}); //ี่
            byte[] line7_vowelByteArray2 = (new byte[]{(byte) 0xE9}); // ้
            byte[] line7_vowelByteArray3 = (new byte[]{(byte) 0xD4}); //ิ
            String line_bottom_vowel = "   " + new String(line7_vowelByteArray0, CHARSET);
            String line_top_vowel   =   "     " + new String(line7_vowelByteArray1, CHARSET) + " "+  new String(line7_vowelByteArray2, CHARSET)
                    + " " +  new String(line7_vowelByteArray3, CHARSET);
            String line7 = line_top_vowel + "\n" + "ขอบคณทใชบรการ" + "\n" + line_bottom_vowel;

            hsBluetoothPrintDriver.SetAlignMode((byte) 0x00);
            hsBluetoothPrintDriver.SetUnderline((byte)0x00);
            hsBluetoothPrintDriver.printString(line1);
            hsBluetoothPrintDriver.printString(line2);
            hsBluetoothPrintDriver.printString(line3);
            hsBluetoothPrintDriver.printString(line4);
            hsBluetoothPrintDriver.printString(line5);
            hsBluetoothPrintDriver.printString(line7);
            hsBluetoothPrintDriver.SetHRIPosition((byte) 0x02);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CutPaper();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void printTicket(Map map) {

//        Toast.makeText(mContext, "4. printTicket() starts", Toast.LENGTH_SHORT).show();

        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.Begin();
        hsBluetoothPrintDriver.setCharsetName(CHARSET);
        try {

            //TITIE
            String PREMIUM = "PREMIUM";
            hsBluetoothPrintDriver.SetUnderline((byte)0x01);
            hsBluetoothPrintDriver.SetBold((byte)0x50);
            hsBluetoothPrintDriver.SetAlignMode((byte) 0x01);
            hsBluetoothPrintDriver.printString(PREMIUM);

            //line1
            String ticketNumber = StringUtils.defaultString(map.get(Constants.PRINT_TICKET_NUMBER).toString(), Constants.PRINT_NO_TICKET_NUMBER);//"00012";
            String date = StringUtils.defaultString(map.get(Constants.PRINT_DATE).toString(), Constants.PRINT_NO_DATE);//"26/07/2016 17.17";
            byte[] ticket_vowelByteArray = (new byte[]{(byte) 0x95}); // ํ๋
            byte[] date_vowelByteArray = (new byte[]{(byte) 0xD1}); // ั
            String vowel = "       " + new String(ticket_vowelByteArray, CHARSET) + "            " + new String(date_vowelByteArray, CHARSET);
            String ticket_out = "หมายเลขตว: " + ticketNumber;
            String date_out = "วนเวลา: " + date;
            String line1 = vowel + "\n" + ticket_out + "    " + date_out + "\n";

            //line 2
            String route = StringUtils.defaultString(map.get(Constants.PRINT_ROUTE).toString(), Constants.PRINT_NO_ROUTE);;//"554F";
            String bus = StringUtils.defaultString(map.get(Constants.PRINT_BUS).toString(), Constants.PRINT_NO_BUS);//"SV580004";
            String line2 = "สาย: " + route + "    " + "หมายเลขรถ: " + bus;
            line2 = getThaiFormat(line2);

            //line 3
            String from = StringUtils.defaultString(map.get(Constants.PRINT_FROM).toString(), Constants.PRINT_NO_FROM);;//"ด่านทับช้าง";
            String to = StringUtils.defaultString(map.get(Constants.PRINT_TO).toString(), Constants.PRINT_NO_TO);;//"เมืองทอง";
            String line3 = "ต้นทาง: " + from + "    " + "ปลายทาง: " + to;
            line3 = getThaiFormat(line3);

            //line 4
            String line4 = "";
            int adultCount = Integer.parseInt(StringUtils.defaultString(map.get(Constants.PRINT_ADULT_NUMBER_OF_PERSON).toString(),"0"));
            boolean isAdult = (adultCount > 0);
            if(isAdult) {
                String adultNumberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_ADULT_NUMBER_OF_PERSON).toString(), Constants.PRINT_NO_ADULT_NUMBER_OF_PERSON);//"2";
                String adultTotal = StringUtils.defaultString(map.get(Constants.PRINT_ADULT_TOTAL).toString(), Constants.PRINT_NO_ADULT_TOTAL);//"40";
                line4 = "จำนวนผู้ใหญ่: " + adultNumberOfPerson + "  " + "ค่าโดยสารรวมผู้ใหญ่: " + adultTotal;
                line4 = getThaiFormat(line4);
            }

            //line 5
            String line5 = "";
            int seniorCount = Integer.parseInt(StringUtils.defaultString(map.get(Constants.PRINT_SENIOR_NUMBER_OF_PERSON).toString(),"0"));
            boolean isSenior = (seniorCount > 0);
            if(isSenior) {
                String seniorNumberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_SENIOR_NUMBER_OF_PERSON).toString(), Constants.PRINT_NO_SENIOR_NUMBER_OF_PERSON);//"2";
                String seniorTotal = StringUtils.defaultString(map.get(Constants.PRINT_SENIOR_TOTAL).toString(), Constants.PRINT_NO_SENIOR_TOTAL);//"40";
                line5 = "จำนวนอาวุโส: " + seniorNumberOfPerson + "  " + "ค่าโดยสารอาวุโสทั้งหมด: " + seniorTotal;
                line5 = getThaiFormat(line5);
            }

            //line 6
            String line6 = "";
            int studentCount = Integer.parseInt(StringUtils.defaultString(map.get(Constants.PRINT_STUDENT_NUMBER_OF_PERSON).toString(),"0"));
            boolean isStudent = (studentCount > 0);
            if(isStudent) {
                String studentNumberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_STUDENT_NUMBER_OF_PERSON).toString(), Constants.PRINT_NO_STUDENT_NUMBER_OF_PERSON);//"2";
                String studentTotal = StringUtils.defaultString(map.get(Constants.PRINT_STUDENT_TOTAL).toString(), Constants.PRINT_NO_STUDENT_TOTAL);//"40";
                line6 = "จำนวนนักเรียน: " + studentNumberOfPerson + "  " + "นักเรียนราคารวม: " + studentTotal;
                line6 = getThaiFormat(line6);
            }

            //line 7
            byte[] line7_vowelByteArray0 = (new byte[]{(byte) 0xD8}); //ุ
            byte[] line7_vowelByteArray1 = (new byte[]{(byte) 0x9B}); //ี่
            byte[] line7_vowelByteArray2 = (new byte[]{(byte) 0xE9}); // ้
            byte[] line7_vowelByteArray3 = (new byte[]{(byte) 0xD4}); //ิ
            String line_bottom_vowel = "   " + new String(line7_vowelByteArray0, CHARSET);
            String line_top_vowel   =   "     " + new String(line7_vowelByteArray1, CHARSET) + " "+  new String(line7_vowelByteArray2, CHARSET)
                    + " " +  new String(line7_vowelByteArray3, CHARSET);
            String line7 = line_top_vowel + "\n" + "ขอบคณทใชบรการ" + "\n" + line_bottom_vowel;

            hsBluetoothPrintDriver.SetAlignMode((byte) 0x00);
            hsBluetoothPrintDriver.SetUnderline((byte)0x00);
            hsBluetoothPrintDriver.printString(line1);
            hsBluetoothPrintDriver.printString(line2);
            hsBluetoothPrintDriver.printString(line3);
            if(isAdult){
                hsBluetoothPrintDriver.printString(line4);
            }
            if(isSenior){
                hsBluetoothPrintDriver.printString(line5);
            }
            if(isStudent){
                hsBluetoothPrintDriver.printString(line6);
            }
            hsBluetoothPrintDriver.printString(line7);
            hsBluetoothPrintDriver.SetHRIPosition((byte) 0x02);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CutPaper();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getThaiFormat(String value) {
        try {

            String input = value;
            List<Format> vowels = new ArrayList<>();
            byte[] b = input.getBytes(Charset.forName(CHARSET));

            for (int i = 0; i < b.length; i++) {

                byte ascii = b[i];
//                Log.d("--> ", ascii + " ");
                if (ascii == (byte) 0xE8 || ascii == (byte) 0xE9 || ascii == (byte) 0xEA ||
                        ascii == (byte) 0xEB || ascii == (byte) 0xEC || ascii == (byte) 0xED || ascii == (byte) 0xEE) { //่้๊๋
                    int position;
                    if (vowels.isEmpty()) {
                        position = i - 1;
                    } else {
                        position = i - 1 - vowels.size();
                    }
                    Format format = new Format(ascii, position, true);
                    vowels.add(format);
                    input = input.replaceAll(new String(new byte[]{ascii}, CHARSET), "");
                } else if (ascii == (byte) 0xD1 || ascii == (byte) 0xD4 || ascii == (byte) 0xD5 ||
                        ascii == (byte) 0xD6 || ascii == (byte) 0xD7 || ascii == (byte) 0xDA ||
                        ascii == (byte) 0xDB || ascii == (byte) 0xDC || ascii == (byte) 0xDD || ascii == (byte) 0xDE) {
                    int position;
                    if (vowels.isEmpty()) {
                        position = i - 1;
                    } else {
                        position = i - 1 - vowels.size();
                    }
                    Format format = new Format(ascii, position, true);
                    vowels.add(format);
                    input = input.replaceAll(new String(new byte[]{ascii}, CHARSET), "");
                } else if (ascii == (byte) 0xD8 || ascii == (byte) 0xD9) {
                    int position;
                    if (vowels.isEmpty()) {
                        position = i - 1;
                    } else {
                        position = i - 1 - vowels.size();
                    }
                    Format format = new Format(ascii, position, false);
                    vowels.add(format);
                    input = input.replaceAll(new String(new byte[]{ascii}, CHARSET), "");
                }
            }


            String line_top_vowel = "";
            String line_bottom_vowel = "";
            int j = 0;
            for (int i = 0; i < b.length; i++) {
                if (j >= vowels.size()) break;
                Format format = vowels.get(j);
                if (format.position == i) {
//                    Log.d("==> ", i + " " + format.ascii);
                    if (format.isTop)
                        line_top_vowel += new String(new byte[]{format.ascii}, CHARSET);
                    else line_bottom_vowel += new String(new byte[]{format.ascii}, CHARSET);
                    j++;
                } else {
                    line_top_vowel += " ";
                    line_bottom_vowel += " ";
                }
            }

            if(line_top_vowel.isEmpty() && line_bottom_vowel.isEmpty()) {
                return  input;
            } else if(line_top_vowel.isEmpty() && !line_bottom_vowel.isEmpty()) {
                return input + "\n" + line_bottom_vowel;
            } else {
                return line_top_vowel + "\n" + input;
            }

        } catch (Exception e) {
            return "";
        }


    }

    class Format {
        byte ascii;
        int position;
        boolean isTop;

        Format(byte ascii, int position, boolean isTop) {
            this.ascii = ascii;
            this.position = position;
            this.isTop = isTop;
        }

    }
}

