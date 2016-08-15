package com.dhb.qqshuaping.xposed;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by dhb on 2016/8/14.
 * hook BaseChatPie class
 * com.tencent.mobileqq.activity.BaseChatPie
 */
public class BaseChatPie {


    private static final String CLASS_NAME = "com.tencent.mobileqq.activity.BaseChatPie";
    private static final CharSequence ORDER_SET_TIMES = "/setTimes";
    private static final CharSequence ORDER_RESET = "/reset";
    private static final String ORDER_SET_FREQUENCY = "/setF";
    private static final CharSequence ORDER_SP = "/sp";
    private static final CharSequence ORDER_SPM = "/msp";
    static EditText editText;
    static Button button;
    private static int count = 10;
    private static int frequency = 100;
    private static String text;
    private static MyHandler handler;

    /**
     * @param param 初始化
     */
    public static void init(XC_LoadPackage.LoadPackageParam param) {
        onClick(param);
        onTextChanged(param);
    }

    /**
     * @param packageParam hook onClick()方法
     */
    private static void onClick(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(CLASS_NAME, packageParam.classLoader,
                "onClick",
                "android.view.View",
                new ReplaceOnClick(packageParam));

    }

    /**
     *
     */
    private static void setFrequency() {
        try {
            frequency = Integer.parseInt(text.split(" ")[1]);
        } catch (Exception e) {
            Log.d("不能设置为非数字");
        }
        editText.setText("");
    }

    /**
     *
     */
    private static void reset() {
        count = 10;
        frequency = 100;
        editText.setText("");
    }

    private static void setTimes() {
        try {
            count = Integer.parseInt(text.split(" ")[1]);
        } catch (Exception e) {
            Log.d("不能设置为非数字");
        }
        editText.setText("");
    }

    /**
     * @param param
     * @param packageParam
     * @throws IllegalAccessException
     */
    private static void getEditText(XC_MethodHook.MethodHookParam param, XC_LoadPackage.LoadPackageParam packageParam)
            throws IllegalAccessException {
        editText = (EditText) XposedHelpers.findFirstFieldByExactType(
                XposedHelpers.findClass(CLASS_NAME,
                        packageParam.classLoader), XposedHelpers.findClass("com.tencent.widget.XEditTextEx",
                        packageParam.classLoader)).get(param.thisObject);
    }

    /**
     * @param param
     * @param packageParam
     * @throws IllegalAccessException
     */
    private static void getButton(XC_MethodHook.MethodHookParam param, XC_LoadPackage.LoadPackageParam packageParam)
            throws IllegalAccessException {
        button = (Button) XposedHelpers.findFirstFieldByExactType(
                XposedHelpers.findClass(CLASS_NAME,
                        packageParam.classLoader), XposedHelpers.findClass("com.tencent.widget.PatchedButton",
                        packageParam.classLoader)).get(param.thisObject);
    }

    /**
     * @param param
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void send(XC_MethodHook.MethodHookParam param)
            throws IllegalAccessException, InvocationTargetException {
        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
    }

    /**
     * @param packageParam
     */
    private static void onTextChanged(XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(CLASS_NAME, packageParam.classLoader, "onTextChanged",
                CharSequence.class, int.class, int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String s = param.args[0].toString();
                        if (button == null) return;
                        if (s.contains(ORDER_SET_TIMES)) {
                            button.setText("设置");
                        } else if (s.contains(ORDER_RESET)) {
                            button.setText("重置");
                        } else if (s.contains(ORDER_SET_FREQUENCY)) {
                            button.setText("设置");
                        } else if (s.contains(ORDER_SP) || s.contains(ORDER_SPM)) {
                            button.setText("刷屏");
                        }else {
                            button.setText("发送");
                        }
                    }
                });
    }

    /**
     *
     */
    static class ReplaceOnClick extends XC_MethodReplacement {
        private XC_LoadPackage.LoadPackageParam lpparam;

        ReplaceOnClick(XC_LoadPackage.LoadPackageParam lpparam) {
            this.lpparam = lpparam;
        }

        @Override
        protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
            Log.d(param.args[0].toString());
            getButton(param, lpparam);
            getEditText(param, lpparam);
            handler = new MyHandler(param);

            String viewName = param.args[0].getClass().getName();
            Log.d(viewName);
            text = editText.getText().toString();
            Log.d(text);
            //设置次数
            if (text.contains(ORDER_SET_TIMES) &&
                    viewName.equals("com.tencent.widget.PatchedButton")) {
                setTimes();
            }
            //reset
            if (text.contains(ORDER_RESET) &&
                    viewName.equals("com.tencent.widget.PatchedButton")) {
                reset();
            }
            //设置频率
            if (text.contains(ORDER_SET_FREQUENCY) &&
                    viewName.equals("com.tencent.widget.PatchedButton")) {
                setFrequency();
            }
            //刷屏
            if (text.contains(ORDER_SP) &&
                    viewName.equals("com.tencent.widget.PatchedButton")) {
                Log.d(" contains is running...");
                new SpThread().start();
            } else if (text.contains(ORDER_SPM) &&
                    viewName.equals("com.tencent.widget.PatchedButton")) { //多句刷屏
                new SpMoreThread().start();
            } else {
                //正常发送
                send(param);
            }
            return param.thisObject;
        }
    }

    /**
     *
     */
    static class MyHandler extends Handler {
        private XC_MethodHook.MethodHookParam param;

        MyHandler(XC_MethodHook.MethodHookParam param) {
            this.param = param;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(" handleMessage is running...");
            switch (msg.what) {
                case 0:
                    try {
                        editText.setText(text.substring(msg.arg2));
                        Log.d("剩余：" + msg.arg1);
                        send(param);
                        button.setText((msg.arg1 - 1) != 0 ? msg.arg1 + "" : "发送");
                    } catch (Exception e) {
                        Log.d(e.toString());
                    }
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    try {
                        editText.setText(bundle.getString("sendText", " "));
                        Log.d("剩余：" + msg.arg1);
                        send(param);
                        button.setText((msg.arg1 - 1) != 0 ? msg.arg1 + "" : "发送");
                    } catch (Exception e) {
                        Log.d(e.toString());
                    }
                    break;
            }
        }
    }

    /**
     *
     */
    static class SpThread extends Thread {
        @Override
        public void run() {
            for (int i = count; i > 0; i--) {
                try {
                    Thread.sleep(frequency);
                } catch (InterruptedException e) {
                    Log.d(e.toString());
                }
                Message message = new Message();
                message.what = 0;
                message.arg1 = i;
                message.arg2 = 4;
                handler.sendMessage(message);
            }
        }
    }

    /**
     *
     */
    static class SpMoreThread extends Thread {
        @Override
        public void run() {
            MainXposed.prefs.reload();
            String[] texts = MainXposed.prefs.getString("text", " ").split("\n");
            for (int i = count; i > 0; i--) {
                try {
                    Thread.sleep(frequency);
                } catch (InterruptedException e) {
                    Log.d(e.toString());
                }
                for (String text1 : texts) {
                    Log.d("sendText："+text1);
                    Bundle bundle = new Bundle();
                    Message message = new Message();
                    bundle.putString("sendText", text1);
                    message.what = 1;
                    message.arg1 = i;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }
    }
}

