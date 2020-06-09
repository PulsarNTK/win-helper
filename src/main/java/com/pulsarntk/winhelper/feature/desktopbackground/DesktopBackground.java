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
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.pulsarntk.winhelper.feature.desktopbackground.DesktopBackground.Settings.Background;
import com.pulsarntk.winhelper.itf.Configurable;
import com.pulsarntk.winhelper.itf.Feature;
import com.pulsarntk.winhelper.lib.User32Extra;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor;
import com.pulsarntk.winhelper.lib.VirtualDesktopAccessor.VirtualDesktopListener;
import com.pulsarntk.winhelper.settings.Setting;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser.MSG;
import org.json.JSONObject;
import java.awt.image.BufferedImage;


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


    public static BufferedImage getBackground(int index) {
        if (index < 0) {
            return getBackground(0);
        }
        if (index < Settings.backgrounds.size()) {
            return Settings.backgrounds.get(index).image;
        } else {
            return (getBackground(index % Settings.backgrounds.size() - 1));
        }
    }

    @Override
    public JDialog getOptionsDialog() {
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

    public static class Settings {
        private JDialog frame = new JDialog((JFrame) null, "Desktop Background Settings");
        private JPanel backgroundPanel = new JPanel(new GridLayout(0, 1));
        public static List<Background> backgrounds = new ArrayList<Background>();
        private Setting setting = new Setting("Desktop Background");

        public Settings() {
            for (int i = 0; i < setting.toMap().size(); i++) {
                Background temp = new Background(i, setting.newSettings(Integer.toString(i)));
                backgrounds.add(temp);
                backgroundPanel.add(temp.panel);
            }
            JButton plus = new JButton("+");
            JButton minus = new JButton("-");
            JPanel controlPanel = new JPanel();
            plus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Background temp = new Background(backgrounds.size(), setting.newSettings(Integer.toString(backgrounds.size())));
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
                    setting.remove(Integer.toString(backgrounds.size()));
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

        public class Background {
            public JButton button;
            public JTextField textField;
            public JTextField label;
            public JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
            public int i;
            public Setting setting;
            public String path = "";
            JFileChooser imageFileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG & JPG Images", "png", "jpg");
            public File imageFile;
            public BufferedImage image;

            public Background(int i, Setting setting) {
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


                path = this.setting.optString("path");
                imageFile = new File(this.setting.optString("path"));
                if (imageFile.isFile()) {
                    try {
                        image = ImageIO.read(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                }

                this.button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (imageFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            setting.put("path", imageFileChooser.getSelectedFile().getPath());
                            textField.setText(imageFileChooser.getSelectedFile().getPath());
                            path = imageFileChooser.getSelectedFile().getPath();
                            imageFile = new File(path);
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
        listener.register();
    }

    @Override
    public void disable() {
        listener.unRegister();
    }

    @Override
    public void readFromJson() {
    }

    @Override
    public void writeToJson() {
    }
}
