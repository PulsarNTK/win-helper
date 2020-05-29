package com.pulsarntk.winhelper.feature.hotkeys.action.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.customactions.ScrollDesktop;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.ActionInfo;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.CustomAction;
import com.pulsarntk.winhelper.settings.Setting;
import org.json.JSONArray;
import org.json.JSONObject;

public class CustomActionHandler extends Setting {
    public static Map<String, CustomAction> customActions = new HashMap<String, CustomAction>();
    public static Map<String, Class<?>> customActionsClass = new HashMap<String, Class<?>>();
    static {
        registerCustomActionClass(ScrollDesktop.class);
    }

    public CustomActionHandler() {
        super("Custom Actions");
        Map<String, Object> m = this.toMap();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String name = entry.getKey();
            JSONObject json = new JSONObject((Map) entry.getValue());
            String className = json.optString("className");
            createInstanceFromClassName(className, name, false);
        }
    }

    private static void registerCustomActionClass(Class<?> clazz) {
        String name;
        ActionInfo aInfo = clazz.getAnnotation(ActionInfo.class);
        if (aInfo == null)
            name = clazz.getName();
        else
            name = aInfo.getName();
        customActionsClass.put(name, clazz);
    }

    public boolean createInstanceFromClassName(String className, String name, boolean showSettingDialog) {
        Class<?> actionClass = customActionsClass.get(className);
        if (actionClass != null) {
            try {
                CustomAction instance = (CustomAction) actionClass.newInstance();
                instance.init(newSettings(name), name);
                instance.getSetting().put("className", className);
                if (showSettingDialog)
                    instance.getSettingDialog().setVisible(true);
                customActions.put(name, instance);
                return true;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean removeCustomAction(Action action) {
        if (customActions.containsKey(action.getName())) {
            remove(action.getName());
            Setting.writeToFile();
            customActions.remove(action.getName());
            return true;
        }
        return false;
    }
}
