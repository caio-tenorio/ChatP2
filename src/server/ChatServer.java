package server;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import common.*;

public class ChatServer implements Runnable {
    private ChatServerThread clients[] = new ChatServerThread[50];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;

    //private HashMap<Socket, String> mapSocketClient = new HashMap<Socket, String>();
    private HashMap<String, ChatServerThread> mapNicknameToClient = new HashMap<String, ChatServerThread>();
    //private HashMap<server.ChatServerThread, String> mapClientToNickname = new HashMap<server.ChatServerThread, String>();

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public ChatServer(int port) throws ErroIniciandoServidorException {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }

//    public synchronized void handle(int ID, String input) {
//        if (input.endsWith(".bye")) {
//            clients[findClient(ID)].send(".bye");
//            remove(ID);
//        } else
//            for (int i = 0; i < clientCount; i++)
//                clients[i].send(input);
//    }

    public synchronized void handle(int senderID, Message msg) {
//        if (msg.getCommand().equals("BYE")) {
//            //clients[findClient(ID)].send(".bye");
//            clients[findClient(senderID)].send(new ByeMessage());
//            remove(senderID);
//        } else {
//            if (msg.getCommand().equals("TEXT")) {
//                if (msg.getTarget() != null) {
//                    ChatServerThread targetClient = mapNicknameToClient.get(msg.getTarget());
//                    if (targetClient != null) {
//                        targetClient.send(msg);
//                    } else {
//                        ChatServerThread senderClient = clients[findClient(senderID)];
//                        senderClient.send(new ErrorMessage("TEXT_SENT_TO_INVALID_NICKNAME", "Nickname doesn't exist: " + msg.getTarget()));
//                    }
//                } else {
//                    for (int i = 0; i < clientCount; i++) {
//                        clients[i].send(msg);
//                    }
//                }
//            } else {
//                System.out.println("#" + senderID + "# " + msg.toString());
//            }
//        }

        ChatServerThread senderClient = clients[findClient(senderID)];

        if (msg.getCommand().equals("BYE")) {
            //clients[findClient(ID)].send(".bye");
            clients[findClient(senderID)].send(new ByeMessage());
            remove(senderID);
        } else if (msg.getCommand().equals("TEXT")) {
            if (msg.getTarget() != null) {
                ChatServerThread targetClient = mapNicknameToClient.get(msg.getTarget());
                if (targetClient != null) {
                    targetClient.send(msg);
                } else {
                    //ChatServerThread senderClient = clients[findClient(senderID)];
                    senderClient.send(new ErrorMessage("TEXT_SENT_TO_INVALID_NICKNAME", "Nickname doesn't exist: " + msg.getTarget()));
                }
            } else {
                for (int i = 0; i < clientCount; i++) {
                    clients[i].send(msg);
                }
            }
        } else if (msg.getCommand().equals("NICK")) {
            try {
                NickMessage nickMessage = new NickMessage(msg);
                //nickMessage.validate();

                String desiredNickname = nickMessage.getDesiredNickname();

                if (mapNicknameToClient.containsKey(desiredNickname)) {
                    senderClient.send(new ErrorMessage("NICK_ALREADY_IN_USE", "Nickname já está em uso!"));
                } else {
                    mapNicknameToClient.remove(getKeyByValue(mapNicknameToClient, senderClient));
                    mapNicknameToClient.put(desiredNickname, senderClient);
                    senderClient.send(new NickMessage(desiredNickname));
                }

            } catch (InvalidNicknameException ine) {
                senderClient.send(new ErrorMessage("NICK_INVALID", ine.getMessage()));
            }
        } else {
            System.out.println("#" + senderID + "# " + msg.toString());
        }
    }

    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            ChatServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1)
                for (int i = pos + 1; i < clientCount; i++)
                    clients[i - 1] = clients[i];
            clientCount--;
            try {
                toTerminate.close();

                mapNicknameToClient.remove(getKeyByValue(mapNicknameToClient, toTerminate));
                System.out.println("List of clients: " + mapNicknameToClient.keySet());
            } catch (IOException ioe) {
                System.out.println("Error closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new ChatServerThread(this, socket);

            mapNicknameToClient.put(Integer.toString(clients[clientCount].getID()), clients[clientCount]);
            System.out.println("List of clients: " + mapNicknameToClient.keySet());

            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        } else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }

    public static void main(String args[]) {
        ChatServer server = null;
        if (args.length != 1) {
            System.out.println("Usage: java server.ChatServer port");
        } else {
            try {
                server = new ChatServer(Integer.parseInt(args[0]));
            } catch (ErroIniciandoServidorException eise) {
                System.out.println("Erro iniciando o servidor!" + eise.getMessage());

            }
        }


    }
}