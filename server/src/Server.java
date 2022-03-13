import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionObserver{

    private static void log(String msg){
        System.out.println(msg);
    }

    public static void main(String[] args) {
        new Server();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private Server(){
        log("Server up");
        try(ServerSocket serverSocket = new ServerSocket(8000)){
            while(true){
                try{
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e){
                    log("TCPConnection exception " + e);
                }
            }
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection connection) {
        connections.add(connection);
        sendToAll("Connected: " + connection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection connection, String value) {
        log("Message: \"" + value + "\" from: "+connection);
        sendToAll(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection connection) {
        connections.remove(connection);
        sendToAll("Disconnected: " + connection);
    }

    @Override
    public synchronized void onException(TCPConnection connection, Exception e) {
        log("Exception " + e + "in " + connection);
    }

    private void sendToAll(String msg){
        log(msg);
        connections.forEach(c -> c.sendString(msg));
    }
}
