package vibrofeeldebug;

import com.fazecast.jSerialComm.SerialPort;

public class App {
    private InterfaceWnd interfaceWnd;
    private SerialPort serialPort; 

    private int lastLargeMotor = 0;
    private int lastSmallMotor = 0;

    public App() {
        
        interfaceWnd = new InterfaceWnd(() -> {
            this.closePort();
            this.refreshPorts();
        }, portName -> {
            if(serialPort == null) {
                SerialPort[] ports = SerialPort.getCommPorts();
                for (SerialPort serialPort : ports) {
                    String targetPort = portToString(serialPort);
                    serialPort.setComPortParameters(115200, 8, 1, 0);
                    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
                    if (targetPort.equals(portName)) {
                        if (serialPort.openPort()) { 
                            this.closePort();
                            this.serialPort = serialPort;
                            interfaceWnd.log("Port: \"" + targetPort + "\" has been opened");
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            } else {
                closePort();
            }
            return false;
        }, () -> {
            int[] values = interfaceWnd.getValues();
            write(values[0], values[1]);
        }, () -> {
            closePort();
        });
        interfaceWnd.show();
        closePort();
        refreshPorts();
    }
    
    public void refreshPorts() {
        interfaceWnd.reset();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            interfaceWnd.addComboItem(port.getSystemPortName());
            System.out.println(portToString(port));
            
        }
    }

    private void closePort() {
        if (serialPort != null) {
            String name = portToString(serialPort);
            write(0, 0);
            serialPort.closePort();
            serialPort = null;
            interfaceWnd.log("Port: \"" + name + "\" has been closed");
        }
    }

    private String portToString(SerialPort port) {
        //return port.getSystemPortPath() + " " + port.getSystemPortName();
        return port.getSystemPortName();
    } 

    private void write(int small, int large) {
        if (small == lastSmallMotor && large == lastLargeMotor) {
            return;
        }
        
        if (serialPort == null) {
            interfaceWnd.log("Serial port not connected");
            return;
        }
        if (!serialPort.isOpen()) {
            interfaceWnd.log("Error port had been disconnected");
            interfaceWnd.reset();
            return;
        }

        byte[] buffer = new byte[] { (byte) small, (byte) large};
        serialPort.writeBytes(buffer, buffer.length);

        lastSmallMotor = small;
        lastLargeMotor = large;

        //serialPort.writeBytes(buffer, buffer.length);
        interfaceWnd.log("writing: " + small + ":" + large);
    }
}
