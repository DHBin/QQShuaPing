package com.dhb.qqshuaping.xposed;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by dhb on 2016/8/14.
 *
 */
public class Log {
    private static int DEBUG = 0;
    /**
     * @param s 输出Xposed Log 文字
     */
    public static void d(String s){
        if (check()){
            XposedBridge.log("qqSHuaPing/"+s);

        }
    }

    private static boolean check() {
        return DEBUG != 0;
    }
}
