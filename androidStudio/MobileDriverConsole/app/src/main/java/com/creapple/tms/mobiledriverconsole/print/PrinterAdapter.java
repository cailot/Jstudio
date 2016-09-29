package com.creapple.tms.mobiledriverconsole.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

//    private Logger logger = Logger.getLogger(LOG_TAG);

    PrinterViewAction viewAction;

    BluetoothAdapter bluetoothAdapter;

    public PrinterAdapter(PrinterViewAction viewAction, BluetoothAdapter bluetoothAdapter){
        this.viewAction = viewAction;

        this.bluetoothAdapter = bluetoothAdapter;

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : devices){
            Log.d(LOG_TAG, device.getName() + "\t" + device.getAddress());
            if(device.getName().equalsIgnoreCase("820USEB")){
                connectBluetooth(device);
                return;
            }
        }
    }

    public void connectBluetooth(BluetoothDevice bluetoothDevice){
        ConnStateHandler connStateHandler = new ConnStateHandler();
        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.setHandler(connStateHandler);
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

    public void print(Map map) {

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
            String ticketNumber = StringUtils.defaultString(map.get(Constants.PRINT_TICKET_NUMBER).toString());//"00012";
            String date = StringUtils.defaultString(map.get(Constants.PRINT_DATE).toString());//"26/07/2016 17.17";
            byte[] ticket_vowelByteArray = (new byte[]{(byte) 0x95}); // ํ๋
            byte[] date_vowelByteArray = (new byte[]{(byte) 0xD1}); // ั
            String vowel = "       " + new String(ticket_vowelByteArray, CHARSET) + "            " + new String(date_vowelByteArray, CHARSET);
            String ticket_out = "หมายเลขตว: " + ticketNumber;
            String date_out = "วนเวลา: " + date;
            String line1 = vowel + "\n" + ticket_out + "    " + date_out + "\n";
            Log.d("#######", line1);



            //line 2
            String route = StringUtils.defaultString(map.get(Constants.PRINT_ROUTE).toString());;//"554F";
            String bus = StringUtils.defaultString(map.get(Constants.PRINT_BUS).toString());//"SV580004";
            String line2 = "สาย: " + route + "    " + "หมายเลขรถ: " + bus;
            line2 = getThaiFormat(line2);

            //line 3
            String from = StringUtils.defaultString(map.get(Constants.PRINT_FROM).toString());;//"ด่านทับช้าง";
            String to = StringUtils.defaultString(map.get(Constants.PRINT_TO).toString());;//"เมืองทอง";
            String line3 = "ต้นทาง: " + from + "    " + "ปลายทาง: " + to;
            line3 = getThaiFormat(line3);

            //line 4
//            String numberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_NUMBER_OF_PERSON).toString());//"2";
//            String farePerPerson = StringUtils.defaultString(map.get(Constants.PRINT_FARE_PER_PERSON).toString());//"20";
//            String total = StringUtils.defaultString(map.get(Constants.PRINT_TOTAL).toString());//"40";
//            String line4 = "จำนวน: " + numberOfPerson + "     " + "ราคาต่อคน: " + farePerPerson + "    " + "ราคารวม: " + total;
//            line4 = getThaiFormat(line4);
            String adultNumberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_ADULT_NUMBER_OF_PERSON).toString());//"2";
            String adultFarePerPerson = StringUtils.defaultString(map.get(Constants.PRINT_ADULT_FARE_PER_PERSON).toString());//"20";
            String adultTotal = StringUtils.defaultString(map.get(Constants.PRINT_ADULT_TOTAL).toString());//"40";
//            String line4 = "จำนวนผู้ใหญ่: " + adultNumberOfPerson + "  " + "ผราคาของผู้ใหญ่: " + adultFarePerPerson + "  " + "ค่าโดยสารรวมผู้ใหญ่: " + adultTotal;
            String line4 = "จำนวนผู้ใหญ่: " + adultNumberOfPerson + "  " + "ค่าโดยสารรวมผู้ใหญ่: " + adultTotal;
            line4 = getThaiFormat(line4);

            String seniorNumberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_SENIOR_NUMBER_OF_PERSON).toString());//"2";
            String seniorFarePerPerson = StringUtils.defaultString(map.get(Constants.PRINT_SENIOR_FARE_PER_PERSON).toString());//"20";
            String seniorTotal = StringUtils.defaultString(map.get(Constants.PRINT_SENIOR_TOTAL).toString());//"40";
//            String line5 = "จำนวนอาวุโส: " + seniorNumberOfPerson + "  " + "ราคาของอาวุโส: " + seniorFarePerPerson + "  " + "ค่าโดยสารอาวุโสทั้งหมด: " + seniorTotal;
            String line5 = "จำนวนอาวุโส: " + seniorNumberOfPerson + "  " + "ค่าโดยสารอาวุโสทั้งหมด: " + seniorTotal;
            line5 = getThaiFormat(line5);

            String studentNumberOfPerson = StringUtils.defaultString(map.get(Constants.PRINT_STUDENT_NUMBER_OF_PERSON).toString());//"2";
            String studentFarePerPerson = StringUtils.defaultString(map.get(Constants.PRINT_STUDENT_FARE_PER_PERSON).toString());//"20";
            String studentTotal = StringUtils.defaultString(map.get(Constants.PRINT_STUDENT_TOTAL).toString());//"40";
//            String line6 = "จำนวนนักเรียน: " + studentNumberOfPerson + "  " + "ค่าโดยสารนักเรียน: " + studentFarePerPerson + "  " + "นักเรียนราคารวม: " + studentTotal;
            String line6 = "จำนวนนักเรียน: " + studentNumberOfPerson + "  " + "นักเรียนราคารวม: " + studentTotal;
            line6 = getThaiFormat(line6);

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
            hsBluetoothPrintDriver.printString(line6);
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
                Log.d("--> ", ascii + " ");
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
                    Log.d("==> ", i + " " + format.ascii);
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

