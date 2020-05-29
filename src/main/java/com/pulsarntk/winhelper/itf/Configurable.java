package com.pulsarntk.winhelper.itf;

import javax.swing.JDialog;
import javax.swing.JFrame;

public interface Configurable {
    void readFromJson();

    void writeToJson();

    JDialog getOptionsDialog();
}
