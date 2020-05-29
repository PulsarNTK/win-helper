package com.pulsarntk.winhelper.feature.hotkeys.action.itf;

import javax.swing.JDialog;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;

public interface Action {

    public String getName();

    public HotkeyListener getListener();

    default public boolean isCustom() {
        return false;
    }

}
