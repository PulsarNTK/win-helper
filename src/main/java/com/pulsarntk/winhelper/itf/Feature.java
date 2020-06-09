package com.pulsarntk.winhelper.itf;

import com.pulsarntk.winhelper.itf.Configurable;

public interface Feature extends Configurable {
    public String getName();

    public String getDescription();

    public void enable();

    public void disable();
}
