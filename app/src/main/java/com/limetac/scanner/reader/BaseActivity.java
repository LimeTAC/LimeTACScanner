package com.limetac.scanner.reader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.limetac.scanner.R;
import com.limetac.scanner.utils.Lock;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BaseActivity extends AppCompatActivity{
    protected static final String TAG = "Example";
    private Toast _MyToast = null;

    protected static final int MSG_SHOW_WAIT = -1;
    protected static final int MSG_HIDE_WAIT = -2;
    protected static final int MSG_SHOW_TIP = -3;
    protected static final int MSG_SHOW_MSG = -4;
    protected static final int MSG_SHOW_CONFIRM = -5;
    protected static final int MSG_UPDATE_WAIT = -6;
    protected static final int MSG_SHOW_INPUT = -7;
    protected static final int MSG_SHOW_LONG_TIP = -8;

    protected static final int MSG_USER_BEG = 0;

    private ProgressDialog waitDialog = null;
    private Lock waitDialogLock = new Lock();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_WAIT:
                    doShowWaitDialog(null, (String) msg.obj);
                    break;
                case MSG_UPDATE_WAIT:
                    doUpdateWaitDialog(null, (String) msg.obj);
                    break;
                case MSG_HIDE_WAIT:
                    doHideWaitDialog();
                    break;
                case MSG_SHOW_TIP:
                    doShowTip((String) msg.obj, false);
                    break;
                case MSG_SHOW_LONG_TIP:
                    doShowTip((String) msg.obj, true);
                    break;
                case MSG_SHOW_MSG:
                    ShowMsgInfo msgInfo = (ShowMsgInfo) msg.obj;
                    if (msgInfo != null) {
                        doShowMsg(msgInfo.title, msgInfo.msg, msgInfo.listener);
                    }
                    break;
                case MSG_SHOW_CONFIRM:
                    ShowConfimInfo confimInfo = (ShowConfimInfo) msg.obj;
                    if (confimInfo != null) {
                        doShowConfim(confimInfo.msg,
                                confimInfo.okString, confimInfo.cancelString,
                                confimInfo.okListener, confimInfo.cancelListener);
                    }
                    break;
                default:
                    msgProcess(msg);
                    break;
            }
        }
    };


    /****************************************************************
     * Message Related
     ***************************************************************/
    /**
     * message processing
     *
     * @param msg
     */
    protected void msgProcess(Message msg) {

    }

    /**
     * send message
     *
     * @param what
     * @param obj
     */
    protected void sendMessage(int what, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, obj));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * send message
     *
     * @param what
     */
    protected void sendMessage(int what) {
        handler.sendMessage(handler.obtainMessage(what, null));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /****************************************************************
     * tip message
     ***************************************************************/

    /**
     * Bubble Alert
     *
     * @param msg
     */
    protected void showTip(String msg) {
        sendMessage(MSG_SHOW_TIP, msg);
    }

    private synchronized void doShowTip(String msg, boolean isLong) {
        if (_MyToast == null) {
            _MyToast = Toast.makeText(BaseActivity.this, msg,
                    isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        } else {
            _MyToast.setText(msg);
        }
        _MyToast.show();
    }

    /**
     * @param title
     * @param info
     */
    private void doShowWaitDialog(String title, String info) {
        synchronized (waitDialogLock) {
            try {
                if (waitDialog != null) {
                    waitDialog.dismiss();
                    waitDialog = null;
                }
            } catch (Exception e) {
            }
            waitDialog = ProgressDialog.show(this, title, info);
        }
    }

    private void doUpdateWaitDialog(String title, String info) {
        try {
            synchronized (waitDialogLock) {
                if (waitDialog != null) {
                    waitDialog.setTitle(title);
                    waitDialog.setMessage(info);
                }
            }
        } catch (Exception e) {

        }
    }

    private void doHideWaitDialog() {
        try {
            synchronized (waitDialogLock) {
                if (waitDialog != null) {
                    waitDialog.dismiss();
                    waitDialog = null;
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Display waiting messages
     *
     * @param msg Awaiting information
     */
    protected void showWait(String msg) {
        sendMessage(MSG_SHOW_WAIT, msg);
    }


    /**
     * Update waiting information
     *
     * @param msg Awaiting information
     */
    protected void updateWait(String msg) {
        sendMessage(MSG_UPDATE_WAIT, msg);
    }


    /**
     * Hide waiting messages
     */
    protected void hideWait() {
        sendMessage(MSG_HIDE_WAIT, null);
    }

    protected class ShowMsgInfo {
        public String title;
        public String msg;
        public DialogInterface.OnClickListener listener;
    }

    protected class ShowConfimInfo {
        public String msg;
        public String okString;
        public String cancelString;
        public DialogInterface.OnClickListener okListener;
        public DialogInterface.OnClickListener cancelListener;
    }

    public interface InputDialogInputFinishCallBack {
        void onInputDialogFinish(String text);
    }

    protected class ShowInputDialogInfo {
        public String title;
        public String info;
        public String edit;
        public InputDialogInputFinishCallBack callBack;
    }

    /**
     * Show alert messages
     *
     * @param msg
     * @param listener
     */
    private synchronized void doShowMsg(String title, String msg,
                                        DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info).setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.str_ok), listener)
                .setCancelable(false)
                .create().show();
    }

    /**
     * Show alert messages
     *
     * @param msg
     * @param listener
     */
    protected void showMsg(String title, String msg,
                           DialogInterface.OnClickListener listener) {
        ShowMsgInfo info = new ShowMsgInfo();

        info.title = title;
        info.msg = msg;
        info.listener = listener;

        sendMessage(MSG_SHOW_MSG, info);
    }

    /**
     * Show alert messages
     *
     * @param msg
     * @param listener
     */
    protected void showMsg(int title, int msg,
                           DialogInterface.OnClickListener listener) {
        showMsg(getString(title), getString(msg), listener);
    }

    /**
     * Show alert messages
     *
     * @param msg      alert message
     * @param listener Keystroke Listening Events
     */
    protected void showMsg(String msg, DialogInterface.OnClickListener listener) {
        showMsg(null, msg, listener);
    }


    /**
     * alert message
     *
     * @param msg alert message
     */
    protected void showMsg(String msg) {
        showMsg(null, msg, null);
    }



    private synchronized void doShowConfim(String msg,
                                           String okString, String cancelString,
                                           DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {

        if (okString != null && !okString.isEmpty()) {
            okString = getString(R.string.str_ok);
        }

        if (cancelString != null && !cancelString.isEmpty()) {
            cancelString = getString(R.string.str_cancel);
        }

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info).setTitle(getString(R.string.str_confirm))
                .setMessage(msg)
                .setPositiveButton(okString, okListener)
                .setNegativeButton(cancelString, cancelListener)
                .setCancelable(false)
                .create().show();
    }

    /**
     * ???????????????
     *
     * @param msg            ???????????????
     * @param okString       ????????????????????????
     * @param cancelString   ????????????????????????
     * @param okListener     ????????????????????????
     * @param cancelListener ????????????????????????
     */
    protected void showConfim(String msg,
                              String okString, String cancelString,
                              DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        ShowConfimInfo info = new ShowConfimInfo();

        info.msg = msg;
        info.okString = okString;
        info.cancelString = cancelString;
        info.okListener = okListener;
        info.cancelListener = cancelListener;

        sendMessage(MSG_SHOW_CONFIRM, info);
    }

    /**
     * ???????????????
     *
     * @param msg            ???????????????
     * @param okListener     ????????????????????????
     * @param cancelListener ????????????????????????
     */
    protected void showConfim(String msg, DialogInterface.OnClickListener okListener,
                              DialogInterface.OnClickListener cancelListener) {
        showConfim(msg, null, null, okListener, cancelListener);
    }


    /**
     * ???????????????
     *
     * @return ????????????????????????
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            return null;
        }
    }

    private long lastClickTime;

    protected synchronized boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 600) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /****************************************************************
     * ?????????????????? ??????
     ***************************************************************/

    /**
     * ??????????????????ActionBar
     *
     * @param title       ??????
     * @param subTitle    ?????????
     * @param left        ?????????
     * @param right       ?????????
     * @param left_icon   ???????????????
     * @param right_icon  ???????????????
     * @param leftListen  ???????????????
     * @param rightListen ???????????????
     * @return ????????????
     */


    /**
     * ??????????????????ActionBar
     *
     * @param title       ??????
     * @param left        ?????????
     * @param right       ?????????
     * @param left_icon   ???????????????
     * @param right_icon  ???????????????
     * @param leftListen  ???????????????
     * @param rightListen ???????????????
     * @return ????????????
     */
    protected boolean showCustomBar(String title, String left, String right,
                                    int left_icon, int right_icon, View.OnClickListener leftListen,
                                    View.OnClickListener rightListen) {
       /* return showCustomBar(title, null, left, right, left_icon, right_icon,
                leftListen, rightListen);*/
        return true;
    }

    /**
     * ??????????????????ActionBar
     *
     * @param title       ??????
     * @param left        ?????????
     * @param right       ????????? * @param leftListen ???????????????
     * @param rightListen ???????????????
     * @return ????????????
     */


    /**
     * ??????????????????ActionBar
     *
     * @param title       ??????
     * @param subTitle    ?????????
     * @param left        ?????????
     * @param right       ?????????
     * @param leftListen  ???????????????
     * @param rightListen ???????????????
     * @return ????????????
     */

    /****************************************************************
     * ?????? ??????
     ***************************************************************/
    ToneGenerator toneGenerator;// = new ToneGenerator(AudioManager.STREAM_MUSIC,99);
    public static final int SUCESS_TONE = ToneGenerator.TONE_PROP_BEEP;
    public static final int ERROR_TONE = ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK;

    /**
     * ????????????
     *
     * @param toneType ??????ToneGenerator
     */
    protected void playTone(final int toneType, final int timout) {
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(timout);
                        if (toneGenerator != null) {
                            toneGenerator.stopTone();
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }.start();
            if (toneGenerator == null) {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 99);
            }
            toneGenerator.startTone(toneType);
        } catch (Exception e) {
        }
    }

    private MediaPlayer mShootMP;

    /**
     * ????????????
     *
     * @param toneType ??????ToneGenerator
     */
    protected void playTone(final int toneType) {
        playTone(toneType, 1000);
        //toneGenerator.startTone(toneType);
    }

    /**
     * ????????????(????????????)
     */
    synchronized protected void playSound(String uriString) {
        AudioManager meng = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (mShootMP == null)
                mShootMP = MediaPlayer.create(this, Uri.parse(uriString));
            if (mShootMP != null)
                mShootMP.start();
        }
    }

    /**
     * ??????????????????
     * @param hz
     */
    public void beep(int hz, int ms) {
        /**
         * ??????????????????
         **/
        final int HEIGHT = 127;
        /**
         * 2PI
         **/
        final double TWOPI = 2 * 3.1415;

        final int SAMPLERATE = 44100;

        if (ms <= 0) {
            ms = 1;
        }
        if (hz <= 0) {
            hz = 1407;
        }

        int waveLen = SAMPLERATE / hz;
        int length = ms * SAMPLERATE / 1000;
        length = (length / waveLen) * waveLen;

        int bufferSize = AudioTrack.getMinBufferSize(SAMPLERATE,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_8BIT);

        if ((length *2) > bufferSize) {
            bufferSize = length * 2;

        }
//		ms = (bufferSize * 1000 / SAMPLERATE) + 1;
        try {
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE,
                    AudioFormat.CHANNEL_OUT_STEREO, // CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_8BIT, bufferSize, AudioTrack.MODE_STATIC);
            //???????????????
            byte[] wave = new byte[bufferSize];
            int i=0;
            for (; i < length; i++) {
                wave[i] = (byte) ((int)(HEIGHT * (1 -  Math.sin(TWOPI
                        * ((i % waveLen) * 1.00 / waveLen)))) & 0xff);
            }

            for (; i < bufferSize; i++) {
                wave[i] = (byte) ((int)(HEIGHT * (1 - Math.sin(0)))& 0xff);
            }

            if (audioTrack != null) {

                audioTrack.write(wave, 0, bufferSize);
                audioTrack.play();
                Thread.sleep(ms*2);
                audioTrack.stop();
                audioTrack.release();
            }

        } catch (Exception e) {
            Log.e(TAG,"beep error:" + e.toString() + " \nlen:"+ length + "\nmin:"+bufferSize);
        }
    }
    /**
     * ??????????????????
     */
    public void beep(int ms) {
        beep(1407, ms);
    }

    /**
     * ??????????????????
     */
    public void beep() {
        beep(1407, 50);
    }

    public void beepWarnning() {
        beep(1407, 30);
        beep(1407, 30);
        beep(1407, 30);
    }

    /****************************************************************
     * ?????? ??????
     ***************************************************************/
    private Vibrator vibrator = null;//(Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
    public static final int SHORT_VIBRATOR = 200;
    public static final int LONG_VIBRATOR = 1000;

    /**
     * ??????
     *
     * @param milliseconds ????????????
     */
    protected void playVibrator(int milliseconds) {
        if (null == this.vibrator) {
            this.vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        }


        this.vibrator.vibrate((long)milliseconds);
    }

    protected void playVibrator(long[] pattern, int repeat) {
        if (null == this.vibrator) {
            this.vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        }

        this.vibrator.vibrate(pattern, repeat);
    }

    protected void cancelVibrator() {
        if (this.vibrator != null) {
            this.vibrator.cancel();
        }

    }

