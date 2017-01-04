package cn.edu.nju.dislab.moodexp.permissionintro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import cn.edu.nju.dislab.moodexp.MainApplication;
import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/26.
 */

public class PermissionIntroActivity extends MaterialIntroActivity {
    private static String appName = MainApplication.getAppName();
    private static String textButton = MainApplication.getContext().getString(R.string.setting_now);
    private IntentBuilder[] mIntentBuilders = new IntentBuilder[]{
            new IntentBuilder()
                    .setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:" + MainApplication.getContext().getPackageName()))
                    .setTitle("设置 " + appName + " 的电池优化")
                    .setMessage("服务的持续运行需要将 " + appName + " 加入到电池优化的忽略名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『忽略电池优化』对话框中，选择『是』。")
                    .setName("DOZE"),
            new IntentBuilder()
                    .setAction("huawei.intent.action.HSM_BOOTAPP_MANAGER")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要允许 " + appName + " 的后台自动启动。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『自动启动管理』中，将 " + appName + " 对应的开关打开。")
                    .setName("HUAWEI"),
            new IntentBuilder()
                    .setPackage("com.huawei.systemmanager")
                    .setClass("com.huawei.systemmanager.optimize.process.ProtectActivity")
                    .setTitle("加入" + appName + " 到受保护的应用名单中")
                    .setMessage("服务的持续运行需要 " + appName + " 加入到受保护的应用名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『受保护应用』列表中，将 " + appName + " 对应的开关打开。")
                    .setName("HUAWEI_GOD"),
            new IntentBuilder()
                    .setPackage("com.zte.heartyservice")
                    .setClass("com.zte.heartyservice.setting.ClearAppSettingsActivity")
                    .setTitle("加入" + appName + " 到受保护的应用名单中")
                    .setMessage("服务的持续运行需要 " + appName + " 加入到受保护的应用名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『受保护应用』列表中，将 " + appName + " 对应的开关打开。")
                    .setName("ZTE_GOD"),
            new IntentBuilder()
                    .setAction("miui.intent.action.OP_AUTO_START")
                    .setCategory(Intent.CATEGORY_DEFAULT)
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要将 " + appName + " 加入到自启动白名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『自启动管理』中，将 " + appName + " 对应的开关打开。")
                    .setName("XIAOMI"),
            new IntentBuilder()
                    .setPackage("com.miui.powerkeeper")
                    .setClass("com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity")
                    .setTitle("设置 " + appName + " 的神隐模式")
                    .setMessage("服务的持续运行需要设置 " + appName + " 的神隐模式。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的神隐模式应用列表中，点击 " + appName + " ，然后选择『无限制』和『允许定位』。")
                    .setName("XIAOMI_GOD"),
            new IntentBuilder()
                    .setPackage("com.samsung.android.sm")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要 " + appName + " 在屏幕关闭时继续运行。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『智能管理器』中，点击『内存』，选择『自启动应用程序』选项卡，将 " + appName + " 对应的开关打开。")
                    .setName("SAMSUNG"),
            new IntentBuilder()
                    .setAction("com.meizu.safe.security.SHOW_APPSEC")
                    .setCategory(Intent.CATEGORY_DEFAULT)
                    .setExtraKey("packageName")
                    .setExtraValue(MainApplication.getContext().getPackageName())
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要允许 " + appName + " 的自启动。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的应用信息界面中，将『自启动』开关打开。")
                    .setName("MEIZU"),
            new IntentBuilder()
                    .setPackage("com.meizu.safe")
                    .setClass("com.meizu.safe.powerui.AppPowerManagerActivity")
                    .setTitle("设置 " + appName + " 的耗电管理")
                    .setMessage("服务的持续运行需要 " + appName + " 在待机时保持运行。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『待机耗电管理』中，将 " + appName + " 对应的开关打开。")
                    .setName("MEIZU_GOD"),
            new IntentBuilder()
                    .setPackage("com.color.safecenter")
                    .setClass("com.color.safecenter.permission.startup.StartupAppListActivity")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要将 " + appName + " 加入到自启动白名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『自启动管理』中，将 " + appName + " 对应的开关打开。")
                    .setName("OPPO"),
            new IntentBuilder()
                    .setPackage("com.color.safecenter")
                    .setClass("com.color.purebackground.PureBackgroundSettingActivity")
                    .setTitle("设置 " + appName + " 的后台运行")
                    .setMessage("服务的持续运行需要允许 " + appName + " 在后台运行。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『纯净后台应用管控』中，将 " + appName + " 对应的开关打开。")
                    .setName("OPPO_GOD"),
            new IntentBuilder()
                    .setPackage("com.iqoo.secure")
                    .setClass("com.iqoo.secure.MainActivity")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要允许 " + appName + " 的自启动。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的 i管家 中，找到『软件管理』->『自启动管理』，将 " + appName + " 对应的开关打开。")
                    .setName("VIVO"),
            new IntentBuilder()
                    .setPackage("com.vivo.abe")
                    .setClass("com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity")
                    .setTitle("设置" + appName + " 的耗电管理。")
                    .setMessage("服务的持续运行需要允许 " + appName + " 在后台高耗电时运行。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『后台高耗电』中，将 " + appName + " 对应的开关打开。")
                    .setName("VIVO_GOD"),
            new IntentBuilder()
                    .setPackage("com.gionee.softmanager")
                    .setClass("com.gionee.softmanager.MainActivity")
                    .setTitle("" + appName + " 需要加入应用自启和绿色后台白名单。")
                    .setMessage("服务的持续运行需要允许 " + appName + " 的自启动和后台运行。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『系统管家』中，分别找到『应用管理』->『应用自启』和『绿色后台』->『清理白名单』，将 " + appName + " 添加到白名单。")
                    .setName("GIONEE"),
            new IntentBuilder()
                    .setPackage("com.letv.android.letvsafe")
                    .setClass("com.letv.android.letvsafe.AutobootManageActivity")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要将 " + appName + " 加入到自启动白名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『自启动管理』中，将 " + appName + " 对应的开关打开。")
                    .setName("LETV"),
            new IntentBuilder()
                    .setPackage("com.letv.android.letvsafe")
                    .setClass("com.letv.android.letvsafe.BackgroundAppManageActivity")
                    .setTitle("设置 " + appName + " 的应用保护")
                    .setMessage("服务的持续运行需要防止 " + appName + " 被自动清理。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『应用保护』中，将 " + appName + " 对应的开关关闭。")
                    .setName("LETV_GOD"),
            new IntentBuilder()
                    .setPackage("com.yulong.android.security")
                    .setClass("com.yulong.android.seccenter.tabbarmain")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要允许 " + appName + " 的自启动。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『酷管家』中，找到『软件管理』->『自启动管理』，取消勾选 " + appName + "，将 " + appName + " 的状态改为『已允许』。")
                    .setName("COOLPAD"),
            new IntentBuilder()
                    .setPackage("com.lenovo.security")
                    .setClass("com.lenovo.security.purebackground.PureBackgroundActivity")
                    .setTitle("设置 " + appName + " 的后台 GPS 和后台运行。")
                    .setMessage("服务的持续运行需要允许 " + appName + " 的后台自启、后台 GPS 和后台运行。\n\n" +
                            "在弹出的『后台管理』中，分别找到『后台自启』、『后台 GPS』和『后台运行』，将 " + appName + " 对应的开关打开。")
                    .setName("LENOVO"),
            new IntentBuilder()
                    .setPackage("com.lenovo.powersetting")
                    .setClass("com.lenovo.powersetting.ui.Settings$HighPowerApplicationsActivity")
                    .setTitle("设置 " + appName + " 的耗电管理。")
                    .setMessage("服务的持续运行需要关闭 " + appName + " 的后台耗电优化。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『后台耗电优化』中，将 " + appName + " 对应的开关关闭。")
                    .setName("LENOVO_GOD"),
            new IntentBuilder()
                    .setPackage("com.zte.heartyservice")
                    .setClass("com.zte.heartyservice.autorun.AppAutoRunManager")
                    .setTitle("设置 " + appName + " 的自动启动")
                    .setMessage("服务的持续运行需要将 " + appName + " 加入到自启动白名单。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "在弹出的『自启动管理』中，将 " + appName + " 对应的开关打开。")
                    .setName("ZTE"),
            new IntentBuilder()
                    .setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    .setTitle("设置 " + appName + " 的统计信息权限")
                    .setMessage(appName + " 需要分析应用的使用情况。\n\n" +
                            "请点击『" + textButton + "』，" +
                            "请点击『确定』，在弹出的『查看使用情况』列表中，将 " + appName + " 对应的开关打开。")
                    .setName("USAGE")
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<IntentBuilder> availableIntentBuilders = new ArrayList<>();

        for (IntentBuilder intentBuilder : mIntentBuilders) {
            Intent intent = intentBuilder.build();
            if (isIntentExists(intent)) {
                availableIntentBuilders.add(intentBuilder);
            }
        }
        int count = 0;
        int maxCount = availableIntentBuilders.size();
        for (IntentBuilder availableIntentBuilder : availableIntentBuilders) {
            count++;
            Intent intent = availableIntentBuilder.build();
            PermissionSlide permissionSlide = new PermissionSlide();
            permissionSlide.setTitle(String.format("第 %d 步（共 %d 步）\n%s", count, maxCount, availableIntentBuilder.getTitle()));
            permissionSlide.setMessage(availableIntentBuilder.getMessage());
            permissionSlide.setIntent(intent);
            addSlide(permissionSlide);
        }
    }

    public boolean isIntentExists(Intent intent) {
        if (intent == null) {
            return false;
        }
        PackageManager pm = MainApplication.getContext().getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }
}
