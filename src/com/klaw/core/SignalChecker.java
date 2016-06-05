package com.klaw.core;

import com.klaw.exceptions.PortNotFoundException;
import com.klaw.util.OSDetector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Klaw Strife
 */
public class SignalChecker extends TimerTask implements SerialPortEventListener {
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;
    private static final byte[] AT_QUALITY_COMMAND = "AT+CSQ".getBytes();

    private SerialPort serialPort;
    private final String portName;
    private BufferedReader input;
    private OutputStream output;
    private List<QualityEventListener> listeners;
    private final Timer timer;

    private static float lastQuality;

    public SignalChecker(String portName, long refreshRate) throws IOException, TooManyListenersException,
            PortInUseException, UnsupportedCommOperationException,
            PortNotFoundException {
        this.portName = portName;
        lastQuality = 0;
        listeners = new ArrayList<QualityEventListener>();
        initializeConecction();
        timer = new Timer(true);
        timer.schedule(this, 0, refreshRate);
    }
    
    public static List<String> getCOMPorts() {
        List<String> list = new ArrayList<String>();
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        while(portIdentifiers.hasMoreElements()){
            CommPortIdentifier currPortId = (CommPortIdentifier) portIdentifiers.nextElement();
            list.add(currPortId.getName());
        }
        return list;
    }
    
    private void initializeConecction() throws IOException, TooManyListenersException,
            PortInUseException, UnsupportedCommOperationException,
            PortNotFoundException {
        if(OSDetector.isUnix()){
            System.setProperty("gnu.io.rxtx.SerialPorts", portName);
        }
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getName().equals(portName)) {
                portId = currPortId;
                break;
            }
        }
        
        if (portId == null) {
            throw new PortNotFoundException("Port not found: " + portName);
        }
        
        serialPort = (SerialPort) portId.open(this.getClass().getName(),
                TIME_OUT);
        serialPort.setSerialPortParams(DATA_RATE,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        output = serialPort.getOutputStream();
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

    }


    private void sendAT_CSQ() {
        try {            
            output.write(AT_QUALITY_COMMAND);
            output.write(13);
        } catch (IOException ex) {
            Logger.getLogger(SignalChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        if (serialPort != null) {
            timer.cancel();
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    
    public void addListener(QualityEventListener listener){
        listeners.add(listener);
    
    }
    
    public void removeListener(QualityEventListener listener){
        listeners.remove(listener);
    }
    
    private void fireEvent(float data) {
        if (!listeners.isEmpty()) {
            for (QualityEventListener listener : listeners) {
                listener.qualityRecived(data);
            }
        }
    }

    private float getQualityDbm(String inputLine) {
        String[] split = inputLine.split(":");
        String signalValue = split[1].trim().replace(",", ".");
        Float floatSignalValue = Float.valueOf(signalValue);
        return TransformSignalQuality.csq_dbm(floatSignalValue);
    }

    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                if(inputLine.contains("+CSQ")){
                    float qualityDbm = getQualityDbm(inputLine);                    
                    if(lastQuality != qualityDbm){
                        fireEvent(qualityDbm);
                        lastQuality = qualityDbm;                        
                    }                    
                }
            } catch (IOException | NumberFormatException e) {                
                System.err.println(e.toString());
            }
        }
    }

    @Override
    public void run() {
        sendAT_CSQ();
    }

    
}
