//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

import android.content.Context;

import com.telpo.tps550.api.reader.SmartCardReader;

import java.util.Iterator;

final class CardAndReaderEventsSmartCardIO extends CardAndReaderEventsAbstract implements DisposableInterface {
  //  TerminalFactory TerminalFac;
  //  CardTerminals Terminals;
    boolean Disposed;
    Thread ReaderChangeThread;
    Thread CardChangeThread;
   // List<CardTerminal> Readers;
  //  ConcurrentHashMap<CardTerminal, Integer> ReadersStates;


    private SmartCardReader reader;

    protected void finalize() throws Throwable {
        this.Dispose();
        super.finalize();
    }

    public CardAndReaderEventsSmartCardIO(Context context) {
        this.Initialize(context);
    }

    public void Initialize(Context context) {

        reader = new SmartCardReader(context);
        //this.ReadersStates = new ConcurrentHashMap();
       // this.TerminalFac = TerminalFactory.getDefault();
        //this.Terminals = this.TerminalFac.terminals();
        this.RunCheckForTerminalChanges();
        this.Disposed = false;
    }

    private void RunCheckForTerminalChanges() {
        this.ReaderChangeThread = new Thread(new Runnable() {
            public void run() {
                Thread var2;
                try {
                    CardAndReaderEventsSmartCardIO.this.CheckForTerminalChanges();
                } catch (PaciException var3) {
                    var2 = Thread.currentThread();
                    var2.getUncaughtExceptionHandler().uncaughtException(var2, var3);
                } catch (Exception var4) {
                    var2 = Thread.currentThread();
                    var2.getUncaughtExceptionHandler().uncaughtException(var2, new PaciException("Unable to detect thread"));
                }

            }
        });
        this.ReaderChangeThread.start();
    }

