public interface TCPConnectionObserver {

    void onConnectionReady(TCPConnection connection);
    void onReceiveString(TCPConnection connection, String value);
    void onDisconnect(TCPConnection connection);
    void onException(TCPConnection connection, Exception e);
}
