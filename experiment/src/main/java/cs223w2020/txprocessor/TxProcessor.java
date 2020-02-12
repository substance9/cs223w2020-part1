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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.cs223w2020.TransactionQueue;
import main.java.cs223w2020.model.Operation;
import main.java.cs223w2020.model.Transaction;

public class TxProcessor implements Runnable 
{ 
    private TransactionQueue txQueue;
    private String dbName;
    private String datasetConcurrency;
    private int mpl;
    private String concurrency;

    private ExecutorService threadPool;
    private HikariDataSource  connectionPool;

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
        Transaction tx;
        Date date;
        long time;
        // while(true){
        //     tx = txQueue.take();
        //     date = new Date();
        //     time = date.getTime();
        //     Timestamp ts = new Timestamp(time);
        //     processNowTimeTick(ts);
        // }
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            con = connectionPool.getConnection();
            pst = con.prepareStatement("SELECT * FROM location");
            rs = pst.executeQuery();

            while (rs.next()) {
                System.out.format("%s %d %d %d", rs.getString(1), rs.getInt(2), 
                        rs.getInt(3),rs.getInt(4));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }finally {

            try {
            
                if (rs != null) {
                    rs.close();
                }
                
                if (pst != null) {
                    pst.close();
                }
                
                if (con != null) {
                    con.close();
                }
                
                connectionPool.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    
} 