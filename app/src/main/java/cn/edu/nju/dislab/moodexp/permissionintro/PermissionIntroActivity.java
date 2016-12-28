package cn.edu.nju.dislab.moodexp.permissionintro;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import cn.edu.nju.dislab.moodexp.MainApplication;

import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.COOLPAD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.DOZE;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.GIONEE;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.HUAWEI;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.HUAWEI_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.LENOVO;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.LENOVO_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.LETV;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.LETV_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.MEIZU;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.MEIZU_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.OPPO;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.OPPO_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.SAMSUNG;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.VIVO;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.VIVO_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.XIAOMI;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.XIAOMI_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.ZTE;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.ZTE_GOD;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.getApplicationName;
import static cn.edu.nju.dislab.moodexp.permissionintro.IntentWrapper.sIntentWrapperList;

/**
 * Created by zhantong on 2016/12/26.
 */

public class PermissionIntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String textButton="现在设置";
        for (IntentWrapper intentWrapper : sIntentWrapperList) {
            //如果本机上没有能处理这个Intent的Activity，说明不是对应的机型，直接忽略进入下一次循环。
            boolean nothingMatches = true;
            String title="";
            String message="";
            if (!intentWrapper.doesActivityExists()) continue;
            switch (intentWrapper.mType) {
                case DOZE:
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        PowerManager pm = (PowerManager) MainApplication.getContext().getSystemService(Context.POWER_SERVICE);
                        if (pm.isIgnoringBatteryOptimizations(MainApplication.getContext().getPackageName())) break;
                        nothingMatches = false;
                        title="需要忽略 " + getApplicationName() + " 的电池优化";
                        message="服务的持续运行需要 " + getApplicationName() + " 加入到电池优化的忽略名单。\n\n" +
                                "请点击『"+textButton+"』，"+
                                "在弹出的『忽略电池优化』对话框中，选择『是』。";
                    }
                    break;
                case HUAWEI:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 自动启动";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 的后台自动启动。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『自动启动管理』中，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case ZTE_GOD:
                case HUAWEI_GOD:
                    nothingMatches = false;
                    title="" + getApplicationName() + " 需要加入受保护的应用名单";
                    message="服务的持续运行需要 " + getApplicationName() + " 加入到受保护的应用名单。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『受保护应用』列表中，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case XIAOMI_GOD:
                    nothingMatches = false;
                    title="需要关闭 " + getApplicationName() + " 的神隐模式";
                    message="服务的持续运行需要 " + getApplicationName() + " 的神隐模式关闭。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的神隐模式应用列表中，点击 " + getApplicationName() + " ，然后选择『无限制』和『允许定位』。";
                    break;
                case SAMSUNG:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 的自启动";
                    message="服务的持续运行需要 " + getApplicationName() + " 在屏幕关闭时继续运行。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『智能管理器』中，点击『内存』，选择『自启动应用程序』选项卡，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case MEIZU:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 的自启动";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 的自启动。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的应用信息界面中，将『自启动』开关打开。";
                    break;
                case MEIZU_GOD:
                    nothingMatches = false;
                    title="" + getApplicationName() + " 需要在待机时保持运行";
                    message="服务的持续运行需要 " + getApplicationName() + " 在待机时保持运行。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『待机耗电管理』中，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case ZTE:
                case LETV:
                case XIAOMI:
                case OPPO:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 的自启动";
                    message="服务的持续运行需要 " + getApplicationName() + " 加入到自启动白名单。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『自启动管理』中，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case OPPO_GOD:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 在后台运行";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 在后台运行。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『纯净后台应用管控』中，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case VIVO:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 的自启动";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 的自启动。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的 i管家 中，找到『软件管理』->『自启动管理』，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case COOLPAD:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 的自启动";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 的自启动。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『酷管家』中，找到『软件管理』->『自启动管理』，取消勾选 " + getApplicationName() + "，将 " + getApplicationName() + " 的状态改为『已允许』。";
                    break;
                case VIVO_GOD:
                    nothingMatches = false;
                    title="" + getApplicationName() + " 需要在后台高耗电时允许运行";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 在后台高耗电时运行。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『后台高耗电』中，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case GIONEE:
                    nothingMatches = false;
                    title="" + getApplicationName() + " 需要加入应用自启和绿色后台白名单";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 的自启动和后台运行。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『系统管家』中，分别找到『应用管理』->『应用自启』和『绿色后台』->『清理白名单』，将 " + getApplicationName() + " 添加到白名单。";
                    break;
                case LETV_GOD:
                    nothingMatches = false;
                    title="需要禁止 " + getApplicationName() + " 被自动清理";
                    message="服务的持续运行需要禁止 " + getApplicationName() + " 被自动清理。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『应用保护』中，将 " + getApplicationName() + " 对应的开关关闭。";
                    break;
                case LENOVO:
                    nothingMatches = false;
                    title="需要允许 " + getApplicationName() + " 的后台 GPS 和后台运行";
                    message="服务的持续运行需要允许 " + getApplicationName() + " 的后台自启、后台 GPS 和后台运行。\n\n" +

                            "在弹出的『后台管理』中，分别找到『后台自启』、『后台 GPS』和『后台运行』，将 " + getApplicationName() + " 对应的开关打开。";
                    break;
                case LENOVO_GOD:
                    nothingMatches = false;
                    title="需要关闭 " + getApplicationName() + " 的后台耗电优化";
                    message="服务的持续运行需要关闭 " + getApplicationName() + " 的后台耗电优化。\n\n" +
                            "请点击『"+textButton+"』，"+
                            "在弹出的『后台耗电优化』中，将 " + getApplicationName() + " 对应的开关关闭。";
                    break;
            }
            if(!nothingMatches){
                BackgroundPermissionSlide backgroundPermissionSlide=new BackgroundPermissionSlide();
                backgroundPermissionSlide.setTitle(title);
                backgroundPermissionSlide.setMessage(message);
                backgroundPermissionSlide.setIntentWrapper(intentWrapper);
                addSlide(backgroundPermissionSlide);
            }
        }
        if(!MainApplication.isUsageStatsGranted()){
            addSlide(new UsagePermissionSlide());
        }
    }
}
