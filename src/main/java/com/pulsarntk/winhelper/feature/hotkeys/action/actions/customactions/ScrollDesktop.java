package com.pulsarntk.winhelper.feature.hotkeys.action.actions.customactions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.ActionInfo;
import com.pulsarntk.winhelper.feature.hotkeys.action.itf.CustomAction;
import com.pulsarntk.winhelper.settings.Setting;
import com.pulsarntk.winhelper.utils.RegisterHotkey.HotkeyListener;
import com.pulsarntk.winhelper.utils.RegisterHotkey.KEY;
import com.sun.jna.platform.win32.WinUser.MSG;
import org.json.JSONObject;

@ActionInfo(getName = "Scroll Desktop")
public class ScrollDesktop implements CustomAction {
    JDialog dialog = new JDialog((JFrame) null, getName(), true);
    JPanel panel = new JPanel();
    JTextField xLabel = new JTextField();
    JComboBox<KEY> keyComboBox = new JComboBox<KEY>(KEY.values());
    JButton okButton = new JButton("Ok");
    HotkeyListener listener = new HotkeyListener() {
        @Override
        public void onKeyPress(MSG msg) {
            System.out.println(Long.toString(System.currentTimeMillis()));
        }
    };
    String name;
    int xInt = 0;
    String xString = "";
    Setting setting;


    public ScrollDesktop() {
    }

    private void initDialog() {
        panel.add(xLabel);
        panel.add(keyComboBox);
        panel.add(okButton);
        dialog.getContentPane().add(panel);
        dialog.pack();
        xLabel.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent arg0) {

            }

            @Override
            public void keyReleased(KeyEvent arg0) {
                setting.put("xString", xString);
                writeToJson();
            }

            @Override
            public void keyTyped(KeyEvent arg0) {

            }
        });
        keyComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                writeToJson();
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
    }

    @Override
    public void readFromJson() {
        if (!setting.isNull("Key")) {
            keyComboBox.setSelectedItem(setting.getString("Key"));
        }
        xLabel.setText(setting.optString("String"));
    }

    @Override
    public void writeToJson() {
        setting.put("Key", keyComboBox.getSelectedItem());
        setting.put("String", xLabel.getText());
    }

    @Override
    public JDialog getOptionsDialog() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public HotkeyListener getListener() {
        return listener;
    }

    @Override
    public JDialog getSettingDialog() {
        return this.dialog;
    }

    @Override
    public CustomAction init(Setting setting, String name) {
        this.setting = setting;
        this.name = name;
        readFromJson();
        initDialog();
        return this;
    }

    @Override
    public Setting getSetting() {
        return this.setting;
    }

}
