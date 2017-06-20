import java.net.*;
import java.io.*;

public class ChatClient implements Runnable {
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;

    private IChatMessageHandler chatMessageHandler = null;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName = null;

    public ChatClient(String serverName, int serverPort) {
        this(serverName, serverPort, null, null );
    }

    public ChatClient(String serverName, int serverPort, IChatMessageHandler chatMessageHandler, String userName) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            this.userName = userName;
            this.chatMessageHandler = chatMessageHandler;
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                //send(console.readLine());
                send(new TextMessage(console.readLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*try {
                streamOut.writeUTF(console.readLine());
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }*/
        }
    }

    public void send(String msg) {
        String var = (this.userName +": " + msg);
        try {
            streamOut.writeUTF(var);
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println("Sending error: " + ioe.getMessage());
            stop();
        }
    }

    public void send(Message message) {
        try {
            streamOut.writeUTF(message.toString());
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println("Sending error: " + ioe.getMessage());
            stop();
        }
    }

    public void handle(String msg) {
        if (this.chatMessageHandler == null) {
            if (msg.equals(".bye")) {
                System.out.println("Good bye. Press RETURN to exit ...");
                stop();
            } else
                System.out.println(msg);
        } else {
            this.chatMessageHandler.handle(msg);
        }
    }

    public void handle(Message msg) {
        if (this.chatMessageHandler == null) {
            switch (msg.getCommand()) {
                case "TEXT":
                    System.out.println(msg.getSource() + ": " + msg.getMessage());
                    break;
                case "BYE":
                    System.out.println("Good bye. Press RETURN to exit ...");
                    stop();
                    break;
                case "NICK":
                    System.out.println(msg.getSource() + " mudou de apelido para " + msg.getTarget());
                    break;
                default:
                    System.out.println("Mensagem desconhecida: " + msg.toString());
            }
        } else {
            this.chatMessageHandler.handleMessage(msg);
            System.out.println(msg.toString());
        }
    }

    public void start() throws IOException {
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
        try {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }
        client.close();
        client.stop();
    }

    public static void main(String args[]) {
        ChatClient client = null;
        if (args.length != 3)
            System.out.println("Usage: java ChatClient host port user_name");
        else
            client = new ChatClient(args[0], Integer.parseInt(args[1]),null, args[2]);
    }
}