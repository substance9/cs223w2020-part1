package cs223w2020;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import main.java.cs223w2020.OperationQueue;
import main.java.cs223w2020.TransactionQueue;
import main.java.cs223w2020.replayer.*;
import main.java.cs223w2020.txsimulator.*;
import main.java.cs223w2020.txprocessor.*;

public class Experiment {
    public static void main( String[] args ){
        System.out.println( "CS223 Part 1 Experiment Initiating:" );

        Properties prop = readConfig();

        OperationQueue oQueue = new OperationQueue();
        TransactionQueue tQueue = new TransactionQueue();

        //Start Transaction Sender
        // Thread txProcessor = new Thread(new TxProcessor(prop.getProperty("sender.db"),
        //                                             prop.getProperty("replayer.concurrency"),
        //                                             Integer.parseInt(prop.getProperty("sender.mpl")),
        //                                             tQueue)); 
        // txProcessor.start();

        //Start Transaction Simulator according to the specified policy
        String txSimulationPolicy = prop.getProperty("simulator.policy");
        Thread txSimulator = null;
        if (txSimulationPolicy.equals("single")){
            txSimulator = new Thread(new SingleTxSimulator(oQueue, tQueue)); 
        }
        //else if (){

        //}
        else{
            System.out.println("ERROR: Transaction simulation policy " + txSimulationPolicy + " not supported");
            return;
        }
        txSimulator.start(); 

        // //Start the replayer to read and send data
        Thread replayer = new Thread(new Replayer(prop.getProperty("replayer.inputs_directory"), 
                                                    prop.getProperty("replayer.concurrency"),
                                                    Integer.parseInt(prop.getProperty("replayer.experiment_duration")),
                                                    oQueue)); 
        
        System.out.println("Experiment Starting");
        long expStartTime = System.currentTimeMillis();
        replayer.start(); 

        // //reaping all threads, ending the experiment
        try
        { 
            replayer.join(); 
        } 
        catch(Exception ex) 
        { 
            System.out.println("Exception has been" + " caught" + ex); 
        } 

        try
        { 
            txSimulator.join(); 
        } 
        catch(Exception ex) 
        { 
            System.out.println("Exception has been" + " caught" + ex); 
        } 

        // try
        // { 
        //     txProcessor.join(); 
        // } 
        // catch(Exception ex) 
        // { 
        //     System.out.println("Exception has been" + " caught" + ex); 
        // } 

        //long expEndTime = System.currentTimeMillis();
        //System.out.println("Experiment took " + String.valueOf(expEndTime-expStartTime) + "ms to finish");
    }

    private static Properties readConfig(){
        Properties prop = null;
        try (InputStream input = Experiment.class.getClassLoader().getResourceAsStream("experiment.properties")) {

            prop = new Properties();

            // load a properties file
            prop.load(input);

            System.out.println( "Experiment Parameters:" );
            // get the property value and print it out
            System.out.println("--replayer.inputs_directory:\t"+prop.getProperty("replayer.inputs_directory"));
            System.out.println("--replayer.concurrency:\t\t"+prop.getProperty("replayer.concurrency"));
            System.out.println("--replayer.experiment_duration:\t"+prop.getProperty("replayer.experiment_duration"));
            System.out.println("--simulator.policy:\t\t"+prop.getProperty("simulator.policy"));
            System.out.println("--simulator.db:\t\t\t"+prop.getProperty("sender.db"));
            System.out.println("--simulator.mpl:\t\t"+prop.getProperty("sender.mpl"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return prop;
    }
 }