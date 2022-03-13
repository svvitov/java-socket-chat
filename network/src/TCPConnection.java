import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader input;
    private final BufferedWriter output;
    private final TCPConnectionObserver eventListener;

    public TCPConnection(TCPConnectionObserver eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        eventListener.onReceiveString(TCPConnection.this, input.readLine());
                    }
                } catch (IOException e){
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }

            }
        });
        this.rxThread.start();
    }

    public TCPConnection(TCPConnectionObserver eventListener, String ipAddress, int port) throws IOException{
        this(eventListener, new Socket(ipAddress, port));
    }

    public synchronized void sendString(String value){
        try {
            output.write(value + "\r\n");
            output.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
