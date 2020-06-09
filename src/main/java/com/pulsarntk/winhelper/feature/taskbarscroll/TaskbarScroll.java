package com.pulsarntk.winhelper.feature.taskbarscroll;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.lib.Kernel32Extra;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.utils.WindowFromPoint;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.lwjgl.system.windows.User32;
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
    GlobalMouseAdapter mouseListener = new GlobalMouseAdapter() {
        @Override
        public void mouseWheel(GlobalMouseEvent event) {
            if (TASKBAR_CLASS.equals(WindowFromPoint.rootWindowClassFromPoint())) {
                int wheel = event.getDelta();
                if (wheel != 0) {
                    int currentDesktop = VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber();
                    HWND target = User32Extra.INSTANCE.FindWindow("Shell_TrayWnd", "");
                    int mainThreadId = Kernel32Extra.INSTANCE.GetCurrentThreadId();
                    int foreThreadId = User32Extra.INSTANCE.GetWindowThreadProcessId(User32Extra.INSTANCE.GetForegroundWindow(), null);
                    int targetThreadId = User32Extra.INSTANCE.GetWindowThreadProcessId(target, null);
                    User32Extra.INSTANCE.AttachThreadInput(mainThreadId, foreThreadId, true);
                    User32Extra.INSTANCE.AttachThreadInput(foreThreadId, targetThreadId, true);
                    User32Extra.INSTANCE.SetForegroundWindow(target);
                    User32Extra.INSTANCE.AttachThreadInput(mainThreadId, foreThreadId, false);
                    User32Extra.INSTANCE.AttachThreadInput(foreThreadId, targetThreadId, false);
                    if (wheel > 0) {
                        VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(currentDesktop - 1);
                    } else {
                        VirtualDesktopAccessor.INSTANCE.GoToDesktopNumber(currentDesktop + 1);
                    }
                }
            }
        }
    };

    public TaskbarScroll() {
    }



    private class Settings extends JPanel {
        Settings() {
            JLabel header = new JLabel("Taskbar Wheel");
        }
    }

    public String getName() {
        return this.NAME;
    }

    public String getDescription() {
        return this.DESCRIPTION;
    }

    @Override
    public void enable() {
        mouseHook.addMouseListener(mouseListener);
    }

    @Override
    public void disable() {
        mouseHook.removeMouseListener(mouseListener);
    }

    @Override
    public void readFromJson() {
    }

    @Override
    public void writeToJson() {
    }

    @Override
    public JDialog getOptionsDialog() {
        return null;
    }
}
