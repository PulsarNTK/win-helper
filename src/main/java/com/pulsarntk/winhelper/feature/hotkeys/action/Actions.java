package com.pulsarntk.winhelper.feature.hotkeys.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.pulsarntk.winhelper.feature.hotkeys.action.actions.DefaultAction;
import com.pulsarntk.winhelper.feature.hotkeys.action.handler.ActionHandler;
import com.pulsarntk.winhelper.feature.hotkeys.action.handler.CustomActionHandler;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.CustomAction;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.Action;

public class Actions {
    public static ActionHandler actionHandler = new ActionHandler();
    public static CustomActionHandler customActionHandler = new CustomActionHandler();
    public static Action DEFAULT_ACTION = new DefaultAction();

    public static Map<String, Action> getActions() {
        return Stream.concat(ActionHandler.actions.entrySet().stream(), CustomActionHandler.customActions.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Action> getActionsSorted() {
        return new TreeMap<String, Action>(getActions());
    }

    public static Action getActionOrNull(String name) {
        Action a = getActions().get(name);
        if (a == null)
            return null;
        return a;
    }

    public static Action getActionOrDefault(String name) {
        Action a = getActions().get(name);
        if (a == null)
            return DEFAULT_ACTION;
        return a;
    }
}
