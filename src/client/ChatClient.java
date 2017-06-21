package client;

import common.*;

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

    public ChatClient(String serverName, int serverPort) throws ErroConectandoException, InvalidNicknameException {
        this(serverName, serverPort, null, null );
    }

    public ChatClient(String serverName, int serverPort, IChatMessageHandler chatMessageHandler, String userName) throws ErroConectandoException, InvalidNicknameException {
        System.out.println("Establishing connection. Please wait ...");
        try {
            NickMessage nickMessage = new NickMessage(userName);
            this.userName = userName;
            this.chatMessageHandler = chatMessageHandler;
            //socket = new Socket(serverName, serverPort);
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverName, serverPort), 5000);

            System.out.println("Connected: " + socket);

            start();

            send(nickMessage);
        } catch (SocketTimeoutException ste) {
            System.out.println("Timeout: " + ste.getMessage());
            throw new ErroConectandoException("Erro ao conectar (timeout): " + ste.getMessage());
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        } catch (InvalidNicknameException ine) {
            System.out.println("Invalid Nickname: " + ine.getMessage());
            throw ine;
        }
    }

    public void run() {
        while (thread != null) {
            try {
                //send(console.readLine());
                //send(new common.TextMessage(console.readLine()));
                String line = console.readLine();
                if (line.startsWith("/:")) {
                    send(Message.fromString(line.substring(2)));
                } else {
                    //send(new common.TextMessage(this.userName, console.readLine()));
                    if (!line.startsWith("/")) {
                        send(new TextMessage(this.userName, line));
                    } else {
                        if (line.startsWith("/quit") || line.startsWith("/bye")) {
                            send(new ByeMessage());
                        }
                    }
                }
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
                    //System.out.println(msg.getSource() + " mudou de apelido para " + msg.getTarget());
                    System.out.println("Seu nickname agora é " + msg.getSource());
                    this.userName = msg.getSource();
                    break;
                default:
                    System.out.println("Mensagem desconhecida: " + msg.toString());
            }
        } else {
            this.chatMessageHandler.handleMessage(msg);
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
            System.out.println("Usage: java client.ChatClient host port user_name");
        else
            try {
                client = new ChatClient(args[0], Integer.parseInt(args[1]), null, args[2]);
            } catch (ErroConectandoException ece){
                System.out.println(ece.getMessage());
            } catch (InvalidNicknameException ine) {
                System.out.println(ine.getMessage());
            }
    }
}