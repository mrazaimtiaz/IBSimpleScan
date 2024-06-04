package com.integratedbiometrics.ibsimplescan.pacicardlibrary;



interface PACICardCommunicatorInterface extends DisposableInterface {
    boolean IsConnected();

    ModelAPDUResponse SendAPDU(byte[] var1) throws PaciException;

    ModelAPDUResponse SendAPDU(ModelAPDUCommand var1) throws PaciException;

    void Connect() throws PaciException;

    void Disconnect() throws PaciException;

    String getReaderName();

    void BeginTransaction() throws PaciException;

    void EndTransaction(int var1) throws PaciException;

    byte[] GetATR() throws PaciException;

    byte[] Control(byte[] var1, byte[] var2) throws PaciException;
}
