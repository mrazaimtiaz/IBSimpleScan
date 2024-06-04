package com.integratedbiometrics.ibsimplescan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.printer.UsbThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class USBPrinterActivity extends Activity {

    private String printVersion;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int MAKER = 13;
    private final int PRINTPICTURE = 14;
    private final int NOBLACKBLOCK = 15;

    private LinearLayout print_text, print_pic;
    private TextView text_index, pic_index,textPrintVersion;
    MyHandler handler;
    private EditText editTextLeftDistance,editTextLineDistance,editTextWordFont,editTextPrintGray,
            editTextBarcode,editTextQrcode,editTextPaperWalk,editTextContent,
            edittext_maker_search_distance,edittext_maker_walk_distance;
    private Button buttonBarcodePrint,buttonPaperWalkPrint,buttonContentPrint,buttonQrcodePrint,
            buttonGetExampleText,buttonGetZhExampleText,buttonGetFRExampleText,buttonClearText,
            button_maker,button_print_picture;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;

    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog progressDialog;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(USBPrinterActivity.this);
    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.bmp";

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(USBPrinterActivity.this);
                    alertDialog.setTitle("operation_result");
                    alertDialog.setMessage("LowBattery");
                    alertDialog.setPositiveButton("dialog_comfirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case NOBLACKBLOCK:
                    Toast.makeText(USBPrinterActivity.this, "maker_not_find", Toast.LENGTH_SHORT).show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        textPrintVersion.setText(printVersion);
                    } else {
                        Toast.makeText(USBPrinterActivity.this,"operation_fail", Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTBARCODE:
                    new barcodePrintThread().start();
                    break;
                case PRINTQRCODE:
                    new qrcodePrintThread().start();
                    break;
                case PRINTPAPERWALK:
                    new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case MAKER:
                    new MakerThread().start();
                    break;
                case PRINTPICTURE:
                    new printPicture().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !USBPrinterActivity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(USBPrinterActivity.this);
                    overHeatDialog.setTitle("operation_result");
                    overHeatDialog.setMessage("overTemp");
                    overHeatDialog.setPositiveButton("dialog_comfirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(USBPrinterActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void initView() {
        print_text = (LinearLayout) findViewById(R.id.print_text);
        print_pic = (LinearLayout) findViewById(R.id.print_code_and_pic);
        text_index = (TextView) findViewById(R.id.index_text);
        pic_index = (TextView) findViewById(R.id.index_pic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.usbprint_text);
        initView();
        savepic();
        handler = new MyHandler();
        buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);

        Log.e("390 devicetype--- = ",SystemUtil.getDeviceType() +" - "+ StringUtil.DeviceModelEnum.TPS390L.ordinal());

        IntentFilter pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
        registerReceiver(printReceive, pIntentFilter);

        editTextLeftDistance = (EditText) findViewById(R.id.set_leftDistance);
        editTextLineDistance = (EditText) findViewById(R.id.set_lineDistance);
        editTextWordFont = (EditText) findViewById(R.id.set_wordFont);
        editTextPrintGray = (EditText) findViewById(R.id.set_printGray);
        editTextBarcode = (EditText) findViewById(R.id.set_Barcode);
        editTextPaperWalk = (EditText) findViewById(R.id.set_paperWalk);
        editTextContent = (EditText) findViewById(R.id.set_content);
        textPrintVersion = (TextView) findViewById(R.id.print_version);
        editTextQrcode = (EditText) findViewById(R.id.set_Qrcode);
        edittext_maker_search_distance = (EditText) findViewById(R.id.edittext_maker_search_distance);
        edittext_maker_walk_distance = (EditText) findViewById(R.id.edittext_maker_walk_distance);
        buttonQrcodePrint = (Button) findViewById(R.id.print_qrcode);
        if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS900.ordinal()){
            editTextPrintGray.setText("5");
        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonQrcodePrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 7) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                qrcodeStr = editTextQrcode.getText().toString();
                if (qrcodeStr == null || qrcodeStr.length() == 0) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.input_print_data), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        progressDialog = ProgressDialog.show(USBPrinterActivity.this, getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));
                    } else {
                        Toast.makeText(USBPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        editTextContent.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        buttonBarcodePrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 7) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                barcodeStr = editTextBarcode.getText().toString();
                if (barcodeStr == null || barcodeStr.length() == 0) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        progressDialog = ProgressDialog.show(USBPrinterActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
                    } else {
                        Toast.makeText(USBPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonPaperWalkPrint = (Button) findViewById(R.id.print_paperWalk);
        buttonPaperWalkPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String exditText;
                exditText = editTextPaperWalk.getText().toString();
                if (exditText == null || exditText.length() == 0) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.walk_paper_intput_value), Toast.LENGTH_LONG).show();
                    return;
                }
                paperWalk = Integer.parseInt(exditText);
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        progressDialog = ProgressDialog.show(USBPrinterActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
                    } else {
                        Toast.makeText(USBPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonClearText = (Button) findViewById(R.id.clearText);
        buttonClearText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextContent.setText("");
            }
        });
        buttonGetExampleText = (Button) findViewById(R.id.getPrintExample);
        buttonGetExampleText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "\n---------------------------\n" +
                        "Print Test:\n" +
                        "Device Base Information\n" +
                        "Printer Version:\n" +
                        "V05.2.0.3\n" +
                        "Printer Gray:3\n" +
                        "Soft Version:\n"+
                        "Demo.G50.0.Build140313\n" +
                        "Battery Level:100%\n" +
                        "CSQ Value:24\n" +
                        "IMEI:86378902177527\n" +
                        "---------------------------\n" +
                        "---------------------------\n" +
                        "Print Test:\n" +
                        "Device Base Information\n" +
                        "Printer Version:\n" +
                        "V05.2.0.3\n" +
                        "Printer Gray:3\n" +
                        "Soft Version:\n"+
                        "Demo.G50.0.Build140313\n" +
                        "Battery Level:100%\n" +
                        "CSQ Value:24\n" +
                        "IMEI:86378902177527\n" +
                        "---------------------------\n" +
                        "---------------------------\n" +
                        "Print Test:\n" +
                        "Device Base Information\n" +
                        "Printer Version:\n" +
                        "V05.2.0.3\n" +
                        "Printer Gray:3\n" +
                        "Soft Version:\n"+
                        "Demo.G50.0.Build140313\n" +
                        "Battery Level:100%\n" +
                        "CSQ Value:24\n" +
                        "IMEI:86378902177527\n" +
                        "---------------------------\n";
                //String str ="나는 모른다.";
                editTextContent.setText(str);
            }
        });

        buttonGetZhExampleText = (Button) findViewById(R.id.getZhPrintExample);
        buttonGetZhExampleText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "\n             烧烤" + "\n---------------------------" + "\n日期：2015-01-01 16:18:20" + "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n---------------------------"
                        + "\n    项目        数量   单价  小计" +
                        "\n秘制烤羊腿    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全羊            1      200    200"+
                        "\n秘制烤鸡腿    1      56      56" +
                        "\n烤牛腿            2      50      100" +
                        "\n烤猪蹄            1      200    200"+
                        "\n秘制烤牛腿    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全羊            1      200    200"+
                        "\n秘制烤猪腿    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全牛            1      200    200"+
                        "\n特色烤鸭腿    1      56      56" +
                        "\n烤土鸡            2      50      100" +
                        "\n烤全羊            1      200    200"+
                        "\n秘制烤火腿    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全羊            1      200    200"+
                        "\n秘制烤鸡腿    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全羊            1      200    200"+
                        "\n秘制烤火腿    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全羊            1      200    200"+
                        "\n秘制烤牛筋    1      56      56" +
                        "\n烤土鸡            2      50      100" +
                        "\n烤白鸽            1      200    200"+
                        "\n秘制鸭下巴    1      56      56" +
                        "\n烤火鸡            2      50      100" +
                        "\n烤全牛            1      200    200"+
                        "\n 合计：1000:00元" +
                        "\n----------------------------" +
                        "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------" +
                        "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
                editTextContent.setText(str);
            }
        });

        buttonGetFRExampleText = (Button) findViewById(R.id.getFrPrintExample);
        buttonGetFRExampleText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = "\nÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóô\n" +
                        " مرحبا ، من فضلك أرني هويتك  شكرا لتعاونكم ";
                editTextContent.setText(str);

                //new EscPrintThread().start();//ESCcommand print
            }

        });

        buttonContentPrint = (Button) findViewById(R.id.print_content);
        buttonContentPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String exditText;
                exditText = editTextLeftDistance.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                leftDistance = Integer.parseInt(exditText);
                exditText = editTextLineDistance.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                lineDistance = Integer.parseInt(exditText);
                printContent = editTextContent.getText().toString();
                exditText = editTextWordFont.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                wordFont = Integer.parseInt(exditText);
                exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (leftDistance > MAX_LEFT_DISTANCE) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
                    return;
                }
                if (lineDistance > 255) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
                    return;
                }
                if (wordFont > 4 || wordFont < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
                    return;
                }
                if (printGray < 0 || printGray > 7) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (printContent == null || printContent.length() == 0) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        progressDialog = ProgressDialog.show(USBPrinterActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
                    } else {
                        Toast.makeText(USBPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        button_maker = (Button) findViewById(R.id.button_maker);
        button_maker.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edittext_maker_search_distance.getText().length() == 0 || edittext_maker_walk_distance.getText().length() == 0) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(edittext_maker_search_distance.getText().toString()) < 0 || Integer.parseInt(edittext_maker_search_distance.getText().toString()) > 255) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(edittext_maker_walk_distance.getText().toString()) < 0 || Integer.parseInt(edittext_maker_walk_distance.getText().toString()) > 255) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        progressDialog = ProgressDialog.show(USBPrinterActivity.this, getString(R.string.maker), getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(MAKER, 1, 0, null));
                    } else {
                        Toast.makeText(USBPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_print_picture = (Button) findViewById(R.id.button_print_picture);
        button_print_picture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String exditText = editTextPrintGray.getText().toString();
                if (exditText == null || exditText.length() < 1) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG).show();
                    return;
                }
                printGray = Integer.parseInt(exditText);
                if (printGray < 0 || printGray > 7) {
                    Toast.makeText(USBPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
                    return;
                }
                if (LowBattery == true) {
                    handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
                } else {
                    if (!nopaper) {
                        progressDialog = ProgressDialog.show(USBPrinterActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                        handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
                    } else {
                        Toast.makeText(USBPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        dialog = new ProgressDialog(USBPrinterActivity.this);
        dialog.setTitle(R.string.idcard_czz);
        dialog.setMessage(getText(R.string.watting));
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mUsbThermalPrinter.start(0);
                    mUsbThermalPrinter.reset();
                    printVersion = mUsbThermalPrinter.getVersion();
                    //int st=mUsbThermalPrinter.checkStatus();
                    //Log.e("checkstatus()","status"+" "+st);
                } catch (Exception e) {
                    if (e.toString().equals("com.telpo.tps550.api.printer.OverHeatException")) {
                        handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                    }
                    Log.e("checkstatus()","status error"+" "+e.toString());
                    e.printStackTrace();

                } finally {
                    if (printVersion != null) {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "1";
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "0";
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();

    }

    /* Called when the application resumes */
    @Override
    protected void onResume() {
        super.onResume();
    }

    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                //TPS390 can not print,while in low battery,whether is charging or not charging
                if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()){
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                }else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        if (level * 5 <= scale) {
                        LowBattery = true;
                        } else {
                            LowBattery = false;
                        }
                    } else {
                        LowBattery = false;
                    }
                }
            }
            //Only use for TPS550MTK devices
            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
                int status = intent.getIntExtra("action", 0);
                int level = intent.getIntExtra("level", 0);
                if(status == 0){
                    if(level < 1){
                        LowBattery = true;
                    }else {
                        LowBattery = false;
                    }
                }else {
                    LowBattery = false;
                }
            }
        }
    };

    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(USBPrinterActivity.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dlg.show();
    }

    private class paperWalkPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.walkPaper(paperWalk);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    private class barcodePrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);
                Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
                if(bitmap != null){
                    mUsbThermalPrinter.printLogo(bitmap,true);
                }
                mUsbThermalPrinter.addString(barcodeStr);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(20);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    private class qrcodePrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);
                Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
                if(bitmap != null){
                    mUsbThermalPrinter.printLogo(bitmap, true);
                }
                mUsbThermalPrinter.addString(qrcodeStr);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(20);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    private class EscPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                String base64Ticket = "G0AbYQEbZAEbIQF0cmdvdmFjLCBwcm9kIG1qZXN0bwobIQAbIQFaYWdyZWIsIFNhdnNrYSAzMgobIQAbZAEbRTFQT1RWUkRBIE8gVVBMQVRJChtFMD09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT0KGyEQG0UxVFYgQmluZ28gICAgIC"+
                        "AgICAgICAgICBLb2xvIDA0OQobRTAbIQA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09ClVQTEFUTkkgQlJPSiAgICAgICAgMDA4MDAgMDEzNDMKLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQobIQFWUklKRU1FIElaVkxBrEVOSkEgICA"+
                        "gICAgICAgICAgICAgMDMuMTIuMjAxOC4gdSAxODoyMCBoChshAD09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT0KUFJPVkpFUkEgRE9CSVRLQQpTTVMgS09EIBtFMSoqVEVTVCoqChtFMBshAVBv52Fsaml0ZSBTTVMga29kIG5hIGJyb2"+
                        "ogGyEAG0UxNjEzMTM2ICgxLDg2IGtuL3BvcnVrYSkKG0UwGyEBaWxpIHByb3ZqZXJpdGUgYmVzcGxhdG5vIG5hIBshABstMXd3dy5sdXRyaWphLmhyChstMBshAVJlenVsdGF0aSBkb3N0dXBuaSAyIHNhdGEgbmFrb24gaXp2bGGfZW5qYQobI"+
                        "QA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09CkJJTkdPIDE1IE9EIDkwIEtPTUJJTkFDSUpFChshAS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQobIQAyMyA0OSA1MyA3MSA4MAoxNyAzMCA0NyA2OS"+
                        "A4NQogMyAxMSA0NSA1NiA4OQobIQEtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0KGyEAMjAgMzYgNjcgNzUgOTAKIDQgMTggNjEgNzAgODQKMTkgMzUgNDAgNTUgODYKGyEBLS0tLS0tLS0tLS0tLS0tLS0tL"+
                        "S0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tChshACA4IDEzIDQ0IDU3IDc3CiA5IDIxIDM3IDY0IDc0CiAxIDI1IDY2IDczIDgzChshAS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQobIQAgNyAxNiA0MyA2MCA3N"+
                        "goxNCAyOCAzMSA1NCA4OAogMiAyNiA1MCA3MiA4MgobIQEtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0KGyEAMjIgMzIgNDIgNTggNzgKIDYgMTUgMzkgNDggNjIKMTAgMzggNTEgNjUgODcKGyEBLS0tLS0t"+
                        "LS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tChshADEyIDI5IDM0IDQ2IDc5CjI0IDMzIDQxIDU5IDY4CiA1IDI3IDUyIDYzIDgxCj09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT0KQklOR08gMjQgT0QgNzUgS09NQklOQ"+
                        "UNJSkUKGyEBLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tChshACA5IDIwIDM5IDU2IDcyCjEyIDE4IDQ0IDQ2IDYxCjEwIDIxIC0tIDQ5IDY5CjE1IDI1IDM0IDUyIDc0CiAzIDIzIDMyIDU3IDY4ChshAS0tLS0tLS0tLS0tLS"+
                        "0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQobIQAxNCAyMiAzMyA1OCA3MAogNSAzMCAzOCA1NCA2NAogMSAyOCAtLSA1OSA3NQoxMyAxNyA0MSA1MCA2NQogNyAyNyA0MyA1NSA2NgobIQEtLS0tLS0tLS0tLS0tLS0tLS0tL"+
                        "S0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0KGyEAIDIgMjYgNDIgNDcgNjcKIDQgMTYgMzEgNjAgNzEKMTEgMTkgLS0gNTMgNjIKIDggMjQgMzYgNDggNjMKIDYgMjkgMzcgNTEgNzMKPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PQ"+
                        "pCaW5nbyBwbHVzIGJyb2pldmkbRTEgICAgICAxNjk3ODE0ChtFMBtFMSAgICAgICAgICAgICAgICAgICAgICAgIDQ2MzU0ODcKG0UwG0UxICAgICAgICAgICAgICAgICAgICAgICAgODI3MDgzMAobRTA9PT09PT09PT09PT09PT09PT09PT09PT09P"+
                        "T09PT09ClNlcmlqc2tpIGJyb2ogbGlzdGmGYRtFMTk5OSAwMDAwOTE5ChtFMD09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT0KGyEQG0UxSXpub3MgdXBsYXRlID0gKjI0LDAwIGtuChtFMBshABtkARshAVVwbGF0b20gaWdyZSBuYXZ"+
                        "lZGVuZSBuYSBvdm9qIHBvdHZyZGkgaWdyYZ8gcG90dnLQdWplCmtha28gamUgdXBvem5hdCBzIHByYXZpbGltYSBpZ3JlIHRlIGlzdGEgdSBjaWplbG9zdGkKcHJpaHZhhmEKGyEAG2QBOTItNDI2ICAzMC4xMS4yMDE4Li8xNjoyMDo1MQodSAA"+
                        "dZgAdaG4ddwIda0kMe0M0MRAyEgBQAA0rG0UxNTI0OTE2NTAxOCAwMDgwMDAxMzQzG0UwG2QBrFVWQUpURSBTVk9KVSBQT1RWUkRVLAobIQFvbmEgamUgamVkaW5pIERPS0FaIFpBIFNVREpFTE9WQU5KRSBVIElHUkkuChshABsh"+
                        "AU5lIGl6bGGnaXRlIGplIHV0amVjYWp1IHZsYWdlIGkgamFrb2cgc3ZpamV0bGEhGyEAG2QJHVYB";
                byte[] encodedTicketBytes = Base64.decode(base64Ticket, Base64.DEFAULT);
                mUsbThermalPrinter.EscPosCommandExe(new byte[]{0x1b,0x74,0x12});
                mUsbThermalPrinter.EscPosCommandExe(encodedTicketBytes);

                mUsbThermalPrinter.walkPaper(20);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    private class contentPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                mUsbThermalPrinter.setLeftIndent(leftDistance);
                mUsbThermalPrinter.setLineSpace(lineDistance);
                if (wordFont == 4) {
                    mUsbThermalPrinter.setTextSize(40);
                } else if (wordFont == 3) {
                    mUsbThermalPrinter.setTextSize(30);
                } else if (wordFont == 2) {
                    mUsbThermalPrinter.setTextSize(20);
                } else if (wordFont == 1) {
                    mUsbThermalPrinter.setTextSize(10);
                }
                //mUsbThermalPrinter.setHighlight(true);
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.addString(printContent);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(20);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    private class MakerThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.searchMark(Integer.parseInt(edittext_maker_search_distance.getText().toString()),
                        Integer.parseInt(edittext_maker_walk_distance.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else if (Result.equals("com.telpo.tps550.api.printer.BlackBlockNotFoundException")) {
                    handler.sendMessage(handler.obtainMessage(NOBLACKBLOCK, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    private class printPicture extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                File file = new File(picturePath);
                if (file.exists()) {
                    mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath),false);
                    mUsbThermalPrinter.walkPaper(20);
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(USBPrinterActivity.this, getString(R.string.not_find_picture), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && !USBPrinterActivity.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        unregisterReceiver(printReceive);
        mUsbThermalPrinter.stop();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 生成条码
     *
     * @param str
     *            条码内容
     * @param type
     *            条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX,
     *            EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14,
     *            RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
     * @param bmpWidth
     *            生成位图宽,宽不能大于384，不然大于打印纸宽度
     * @param bmpHeight
     *            生成位图高，8的倍数
     */

    public Bitmap CreateCode(String str, com.google.zxing.BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        Hashtable<EncodeHintType,String> mHashtable = new Hashtable<EncodeHintType,String>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void selectIndex(View view) {
        switch (view.getId()) {
            case R.id.index_text:
                text_index.setEnabled(false);
                pic_index.setEnabled(true);
                print_text.setVisibility(View.VISIBLE);
                print_pic.setVisibility(View.GONE);

                break;

            case R.id.index_pic:

                text_index.setEnabled(true);
                pic_index.setEnabled(false);
                print_text.setVisibility(View.GONE);
                print_pic.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void savepic() {
        File file = new File(picturePath);
        if (!file.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("syhlogo.png");
                fos = new FileOutputStream(file);
                int length = 0;
                while((length = inputStream.read(tmp)) > 0){
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
