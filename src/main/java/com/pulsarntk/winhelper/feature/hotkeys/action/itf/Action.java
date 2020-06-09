package com.pulsarntk.winhelper.feature.hotkeys.action.itf;

import javax.swing.JDialog;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;

public interface Action {

    public String getName();

    public HotkeyListener getListener();

    default public boolean isCustom() {
        return false;
    }

    default public void setKey(KEY key) {
    }
}
