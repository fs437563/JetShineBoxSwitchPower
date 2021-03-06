package com.jieshine.box_switch_power;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mjk.adplayer.utils.HardwareInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 明进康电视盒子
 * 定时开关机Util
 */
public class BoxSwitchPowerUtil {
    private static BoxSwitchPowerUtil boxSwitchPowerUtil;
    private static Context mContext;

    public static BoxSwitchPowerUtil getInstance() {
        if (boxSwitchPowerUtil != null)
            return boxSwitchPowerUtil;
        else {
            boxSwitchPowerUtil = new BoxSwitchPowerUtil();
            return boxSwitchPowerUtil;
        }
    }

    //同步系统时间
    public void synSystemTime(int year, int month, int day, int hour, int minute, int second) {
        HardwareInterface.SetSystemTime(year, month, day, hour, minute, second);
    }


    private Timer startTimer;
    private TimerTask startTask;
    //设置开机时间
    public void setStartTime(Context context, String startTime) {
        mContext = context;
        Date date = strToDateLong(startTime);
        if (date != null) {
            if (startTimer != null) {
                startTimer.cancel();
                startTimer=null;
                startTask.cancel();
                startTask=null;
            }
             startTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            };
            startTimer = new Timer(true);
            startTimer.schedule(startTask, date);
        } else {
            Toast.makeText(context, "开机时间解析异常", Toast.LENGTH_LONG).show();
        }
    }


    private Timer closeTimer;
    private  TimerTask closeTask;
    //设置关机时间 多次同时间调用 会导致关机后立即开机
    public void setCloseTime(Context context, String closeTime) {
        mContext = context;
        Date date = strToDateLong(closeTime);
        if (date != null) {
            if (closeTimer != null) {
                closeTimer.cancel();
                closeTimer=null;
                closeTask.cancel();
                closeTask=null;
            }
            closeTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(2);
                }
            };
            closeTimer = new Timer(true);
            closeTimer.schedule(closeTask, date);
        } else {
            Toast.makeText(context, "关机时间解析异常", Toast.LENGTH_LONG).show();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("tyl", "systemTime=" + getDate(System.currentTimeMillis()));
            if (msg.what == 1) {
                openPower();
            } else if (msg.what == 2) {
                closePower();
            }
            super.handleMessage(msg);
        }
    };

    private String getDate(long time) {
        Date d = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(d);
    }

    public void openPower(Context context) {
        HardwareInterface.ShutUp(context);
    }

    public void closePower(Context context) {
        HardwareInterface.ShutDown(context);
    }

    //关机
    private void closePower() {
        Log.e("tyl", "closePower");
        if (mContext != null) {
            HardwareInterface.ShutDown(mContext);
        }
    }

    //开机
    private void openPower() {
        Log.e("tyl", "openPower");
        if (mContext != null) {
            HardwareInterface.ShutUp(mContext);
        }
    }

    private Date strToDateLong(String strDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("tyl", "ParseException=" + e.getMessage());
            return null;
        }
    }

//    private void moveTaskToBack() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//       mContext. startActivity(intent);
//    }
}
