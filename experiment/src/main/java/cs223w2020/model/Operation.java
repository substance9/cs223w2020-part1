package main.java.cs223w2020.model;

import java.sql.Timestamp;

public class Operation {
    public String operationStr;
    public Timestamp timestamp;
    public String sensorId;
    public String sqlStr;

    public Operation(String operationStr,Timestamp timestamp,String sensorId,String sqlStr){
        this.operationStr = operationStr;
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.sqlStr = sqlStr;
    }

    @Override
    public String toString()
    {
         return "Operation: " + operationStr + " - Timestamp: " + timestamp.toLocalDateTime() + " - SensorID: "  + sensorId + " - SQLStr: "  + sqlStr;
    }
}

