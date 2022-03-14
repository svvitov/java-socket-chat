import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements TCPConnectionObserver{

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private final JTextArea textField = new JTextArea();
    private final JTextField nickname = new JTextField();
    private final JTextField messageInput = new JTextField();

    private TCPConnection connection;

    private Client(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        textField.setEditable(false);
        textField.setLineWrap(true);
        add(textField, BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);
        add(nickname, BorderLayout.NORTH);

        messageInput.addActionListener(e -> {
            if(messageInput.getText().trim() != null){
                String msg = messageInput.getText().trim();
                messageInput.setText(null);
                connection.sendString(nickname.getText().trim() + ": " + msg);
            }
        });

        try {
            connection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            log("Ошибка подключения");
        }

        setVisible(true);
    }

    @Override
    public void onConnectionReady(TCPConnection connection) {
        log("Коннектион выполнен");
    }

    @Override
    public void onReceiveString(TCPConnection connection, String value) {
        log(value);
    }

    @Override
    public void onDisconnect(TCPConnection connection) {
        log("Отключился");
    }

    @Override
    public void onException(TCPConnection connection, Exception e) {
       log("Алярма, экспетион");
    }

    private synchronized void log(String msg){
        SwingUtilities.invokeLater(() -> {
            textField.append(msg + "\n");
            textField.setCaretPosition(textField.getDocument().getLength());
        });
    }
}
