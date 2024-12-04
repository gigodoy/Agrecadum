package com.example.agreducamandroid;

public class ApiRequest {
    private String scannedCode;
    private String driverName;
    private String taskType;

    // Constructor con parámetros
    public ApiRequest(String scannedCode, String taskType, String driverName) {
        this.scannedCode = scannedCode;
        this.taskType = taskType;
        this.driverName = driverName;
    }


    public String getScannedCode() {
        return scannedCode;
    }

    public void setScannedCode(String scannedCode) {
        this.scannedCode = scannedCode;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
