package com.pulsarntk.winhelper.feature.taskbarscroll;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.utils.WindowFromPoint;
import com.pulsarntk.winhelper.itf.Configurable;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

public class TaskbarScroll implements Feature {
    GlobalMouseHook mouseHook = new GlobalMouseHook();
    String NAME = "Taskbar Wheel";
    String DESCRIPTION = "Change Virtual Desktop using the mouse wheel and move the pointer over the Taskbar.";
    String TASKBAR_CLASS = "Shell_TrayWnd";
    TaskbarScroll.Settings settings = new TaskbarScroll.Settings();

    public TaskbarScroll() {
        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mouseWheel(GlobalMouseEvent event) {
                if (TASKBAR_CLASS.equals(WindowFromPoint.rootWindowClassFromPoint())) {
                    int wheel = event.getDelta();
                    if (wheel != 0) {
                        int currentDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber();
                        if (wheel > 0) {
                            VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(currentDesktop - 1);
                        } else {
                            VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(currentDesktop + 1);
                        }
                    }
                }
            }
        });
    }



    private class Settings extends JPanel {
        Settings() {
            JLabel header = new JLabel("Taskbar Wheel");
        }
    }

    public JPanel getSettingsPanel() {
        return settings;
    }

    public String getName() {
        return this.NAME;
    }

    public String getDescription() {
        return this.DESCRIPTION;
    }
}