//	private static PowerManager.WakeLock wakeLock = null;

    /****************************************************************
     * ???????????? ??????
     ***************************************************************/
    public void keepScreenOn(boolean on) {
        if (on) {
//			if (null == wakeLock) {
//				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//				wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.getClass().getName());
//				wakeLock.acquire();
//			}
//
            try {
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (Exception ex) {
                //Log.d("Debug", "The initialization of abnormal:" + ex.getMessage());
            }
        } else {
//			if (wakeLock != null) {
//				wakeLock.release();
//				wakeLock = null;
//			}
            try {
                getWindow().setFlags(
                        0,
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (Exception ex) {
                //Log.d("Debug", "The initialization of abnormal:" + ex.getMessage());
            }
        }
    }

    /****************************************************************
     * override ??????
     ***************************************************************/
    @Override
    public void onBackPressed() {
        this.finish();
    }

    protected boolean __Exit = false;
    @Override
    protected void onDestroy() {
        keepScreenOn(false);
        cancelVibrator();
        super.onDestroy();

        __Exit = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideWait();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME
                && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //????????????????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//????????????
    }

    /****************************************************************
     * ???????????? ??????
     ***************************************************************/
    /**
     * ??????????????????
     *
     * @return ??????
     */
    protected int getWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * ??????????????????
     *
     * @return ??????
     */
    protected int getHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * ????????????????????????
     *
     * @return Configuration.ORIENTATION_LANDSCAPE or Configuration.ORIENTATION_PORTRAIT
     */
    protected int getOrientation() {
        return getResources().getConfiguration().orientation;
    }

    /**
     * ????????????????????????
     *
     * @return Surface.ROTATION_0 or Surface.ROTATION_90 or Surface.ROTATION_180 or Surface.ROTATION_270
     */
    protected int getRotation() {
        return getWindowManager().getDefaultDisplay()
                .getRotation();
    }

    /**
     * ???????????????
     *
     * @param cls    ?????????
     * @param intent ??????
     */
    protected void showActivity(Class<?> cls, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(this, cls);
        startActivity(intent);
    }

    /**
     * ???????????????
     *
     * @param cls ?????????
     */
    protected void showActivity(Class<?> cls) {
        showActivity(cls, null);
    }

    /**
     * ??????????????????????????????
     *
     * @param cls         ?????????
     * @param intent      ??????
     * @param requestCode ????????????
     */
    protected void showActivityForResult(Class<?> cls, Intent intent, int requestCode) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }

    /**
     * ??????????????????????????????
     *
     * @param cls         ?????????
     * @param requestCode ????????????
     */
    protected void showActivityForResult(Class<?> cls, int requestCode) {
        showActivityForResult(cls, null, requestCode);
    }

    /**
     * ???????????????????????????
     *
     * @param result ??????
     * @param data   ????????????
     */
    protected void hideActivityWithResult(int result, Intent data) {
        setResult(result, data);
        finish();
    }

    /**
     * ???????????????????????????
     *
     * @param result ??????
     */
    protected void hideActivityWithResult(int result) {
        setResult(result);
        finish();
    }

    /**
     * ????????????
     */
    protected void enterFullScreen() {
        Window _window = getWindow();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //??????
        _window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        _window.setAttributes(params);
    }

    /**
     * ????????????
     */
    protected void hideTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * ??????????????????????????????
     */
    protected void hideNavigation() {
        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        _window.setAttributes(params);
    }

    /****************************************************************
     * ?????? ??????
     ***************************************************************/
    /**
     * ?????????????????????
     *
     * @param is_removale ????????????????????????
     * @return ???????????????null
     */
    public String getStoragePath(boolean is_removale) {
        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