    void CheckForTerminalChanges() throws PaciException {
        String[] var1 = null;

        try {
            Thread.sleep(200L);
        } catch (InterruptedException var27) {
        }

        boolean var2 = true;

        int var3;
        Iterator var4;
       // CardTerminal var5;
        this.reader.open(1);
        reader.iccPowerOn();
        while(!this.Disposed && var2) {
          //  try {
             /*   if (this.Readers.size() > 0) {
                    var2 = false;
                    var1 = new String[this.Readers.size()];
                    var3 = 0;

                    for(var4 = this.Readers.iterator(); var4.hasNext(); ++var3) {
                        var5 = (CardTerminal)var4.next();
                        var1[var3] = var5.getName();
                        this.ReadersStates.put(var5, 0);
                    }
                } else {
                    Thread.sleep(1000L);
                }*/
         /*   } catch (CardException var31) {
                if (var31.getCause() == null || !var31.getCause().toString().contains("SCARD_E_NO_READERS_AVAILABLE")) {
                    throw new PaciException("Unknown error");
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var26) {
                    return;
                }
            } catch (InterruptedException var32) {
                return;
            } catch (Exception var33) {
                throw new PaciException("Unknown error");
            }*/
        }

       /* try {
            Thread.sleep(200L);
        } catch (InterruptedException var25) {
        }

        this.ReaderChangeEvent(var1);

        try {
            var3 = 0;
            var4 = this.Readers.iterator();

            label189:
            while(true) {
                while(true) {
                    if (!var4.hasNext()) {
                        break label189;
                    }

                    var5 = (CardTerminal)var4.next();
                    if (!var5.isCardPresent()) {
                        this.CardDisconnectionEvent(var3);
                        break;
                    }

                    try {
                        this.ReadersStates.put(var5, 1);
                        Card var6 = var5.connect("*");
                        byte[] var7 = var6.getATR().getBytes();
                        var6.disconnect(false);
                        this.CardConnectionEvent(var3, var7);
                        break;
                    } catch (CardException var29) {
                    }
                }

                ++var3;
            }
        } catch (CardException var30) {
            return;
        }

        boolean var34 = false;

        while(!this.Disposed) {
            try {
                this.Readers = this.Terminals.list();
                this.Terminals.waitForChange(1000L);
                var34 = false;
                boolean var35 = false;
                Iterator var40;
                CardTerminal var42;
                if (var1 != null && this.Readers.size() == var1.length) {
                    List var37 = Arrays.asList(var1);
                    var40 = this.Readers.iterator();

                    while(var40.hasNext()) {
                        var42 = (CardTerminal)var40.next();
                        if (!var37.contains(var42.getName())) {
                            var35 = true;
                            break;
                        }
                    }
                } else {
                    var35 = true;
                }

                int var39;
                if (var35) {
                    this.Readers = this.Terminals.list();
                    var1 = new String[this.Readers.size()];
                    var39 = 0;
                    this.ReadersStates.clear();

                    for(var40 = this.Readers.iterator(); var40.hasNext(); ++var39) {
                        var42 = (CardTerminal)var40.next();
                        this.ReadersStates.put(var42, 0);
                        var1[var39] = var42.getName();
                    }

                    this.ReaderChangeEvent(var1);
                    int var43 = 0;

                    for(Iterator var45 = this.Readers.iterator(); var45.hasNext(); ++var43) {
                        CardTerminal var46 = (CardTerminal)var45.next();
                        if (var46.isCardPresent()) {
                            try {
                                Card var48 = var46.connect("*");
                                byte[] var49 = var48.getATR().getBytes();
                                var48.disconnect(false);
                                this.ReadersStates.put(var46, 1);
                                this.CardConnectionEvent(var43, var49);
                            } catch (CardException var24) {
                                this.CardDisconnectionEvent(var39);
                                this.ReadersStates.put(var46, 1);
                            }
                        } else if (!var46.isCardPresent()) {
                            this.ReadersStates.put(var46, 0);
                            this.CardDisconnectionEvent(var43);
                        }
                    }
                } else {
                    var39 = 0;

                    for(var40 = this.Readers.iterator(); var40.hasNext(); ++var39) {
                        var42 = (CardTerminal)var40.next();
                        if (var42.isCardPresent() && (Integer)this.ReadersStates.get(var42) == 0) {
                            try {
                                Card var44 = var42.connect("*");
                                byte[] var47 = var44.getATR().getBytes();
                                var44.disconnect(false);
                                this.ReadersStates.put(var42, 1);
                                this.CardConnectionEvent(var39, var47);
                            } catch (CardException var23) {
                                this.CardDisconnectionEvent(var39);
                                this.ReadersStates.put(var42, 1);
                            }
                        } else if (!var42.isCardPresent() && (Integer)this.ReadersStates.get(var42) == 1) {
                            this.ReadersStates.put(var42, 0);
                            this.CardDisconnectionEvent(var39);
                        }
                    }
                }
            } catch (CardException var28) {
                if (!var28.getCause().getMessage().contains("SCARD_E_NO_SERVICE") && !var28.getCause().getMessage().contains("SCARD_E_SERVICE_STOPPED")) {
                    if (!var28.getCause().getMessage().contains("SCARD_E_NO_READERS_AVAILABLE")) {
                        throw new PaciException("No reader were available");
                    }
                } else {
                    try {
                        Class var36 = Class.forName("sun.security.smartcardio.PCSCTerminals");
                        Field var38 = var36.getDeclaredField("contextId");
                        var38.setAccessible(true);
                        if (var38.getLong(var36) != 0L) {
                            Class var41 = Class.forName("sun.security.smartcardio.PCSC");
                            Method var8 = var41.getDeclaredMethod("SCardEstablishContext", Integer.TYPE);
                            var8.setAccessible(true);
                            Field var9 = var41.getDeclaredField("SCARD_SCOPE_USER");
                            var9.setAccessible(true);
                            long var10 = (Long)var8.invoke(var41, var9.getInt(var41));
                            var38.setLong(var36, var10);
                            this.TerminalFac = TerminalFactory.getDefault();
                            this.Terminals = this.TerminalFac.terminals();
                            Field var12 = var36.getDeclaredField("terminals");
                            var12.setAccessible(true);
                            Class var13 = Class.forName("java.util.Map");
                            Method var14 = var13.getDeclaredMethod("clear");
                            var14.invoke(var12.get(this.TerminalFac));
                        }
                    } catch (ClassNotFoundException var16) {
                    } catch (NoSuchFieldException var17) {
                    } catch (SecurityException var18) {
                    } catch (IllegalArgumentException var19) {
                    } catch (IllegalAccessException var20) {
                    } catch (NoSuchMethodException var21) {
                    } catch (InvocationTargetException var22) {
                    }
                }

                var1 = null;
                if (!var34) {
                    var34 = true;
                    this.ReaderChangeEvent(new String[0]);
                }

                try {
                    Thread.sleep(1000L);
                    this.Terminals = this.TerminalFac.terminals();
                } catch (InterruptedException var15) {
                    throw new PaciException("Interrupted exception has been raised");
                }
            }
        }*/

    }

    public void Dispose() {
        if (!this.Disposed) {
            this.Disposed = true;
            this.ReaderChangeThread.interrupt();

            try {
                Thread.sleep(200L);
            } catch (Exception var2) {
            }

            if (this.ReaderChangeThread.isAlive()) {
                this.ReaderChangeThread.stop();
            }
        }

    }
}
