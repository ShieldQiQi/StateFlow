package com.android.renly.distancemeasure.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.renly.distancemeasure.App;
import com.android.renly.distancemeasure.Bean.MeasureData;
import com.android.renly.distancemeasure.DB.MySQLiteOpenHelper;
import com.android.renly.distancemeasure.R;
import com.android.renly.distancemeasure.Utils.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.myToolBar)
    FrameLayout myToolBar;
    @BindView(R.id.lv_main)
    ListView lvMain;
    @BindView(R.id.tv_time)
    Chronometer timer;

    private Unbinder unbinder;
    public static final int REQUEST_CODE = 256;
    private boolean State_btn_left = false;
    private boolean State_btn_right = false;
    private static final int NOT_MEASURE = 0;
    private static final int MEASUREING = 1;
    private static final int SUCCESS_MEASURE = 2;
    private static final int FAIL_MEASURE = 3;
    private int measureResult = NOT_MEASURE;

    // 限制时间
    private static final int END_TIME = 300;
    // 限制长度
    private static final int END_DISTANCE = 5;
    // MAC地址
    private String MACAddr = "20:19:02:14:27:87";

    private String carid = "测试车牌000";
    private String carDirection = "测试方向";
    private int startDistance = 0;
    private int nowDistance = 0;
    private String measureTime;
    private String theTime;
    private int theID;

    private boolean isBlueToothConnected = false;
    private boolean isFirstData = true;
    private boolean isNewMeasure = true;
    private boolean isCompleted = false;
    private int DataCode = 0;

    private String[] keys = new String[]{
            "Is TS ON",
            "Match Mode",
            "ECU Error Flag",
            "ESC Error Flag",
            "RES Error Flag",
            "EBS Error Flag",
            "Steering Error Flag",
            "Is Gas Enough",
            "Is Brake Engaged",
            "is Steer Enable",
            "Who Cut The S.C",
            "Why ECU Cut S.C",
            "DV State",
            "Current Angle",
            "ACC_Angle RawData",
            "ACC_Liner RawData",
            "DCC_Liner RawData",
            "HV Battery Voltage",
            "Temp PUMP",
            "BMS State",
            "Is AMK Enable",
            "Oil Pressure_bl",
            "Oil Pressure_br",
            "Oil Pressure_fl",
            "Oil Pressure_fr",
            "AMK tempMotor_bl",
            "AMK tempMotor_br",
            "AMK tempMotor_fl",
            "AMK tempMotor_fr",
            "AMK tempInverter_bl",
            "AMK tempInverter_br",
            "AMK tempInverter_fl",
            "AMK tempInverter_fr",
            "AMK tempIGBT_bl",
            "AMK tempIGBT_br",
            "AMK tempIGBT_fl",
            "AMK tempIGBT_fr",
    };

    private String[] values = new String[]{
            "NO",
            "有人驾驶",
            "No Error",
            "No Error",
            "No Error",
            "No Error",
            "No Error",
            "Yes",
            "No",
            "No",
            "No error",
            "No error",
            "有人模式",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
            "获取中...",
    };

    private int[] imgs = new int[]{
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
            R.drawable.ic_alarm_black_24dp,
            R.drawable.ic_alarm_on_black_24dp,
            R.drawable.ic_chrome_reader_mode_black_24dp,
            R.drawable.ic_directions_car_black_24dp,
            R.drawable.ic_swap_calls_black_24dp,
    };

    private SimpleAdapter adapter;
    private List<Map<String, Object>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initData();
        initBluetooth();
        startTimer();
    }

    private BluetoothAdapter mBluetoothAdapter;

    private void initData() {

        title.setText("HRT_19D STATE");
        Intent intent = getIntent();
        isNewMeasure = intent.getBooleanExtra("isNewMeasure", false);
        if (isNewMeasure) {
            MACAddr = intent.getStringExtra("MACAddr");
        } else {
        }
        setBtnNotTouch();

        list = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("key", keys[i]);
            objectMap.put("value", values[i]);
            objectMap.put("img", imgs[i]);
            list.add(objectMap);
        }
        adapter = new SimpleAdapter(this, list, R.layout.item_data, new String[]{"key", "value", "img"}, new int[]{R.id.key, R.id.value, R.id.img});
        lvMain.setAdapter(adapter);
    }

    private void setBtnNotTouch() {
//        if (btnLeft != null && btnRight != null) {
//            btnLeft.setClickable(false);
////            btnRight.setClickable(false);
//            ivLeftbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_left_unenable));
//            ivRightbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_left_unenable));
//        }
    }

    private void setBtnTouch() {
//        if (btnLeft != null && btnRight != null) {
//            btnLeft.setClickable(true);
//            btnRight.setClickable(true);
//            ivLeftbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_left_unenable));
//            if (tvRightbtn.getText().toString().equals("启动"))
//                ivRightbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_right_normal));
//            else
//                ivRightbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_right_press));
//        }
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.back:
//                if (isNewMeasure)
//                    finishMeasure();
//                else
//                    finish();
//                break;
//            case R.id.btn_left:
//                if (State_btn_left)
//                    recreateTimer();
//                break;
//            case R.id.btn_right:
//                if (!State_btn_right) {
//                    // 预备状态转运行状态
//                    if (isBlueToothConnected)
//                        // 已经连接上蓝牙
//                        startTimer();
//                } else {
//                    // 运行状态转停止状态
//                    stopTimer();
//                }
//                break;
//        }
        finish();
    }

    /**
     * 结束实验保存数据
     */
    private void finishMeasure() {
//        new AlertDialog.Builder(this)
//                .setTitle("保存此次实验数据吗？")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
////                        saveMeasureData();
//                        finish();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                    }
//                })
//                .create()
//                .show();
        finish();
    }

    /**
     * 复位计时器
     */
    private void recreateTimer() {
        recreate();
    }

    /**
     * 停止计时
     */
    private void stopTimer() {
//        timer.stop();
//        State_btn_left = true;
//        State_btn_right = false;
//        tvRightbtn.setText("启动");
//        if (isBlueToothConnected){
//            ivRightbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_right_normal));
//            ivLeftbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_left_enable));
//            tvDistance.setTextColor(getResources().getColor(R.color.text_color_sec));
//        }else{
//            setBtnNotTouch();
//        }

//        stopBlutoothThread();
//        if (isCompleted){
//            ivRightbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_left_unenable));
//            btnRight.setClickable(false);
//        }
    }

    /**
     * 开始计时
     */
    private void startTimer() {
        // 开启线程
        if (State_btn_left == true) {
            BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(MACAddr);
            connect(bluetoothDevice);
        }
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mConnectedThread != null) {
                        mConnectedThread.start();
                        break;
                    }
                }
            }
        }.start();

