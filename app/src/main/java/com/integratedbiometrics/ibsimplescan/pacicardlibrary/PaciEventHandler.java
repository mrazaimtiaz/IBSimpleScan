package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

public interface PaciEventHandler {
    void ReaderChangeEvent();

    void CardConnectionEvent(int var1);

    void CardDisconnectionEvent(int var1);
}
