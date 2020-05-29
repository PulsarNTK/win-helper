package com.pulsarntk.winhelper.feature.hotkeys.action.itf;

import javax.swing.JDialog;
import com.pulsarntk.winhelper.itf.Configurable;
import com.pulsarntk.winhelper.settings.Setting;

public interface CustomAction extends Action, Configurable {

    public Setting getSetting();

    public JDialog getSettingDialog();

    public CustomAction init(Setting setting, String name);

    @Override
    public default boolean isCustom() {
        return true;
    }

}
