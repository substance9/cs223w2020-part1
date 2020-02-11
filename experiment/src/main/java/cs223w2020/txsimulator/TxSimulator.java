package main.java.cs223w2020.txsimulator;

import main.java.cs223w2020.OperationQueue;
import main.java.cs223w2020.model.Operation;

public class TxSimulator implements Runnable 
{ 
    private int mpl;
    private OperationQueue opQueue;

    public TxSimulator(String policyStr, String dbStr, int mpl, OperationQueue opQueue){
        this.mpl = mpl;
        this.opQueue = opQueue;
    }

    public void run() 
    {
        
        
    }
} 