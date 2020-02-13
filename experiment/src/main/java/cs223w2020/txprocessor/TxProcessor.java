package main.java.cs223w2020.txprocessor;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import main.java.cs223w2020.TransactionQueue;
import main.java.cs223w2020.model.Operation;
import main.java.cs223w2020.model.Transaction;

public class TxProcessor implements Runnable 
{ 
    private TransactionQueue txQueue;
    private TransactionQueue resQueue;
    private String dbName;
    private String datasetConcurrency;
    private int mpl;
    private String concurrency;

    private ExecutorService threadPool;
    private HikariDataSource  connectionPool;

    private Thread resultAggregator;

    public TxProcessor(String dbName, String datasetConcurrency, int mpl, TransactionQueue txQueue){
        this.dbName = dbName;
        this.mpl = mpl;
        this.txQueue = txQueue;
        this.datasetConcurrency = datasetConcurrency;

        Properties prop = getHikariDbProperties(dbName);
        String jdbcUrlBase = prop.getProperty("jdbcUrl");
        String jdbcUrl = jdbcUrlBase + "cs223w2020_"+ datasetConcurrency + "_concurrency";

        HikariConfig cfg = new HikariConfig(prop);
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setMaximumPoolSize(mpl);
        cfg.setAutoCommit(false);
        connectionPool = new HikariDataSource(cfg);

        threadPool = Executors.newFixedThreadPool(mpl); 

        resQueue = new TransactionQueue();
        resultAggregator = new Thread(new ResultAggregator(resQueue)); 
        resultAggregator.start(); 
    }

    public Properties getHikariDbProperties(String dbName){
        Properties prop = null;
        try (InputStream input = TxProcessor.class.getClassLoader().getResourceAsStream(dbName + ".properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }

    public void run() 
    {
        TxExecutor txexecutor = null;
        Transaction tx = null;
        while(true){
            //1. get the transaction from queue
            tx = txQueue.take();
            //2. Construct TxExecutor with the (1)transaction, (2)connectionPool (3)result (transaction) queue
            txexecutor = new TxExecutor(tx, connectionPool, resQueue);
            //3. Get a thread from pool and execute
            threadPool.execute(txexecutor);
        }
    }
} 