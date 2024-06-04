//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.integratedbiometrics.ibsimplescan.pacicardlibrary;


import java.util.ArrayList;
import java.util.List;

abstract class CardAndReaderEventsAbstract {
    List<CardAndReaderEventsAbstract> listeners = new ArrayList();

    CardAndReaderEventsAbstract() {
    }

    void AddEventHandler(CardAndReaderEventsAbstract var1) {
        this.listeners.add(var1);
    }

    boolean RemoveHandler(CardAndReaderEventsAbstract var1) {
        return this.listeners.remove(var1);
    }

    void ReaderChangeEvent(String[] var1) {
        for(int var2 = 0; var2 < this.listeners.size(); ++var2) {
            ((CardAndReaderEventsAbstract)this.listeners.get(var2)).ReaderChangeEvent(var1);
        }

    }

    void CardConnectionEvent(int var1, byte[] var2) {
        for(int var3 = 0; var3 < this.listeners.size(); ++var3) {
            ((CardAndReaderEventsAbstract)this.listeners.get(var3)).CardConnectionEvent(var1, var2);
        }

    }

    void CardDisconnectionEvent(int var1) {
        for(int var2 = 0; var2 < this.listeners.size(); ++var2) {
            ((CardAndReaderEventsAbstract)this.listeners.get(var2)).CardDisconnectionEvent(var1);
        }

    }
}
