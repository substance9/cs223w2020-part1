package main.java.cs223w2020.txsimulator;

import java.sql.Timestamp;

import main.java.cs223w2020.model.Operation;
import main.java.cs223w2020.model.Transaction;
import main.java.cs223w2020.OperationQueue;
import main.java.cs223w2020.TransactionQueue;

public class BatchTxSimulator extends TxSimulator 
{ 

    private OperationQueue opQueue;
    private String lastSensorId = "";
    private Transaction tx = new Transaction();

    public BatchTxSimulator(OperationQueue opQueue, TransactionQueue txQueue){
        super(opQueue, txQueue);
    }

    @Override
    public void processNewOperation(Operation op){

        if(op.sensorId.equals(lastSensorId)) { 
            tx.appendOperation(op);
        } else {
            lastSensorId = op.sensorId;
            if(tx.operations.size() > 0) {
                sendTransaction(tx);
            }
            Transaction tx = new Transaction();
            tx.appendOperation(op);
        }
    }

    @Override
    public void processNowTimeTick(Timestamp ts){
        return;
    }
} 