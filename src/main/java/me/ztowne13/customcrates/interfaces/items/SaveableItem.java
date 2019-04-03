package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.FileHandler;

public interface SaveableItem extends EditableItem
{
    void saveItem(FileHandler fileHandler, String prefix);

    boolean loadItem(FileHandler fileHandler, String prefix, StatusLogger statusLogger, StatusLoggerEvent itemFailure,
                     StatusLoggerEvent improperEnchant, StatusLoggerEvent improperPotion,
                     StatusLoggerEvent improperGlow, StatusLoggerEvent improperAmount);
}
