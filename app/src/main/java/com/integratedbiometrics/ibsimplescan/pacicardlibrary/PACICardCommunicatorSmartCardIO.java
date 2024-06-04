package com.integratedbiometrics.ibsimplescan.pacicardlibrary;


import com.telpo.tps550.api.reader.SmartCardReader;

import java.nio.charset.StandardCharsets;


class PACICardCommunicatorSmartCardIO implements PACICardCommunicatorInterface, DisposableInterface {
    //TerminalFactory TerminalFac = TerminalFactory.getDefault();
 //   CardTerminals Terminals;
    boolean Disposed = false;
//    List<CardTerminal> Readers;
 //   CardTerminal Terminal;
 //   Card card;
//    CardChannel Channel;
    boolean Exclusive = false;


    private SmartCardReader mReader;

    protected void finalize() throws Throwable {
        this.Dispose();
        super.finalize();
    }

    public PACICardCommunicatorSmartCardIO(String var1,SmartCardReader reader) throws PaciException {
        mReader = reader;
    //    this.Terminals = this.TerminalFac.terminals();
        try {
         //   this.Readers = this.Terminals.list();
         //   Iterator var2 = this.Readers.iterator();

      /*      while(var2.hasNext()) {
                CardTerminal var3 = (CardTerminal)var2.next();
                if (var3.getName().compareTo(var1) == 0) {
                    this.Terminal = var3;
                    this.Terminal.isCardPresent();
                    return;
                }
            }*/
        } catch (Exception var4) {
        }

        throw new PaciException("Requested reader was not found");
    }

    public boolean IsConnected() {
        try {
          //  return this.Channel == null ? false : this.Terminal.isCardPresent();
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public ModelAPDUResponse SendAPDU(byte[] var1) throws PaciException {
        try {
            mReader.transmit(var1);
          /*  CommandAPDU var2 = new CommandAPDU(var1);

            try {
                ResponseAPDU var3 = this.Channel.transmit(var2);
                return new ModelAPDUResponse(var3.getBytes());
            } catch (CardException var5) {
                if (var5.getCause() != null && var5.getCause().toString().contains("SCARD_W_RESET_CARD")) {
                    this.Connect();
                    ResponseAPDU var4 = this.Channel.transmit(var2);
                    return new ModelAPDUResponse(var4.getBytes());
                } else {
                    throw var5;
                }
            }*/
        } catch (Exception var6) {
            throw new PaciException("Could not transmit data to card");
        }
        return null;
    }

    public ModelAPDUResponse SendAPDU(ModelAPDUCommand var1) throws PaciException {
        return this.SendAPDU(var1.ToArray());
    }

    public void Connect() throws PaciException {
        mReader.iccPowerOn();
      /*  try {
            this.card = this.Terminal.connect("*");
            this.Channel = this.card.getBasicChannel();
        } catch (Exception var2) {
            throw new PaciException("Error in connecting the card");
        }*/
    }

    public void Disconnect() throws PaciException {
        mReader.iccPowerOff();
   /*     try {
            if (this.card != null) {
                this.card.disconnect(false);
            }

            this.Channel = null;
        } catch (Exception var2) {
            throw new PaciException("Error in connecting the card");
        }*/
    }

    public String getReaderName() {
        return "";
    }

    public void BeginTransaction() throws PaciException {
    /*    try {
            if (!this.Exclusive) {
                this.card.beginExclusive();
                this.Exclusive = true;
            }

        } catch (CardException var4) {
            if (var4.getCause() != null && var4.getCause().toString().contains("SCARD_W_RESET_CARD")) {
                this.Connect();

                try {
                    this.card.beginExclusive();
                    this.Exclusive = true;
                    return;
                } catch (Exception var3) {
                }
            }

            throw new PaciException("Could not get access to card");
        }*/
    }

    public void EndTransaction(int var1) throws PaciException {
        try {
            if (this.Exclusive) {
               // this.card.endExclusive();
                this.Exclusive = false;
            }
        } catch (IllegalStateException var3) {
            this.Exclusive = false;
        } catch (Exception var4) {
            throw new PaciException("Could not get access to card");
        }

    }

    public byte[] GetATR() throws PaciException {
        return this.mReader.getATRString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] Control(byte[] var1, byte[] var2) throws PaciException {
        try {
            int var3 = 0;
            byte[] var4 = var1;
            int var5 = var1.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte var7 = var4[var6];
                if (var7 < 128) {
                    var3 += var7;
                } else {
                    var3 += 256 + var7;
                }
            }

           // return this.card.transmitControlCommand(var3, var2);
        } catch (Exception var8) {
            throw new PaciException("Could not transmit control data to card");
        }
        return var1;
    }

    public void Dispose() {
        if (!this.Disposed) {
            this.Disposed = true;

            try {
                if (this.Exclusive) {
                    this.EndTransaction(0);
                }

                this.Disconnect();
            } catch (Exception var2) {
            }
        }

    }
}
