package ru.linker.whattodo;

import java.util.EventListener;

/**
 * Created by root on 2/5/17.
 * Licensed under Attribution-NonCommercial 3.0 Unported
 */

public interface ActionListener extends EventListener {

    void actionPerformed(Integer params);

}
