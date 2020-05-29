package com.pulsarntk.winhelper.feature.hotkeys.action.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.DefaultAction;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.GoNextDesktop;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.GoPreviousDesktop;
import com.pulsarntk.winhelper.settings.Setting;

public class ActionHandler {
    public static Map<String, Action> actions = new HashMap<String, Action>();
    public static Map<String, Class<?>> actionsClass = new HashMap<String, Class<?>>();
    static {
        actionsClass.put(GoNextDesktop.class.getName(), GoNextDesktop.class);
        actionsClass.put(GoPreviousDesktop.class.getName(), GoPreviousDesktop.class);

        for (Map.Entry<String, Class<?>> entry : actionsClass.entrySet()) {
            Class<?> actionClass = entry.getValue();
            try {
                Action instance = (Action) actionClass.newInstance();
                actions.put(instance.getName(), instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
