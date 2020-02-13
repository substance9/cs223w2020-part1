package main.java.cs223w2020.txprocessor;

import java.sql.Timestamp;
import java.util.Date;

import main.java.cs223w2020.OperationQueue;
import main.java.cs223w2020.TransactionQueue;
import main.java.cs223w2020.model.Operation;
import main.java.cs223w2020.model.Transaction;

public class ResultAggregator implements Runnable {

    private TransactionQueue resQueue;

    public ResultAggregator(TransactionQueue resQueue){
        this.resQueue = resQueue;
    }

    public void run() 
    {
        Transaction restx = null;
        while(true){
            restx = resQueue.take();
            System.out.println(restx);
        }
    }

} 