//        timer.setBase(TimeUtil.convertStrTimeToLong(timer.getText().toString()));
//        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
//        timer.setFormat("0" + String.valueOf(hour) + ":%s");
//        timer.start();
//        State_btn_right = true;
//        tvRightbtn.setText("停止");
//        tvDistance.setTextColor(getResources().getColor(R.color.text_color_pri));
//        ivRightbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_right_press));
//        State_btn_left = false;
//        ivLeftbtn.setImageDrawable(getDrawable(R.drawable.shape_btn_left_unenable));

//        updateResult(MEASUREING);
    }

    /**
     * 更新测验结果
     */
    private void updateResult(int result) {
        switch (result) {
            case NOT_MEASURE:
                measureResult = NOT_MEASURE;
                list.get(4).put("value", "未测量");
                break;
            case MEASUREING:
                measureResult = MEASUREING;
                list.get(4).put("value", "测量中");
                break;
            case SUCCESS_MEASURE:
                measureResult = SUCCESS_MEASURE;
                list.get(4).put("value", "合格");
                break;
            case FAIL_MEASURE:
                measureResult = FAIL_MEASURE;
                list.get(4).put("value", "不合格");
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void printLog(String str) {
        Log.e("print", str);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        handler.removeCallbacksAndMessages(null);
        stopBlutoothThread();
        super.onDestroy();
    }

    private void stopBlutoothThread() {
        if (mConnectThread != null) {
            mConnectThread.interrupt();
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.interrupt();
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }


    private void saveMeasureData() {
        String result = "";
        switch (measureResult) {
            case 0:
                result = "未测量";
                break;
            case 1:
                result = "测量中止";
                break;
            case 2:
                result = "合格";
                break;
            case 3:
                result = "不合格";
                break;
        }

        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        theTime = simpleDateFormat.format(date);
        measureTime = timer.getText().toString();
        MeasureData data = new MeasureData(carid, carDirection, startDistance, nowDistance, result, measureTime, theTime);

        MySQLiteOpenHelper mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(this);
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
        synchronized (mySQLiteOpenHelper) {
            db.beginTransaction();

            db.execSQL(insertsql(data));

            db.setTransactionSuccessful();
            db.endTransaction();
        }

        db.close();
        mySQLiteOpenHelper.close();
        setResult(RESULT_OK);
    }

    private String insertsql(MeasureData data) {
        return "insert into " + MySQLiteOpenHelper.TABLE_NAME +
                "(" + MySQLiteOpenHelper.carId + "," +
                MySQLiteOpenHelper.carDirection + "," +
                MySQLiteOpenHelper.startDistance + "," +
                MySQLiteOpenHelper.nowDistance + "," +
                MySQLiteOpenHelper.measureResult + "," +
                MySQLiteOpenHelper.measureTime + "," +
//                MySQLiteOpenHelper.theID + "," +
                MySQLiteOpenHelper.theTime + ") values('" +
                data.getCarId() + "','" +
                data.getCarDirection() + "','" +
                data.getStartDistance() + "','" +
                data.getNowDistance() + "','" +
                data.getResult() + "','" +
                data.getMeasureTime() + "','" +
//                data.getTheID() + "','" +
                data.getTime() + "')";
    }

    private ConnectThread mConnectThread;
    public ConnectedThread mConnectedThread;

    private List<Integer> mBuffer;

    public void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null || mBluetoothAdapter.isEnabled() == false) {
            Toast.makeText(this, "可能忘了打开蓝牙？", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mBuffer = new ArrayList<Integer>();
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(MACAddr);

        connect(bluetoothDevice);
    }

    public void connect(BluetoothDevice device) {
        printLog("connect to: " + device);
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket tmp = null;
            mmDevice = device;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(App.SPP_UUID));
            } catch (IOException e) {
                printLog("create() failed" + e);
            }
            mmSocket = tmp;
        }

        public void run() {
            if (Thread.interrupted())
                return;
            printLog("BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                isBlueToothConnected = true;
                mmSocket.connect();
            } catch (IOException e) {

                printLog("unable to connect() socket " + e);
                handler.sendEmptyMessage(NOT_CONNECT);
                isBlueToothConnected = false;
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    printLog("unable to close() socket during connection failure" + e2);
                }
                return;
            }

            mConnectThread = null;

            isBlueToothConnected = true;

            // Start the connected thread
            // Start the thread to manage the connection and perform
            // transmissions
            handler.sendEmptyMessage(CONNECT_SUCCESS);

            mConnectedThread = new ConnectedThread(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                printLog("close() of connect socket failed" + e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            printLog("create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                printLog("temp sockets not created" + e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            if (Thread.interrupted()) {
                printLog("return");
                return;
            }
            printLog("BEGIN mConnectedThread");
            byte[] buffer = new byte[256];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                synchronized (this) {
                    try {
                        // Read from the InputStream
                        bytes = mmInStream.read(buffer);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        msg.what = GET_LAST_DATA;

                        bundle.putInt("data", buffer[0]);
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                        printLog(bytes + "bytes");
//                        if (isFirstData) {
//                            Message msg = new Message();
//                            msg.what = GET_FIRST_DATA;
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("data", buffer[0]);
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//                            isFirstData = false;
//                        } else {
//                            if (getMeasureTime() >= END_TIME) {
//                                // 如果已经达到限制时间
//                                Message msg = new Message();
//                                msg.what = GET_LAST_DATA;
//                                Bundle bundle = new Bundle();
//                                bundle.putInt("data", buffer[0]);
//                                msg.setData(bundle);
//                                handler.sendMessage(msg);
//                                break;
//                            } else {
//                                // 限制时间内
//                                Message msg = new Message();
//                                msg.what = GET_DATA;
//                                Bundle bundle = new Bundle();
//                                bundle.putInt("data", buffer[0]);
//                                msg.setData(bundle);
//                                handler.sendMessage(msg);
//                            }
//                        }
                        // mHandler.sendEmptyMessage(MSG_NEW_DATA);
                    } catch (IOException e) {
                        printLog("disconnected " + e);
                        handler.sendEmptyMessage(OUT_OF_CONNECTED);
                        break;
                    }
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * 获取测量时间 s为单位
     *
     * @return
     */
    private int getMeasureTime() {
        int totalss = 0;
        String string = timer.getText().toString();
        String[] split = string.split(":");
        String string2 = split[0];
        int hour = Integer.parseInt(string2);
        int Hours = hour * 3600;
        String string3 = split[1];
        int min = Integer.parseInt(string3);
        int Mins = min * 60;
        int SS = Integer.parseInt(split[2]);
        totalss = Hours + Mins + SS;
        return totalss;
    }

    private static final int GET_FIRST_DATA = 512;
    private static final int GET_LAST_DATA = 1024;
    private static final int CONNECT_SUCCESS = 2048;
    private static final int OUT_OF_CONNECTED = 4096;
    private static final int NOT_CONNECT = 9192;
    private static final int START_CONNECT = 256;
    private static final int GET_DATA = 128;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int RawData = 0;
            if (true){
                startDistance = msg.getData().getInt("data");
                if(startDistance<37){
                    DataCode = startDistance;
                }else {
                    RawData = startDistance-37;
                    switch(DataCode)
                    {
                        case 0:
                            if(RawData ==0)
                                list.get(DataCode).put("value", "NO");
                            else
                                list.get(DataCode).put("value", "YES");
                            break;
                        case 1:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "有人模式");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "直线加速");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "八字绕环");
                                    break;
                                case 3:
                                    list.get(DataCode).put("value", "高速循迹");
                                    break;
                                case 4:
                                    list.get(DataCode).put("value", "制动测试");
                                    break;
                                case 5:
                                    list.get(DataCode).put("value", "无人检查");
                                    break;
                            }
                            break;
                        case 2:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "ADC_Error");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "RES制动");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "EBS_Error");
                                    break;
                                case 3:
                                    list.get(DataCode).put("value", "Motor_Lose");
                                    break;
                                case 4:
                                    list.get(DataCode).put("value", "ROS_Lose");
                                    break;
                                case 5:
                                    list.get(DataCode).put("value", "RES_Lose");
                                    break;
                                case 6:
                                    list.get(DataCode).put("value", "EBS_Lose");
                                    break;
                                case 7:
                                    list.get(DataCode).put("value", "ESC_Lose");
                                    break;
                                case 8:
                                    list.get(DataCode).put("value", "Steer_Lose");
                                    break;
                            }
                            break;
                        case 3:
                            if(RawData == 0)
                                list.get(DataCode).put("value", "low oil pressure");
                            else
                                list.get(DataCode).put("value", "normal");
                            break;
                        case 4:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "remote stop");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "man drive");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "ready to GO");
                                    break;
                                case 4:
                                    list.get(DataCode).put("value", "AS driving");
                                    break;
                                case 6:
                                    list.get(DataCode).put("value", "stop slowly");
                                    break;
                                case 7:
                                    list.get(DataCode).put("value", "transmitter lose");
                                    break;
                                case 9:
                                    list.get(DataCode).put("value", "transmitter low battery");
                                    break;
                            }
                            break;
                        case 5:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "EBS isNot ready");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "EBS activated");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "low gas pressure");
                                    break;
                                case 3:
                                    list.get(DataCode).put("value", "EBS isNot armed");
                                    break;
                            }
                            break;
                        case 6:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "Steer isNot ready");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "Feedback is lose");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "man driving");
                                    break;
                            }
                            break;
                        case 7:
                            if(RawData ==0)
                                list.get(DataCode).put("value", "NO");
                            else
                                list.get(DataCode).put("value", "YES");
                            break;
                        case 8:
                            if(RawData ==0)
                                list.get(DataCode).put("value", "NO");
                            else
                                list.get(DataCode).put("value", "YES");
                            break;
                        case 9:
                            if(RawData ==0)
                                list.get(DataCode).put("value", "NO");
                            else
                                list.get(DataCode).put("value", "YES");
                            break;
                        case 10:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "ECU");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "EBS");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "RES");
                            }
                            break;
                        case 11:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "RES Lose");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "Controller lose");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "EBS lose");
                                    break;
                                case 3:
                                    list.get(DataCode).put("value", "Steer lose");
                                    break;
                                case 4:
                                    list.get(DataCode).put("value", "ADC error");
                                    break;
                                case 5:
                                    list.get(DataCode).put("value", "ROS lose");
                                    break;
                            }
                            break;
                        case 12:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "有人驾驶状态");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "准备");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "行驶");
                                    break;
                                case 3:
                                    list.get(DataCode).put("value", "完成");
                                    break;
                                case 4:
                                    list.get(DataCode).put("value", "制动");
                                    break;
                            }
                            break;
                        case 13:
                            list.get(DataCode).put("value", RawData*2-108);
                            break;
                        case 14:
                            list.get(DataCode).put("value", RawData*10);
                            break;
                        case 15:
                            list.get(DataCode).put("value", RawData*10);
                            break;
                        case 16:
                            list.get(DataCode).put("value", RawData*10);
                            break;
                        case 17:
                            list.get(DataCode).put("value", RawData*3);
                            break;
                        case 18:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 19:
                            switch(RawData)
                            {
                                case 0:
                                    list.get(DataCode).put("value", "STANDBY");
                                    break;
                                case 1:
                                    list.get(DataCode).put("value", "RUN");
                                    break;
                                case 2:
                                    list.get(DataCode).put("value", "PRECHARGE");
                                    break;
                                case 3:
                                    list.get(DataCode).put("value", "CHARGE");
                                    break;
                                case 4:
                                    list.get(DataCode).put("value", "CHARGE DONE");
                                    break;
                                case 5:
                                    list.get(DataCode).put("value", "ERROR");
                                    break;
                            }
                            break;
                        case 20:
                            if(RawData ==0)
                                list.get(DataCode).put("value", "NO");
                            else
                                list.get(DataCode).put("value", "YES");
                            break;
                        case 21:
                            list.get(DataCode).put("value", RawData/10.0);
                            break;
                        case 22:
                            list.get(DataCode).put("value", RawData/10.0);
                            break;
                        case 23:
                            list.get(DataCode).put("value", RawData/10.0);
                            break;
                        case 24:
                            list.get(DataCode).put("value", RawData/10.0);
                            break;
                        case 25:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 26:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 27:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 28:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 29:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 30:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 31:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 32:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 33:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 34:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 35:
                            list.get(DataCode).put("value", RawData);
                            break;
                        case 36:
                            list.get(DataCode).put("value", RawData);
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            switch (msg.what) {
                case CONNECT_SUCCESS:
                    isBlueToothConnected = true;
                    setBtnTouch();
                    Toast.makeText(MainActivity.this, "HRT_19D 准备就绪", Toast.LENGTH_SHORT).show();
                    break;
                case OUT_OF_CONNECTED:
                    isBlueToothConnected = false;
                    if (!State_btn_left)
                        setBtnNotTouch();
                    //stopTimer();
                    Toast.makeText(MainActivity.this, "HRT_19D 离开直播间", Toast.LENGTH_SHORT).show();
                    break;
                case NOT_CONNECT:
                    isBlueToothConnected = false;
                    if (!State_btn_left)
                        setBtnNotTouch();
                    //stopTimer();
                    Toast.makeText(MainActivity.this, "HRT_19D 离开直播间", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
