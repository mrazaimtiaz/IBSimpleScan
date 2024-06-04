package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

import java.util.concurrent.ConcurrentHashMap;


abstract class SchemaContentAbstract {
    ConcurrentHashMap<String, String> LineContentOfObject = new ConcurrentHashMap();
    ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> Info = new ConcurrentHashMap();

    public SchemaContentAbstract(String var1) {
        this.ReadFile(var1);
    }

    public ConcurrentHashMap<String, Object> ReadSpecificData(String var1) throws PaciException {
        return (ConcurrentHashMap)this.Info.get(var1);
    }

    public abstract void ReadFile();

    public abstract void ReadFile(String var1);
}