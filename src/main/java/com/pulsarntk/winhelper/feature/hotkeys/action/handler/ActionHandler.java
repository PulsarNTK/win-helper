package com.pulsarntk.winhelper.feature.hotkeys.action.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.DefaultAction;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.GoNextDesktop;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.GoPreviousDesktop;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.MoveWindowNextDesktop;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.MoveWindowNextDesktopNGo;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.MoveWindowPreviousDesktop;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.MoveWindowPreviousDesktopNGo;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.PinApp;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.PinWindow;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.UnPinApp;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.UnPinWindow;
import com.pulsarntk.winhelper.settings.Setting;

public class ActionHandler {
    public static Map<String, Action> actions = new HashMap<String, Action>();
    public static Map<String, Class<?>> actionsClass = new HashMap<String, Class<?>>();
    static {
        actionsClass.put(GoNextDesktop.class.getName(), GoNextDesktop.class);
        actionsClass.put(GoPreviousDesktop.class.getName(), GoPreviousDesktop.class);
        actionsClass.put(PinWindow.class.getName(), PinWindow.class);
        actionsClass.put(UnPinWindow.class.getName(), UnPinWindow.class);
        actionsClass.put(PinApp.class.getName(), PinApp.class);
        actionsClass.put(UnPinApp.class.getName(), UnPinApp.class);
        actionsClass.put(MoveWindowNextDesktop.class.getName(), MoveWindowNextDesktop.class);
        actionsClass.put(MoveWindowNextDesktopNGo.class.getName(), MoveWindowNextDesktopNGo.class);
        actionsClass.put(MoveWindowPreviousDesktop.class.getName(), MoveWindowPreviousDesktop.class);
        actionsClass.put(MoveWindowPreviousDesktopNGo.class.getName(), MoveWindowPreviousDesktopNGo.class);

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
