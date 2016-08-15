package com.dhb.qqshuaping.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by dhb on 2016/8/14.
 *
 */
public class MainXposed implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME = "com.dhb.qqshuaping";
    public static XSharedPreferences prefs;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(QQ_PACKAGE_NAME)){
            return;
        }
        Log.d("已劫持QQ");
        BaseChatPie.init(lpparam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        prefs = new XSharedPreferences(PACKAGE_NAME,"settings");
        Log.d(prefs.getFile().getPath());
        prefs.makeWorldReadable();
    }
}
