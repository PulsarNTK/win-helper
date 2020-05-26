package com.pulsarntk.winhelper.feature.desktopbackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.pulsarntk.winhelper.itf.Configurable;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor.VirtualDesktopListener;
import com.pulsarntk.winhelper.settings.Setting;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser.MSG;
import org.json.JSONObject;

public class DesktopBackground implements Feature {
    private Settings settings = new Settings();
    private VirtualDesktopListener listener;

    public DesktopBackground() {
        listener = new VirtualDesktopListener(new VirtualDesktopListener.Listener() {
            @Override
            public void onMessage(MSG msg) {
                if (msg.message == VirtualDesktopListener.CURRENT_VIRTUAL_DESKTOP_CHANGED) {
                    setDesktopBackground(settings.backgrounds.get(VirtualDesktopAccessor.INSTANCE.GetCurrentDesktopNumber()).setting.optString("path"));
                }
            }
        });
    }

    @Override
    public JFrame getSettingsFrame() {
        return settings.frame;
    }


    @Override
    public String getName() {
        return "Desktop Background";
    }

    @Override
    public String getDescription() {
        return "Different background images for each desktop";
    }

    private static class Settings {
        private JFrame frame = new JFrame("Desktop Background Settings");
        private JPanel backgroundPanel = new JPanel(new GridLayout(0, 1));
        private List<FileSelector> backgrounds = new ArrayList<FileSelector>();
        private Setting setting = new Setting("Desktop Background");

        public Settings() {
            for (int i = 0; i < VirtualDesktopAccessor.INSTANCE.GetDesktopCount(); i++) {
                FileSelector temp = new FileSelector(i, setting.newSettings(Integer.toString(i)));
                backgrounds.add(temp);
                backgroundPanel.add(temp.panel);
            }
            JButton plus = new JButton("+");
            JButton minus = new JButton("-");
            JPanel controlPanel = new JPanel();
            plus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileSelector temp = new FileSelector(backgrounds.size(), setting.newSettings(Integer.toString(backgrounds.size())));
                    backgrounds.add(temp);
                    backgroundPanel.add(temp.panel);
                    frame.pack();
                }
            });
            minus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    backgroundPanel.remove(backgrounds.size() - 1);
                    backgrounds.remove(backgrounds.size() - 1);
                    frame.pack();
                }
            });
            controlPanel.add(plus);
            controlPanel.add(minus);
            frame.getContentPane().add(backgroundPanel, BorderLayout.NORTH);
            frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
            frame.setResizable(false);
            frame.pack();
        }

        private class FileSelector {
            public JButton button;
            public JTextField textField;
            public JTextField label;
            public JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
            public int i;
            public Setting setting;
            JFileChooser imageFileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG & JPG Images", "png", "jpg");

            public FileSelector(int i, Setting setting) {
                this.setting = setting;
                this.label = new JTextField(setting.optString("label"));
                this.textField = new JTextField(setting.optString("path"));
                this.button = new JButton("...");
                this.i = i;

                this.label.setPreferredSize(new Dimension(92, 24));
                this.textField.setPreferredSize(new Dimension(256, 24));
                this.button.setPreferredSize(new Dimension(32, 24));

                panel.add(this.label);
                panel.add(this.textField);
                panel.add(this.button);

                this.button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (imageFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            setting.put("path", imageFileChooser.getSelectedFile().getPath());
                            textField.setText(imageFileChooser.getSelectedFile().getPath());
                        }
                    }
                });
                this.textField.addKeyListener(new KeyListener() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        setting.put("path", textField.getText());
                    }

                    @Override
                    public void keyTyped(KeyEvent e) {
                    }
                });
                this.label.addKeyListener(new KeyListener() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        setting.put("label", label.getText());
                    }

                    @Override
                    public void keyTyped(KeyEvent e) {
                    }
                });
                imageFileChooser.setFileFilter(filter);
            }
        }
    }

    public boolean setDesktopBackground(String imagePath) {
        File f = new File(imagePath);
        if (f.exists() && !f.isDirectory()) {
            User32Extra.INSTANCE.SystemParametersInfo(User32Extra.SPI_SETDESKWALLPAPER, 0, imagePath, User32Extra.SPIF_UPDATEINIFILE | User32Extra.SPIF_SENDWININICHANGE);
            return true;
        }
        return false;
    }

    @Override
    public void enable() {
        VirtualDesktopAccessor.INSTANCE.RegisterPostMessageHook(listener.threadId, 0);
    }

    @Override
    public void disable() {
        VirtualDesktopAccessor.INSTANCE.UnregisterPostMessageHook(listener.threadId);
    }
}
