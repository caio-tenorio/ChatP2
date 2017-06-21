/**
 * Created by jamaj on 16/06/17.
 */
public class Message {

    private String command;
    private String source;
    private String target;
    private String message;

    public Message(String command, String source, String target, String message) {
        this.command = command;
        this.source = source;
        this.target = target;
        this.message = message;
    }

    public Message(String command, String source, String message) {
        this(command, source, null, message);
    }

    public Message(String command, String message) {
        this(command, null, null, message);
    }

    //public Message(String message) {
    public Message(String command) {
//        this.command = "MSG";
//        this.source = null;
//        this.target = null;
//        this.message = message;
        this(command, null);
    }

    public String toString() {
//        String message = "";
//        if (this.source != null) {
//            message += this.source;
//        }
//        if (this.target != null) {
//            message += "TO";
//            message +=
//        }

        String message = this.command;
        if (this.source != null) {
            message += " " + this.source;
        }
        if (this.target != null) {
            message += " " + this.target;
        }
        if (this.message != null) {
            message += " :" + this.message;
        }

        return message;

        //return this.message;
    }

    public String getCommand() {
        return this.command;
    }

    public String getSource() {
        return this.source;
    }

    public String getTarget() {
        return this.target;
    }

    public String getMessage() {
        return this.message;
    }

    //"COMMAND SOURCE TARGET :MESSAGE"

    public static Message fromString(String str) {
        String command = "MSG";
        String source = null;
        String target = null;
        //String message = "";
        String message = null;

        if (str == null) {
            return null;
        }

        String[] parts = str.split(" :", 2);
        if (parts.length < 2) {
            // Erro! As mensagens têm que ter pelo menos o comando e o separador.
            command = str;
        } else {
            String[] beforeMessage = parts[0].split(" ", 3);
            command = beforeMessage[0];
            if (beforeMessage.length == 0) {
                // Erro!
            }
            if (beforeMessage.length >= 1) command = beforeMessage[0];
            if (beforeMessage.length >= 2) source = beforeMessage[1];
            if (beforeMessage.length >= 3) target = beforeMessage[2];
        }
        if (parts.length >= 2) {
            message = parts[1];
        }

        return new Message(command, source, target, message);
    }

    public static void main(String[] args) {
        String msg0 = "QUIT";
        String msg1 = "TEXT :Text1";
        //String msg2 = "TEXT target :Text2";
        String msg2 = "TEXT source :Text2";
        String msg3 = "TEXT source target :Text3";

        Message text0 = Message.fromString(msg0);
        Message text1 = Message.fromString(msg1);
        Message text2 = Message.fromString(msg2);
        Message text3 = Message.fromString(msg3);

        assert text0.toString().equals(msg0);
        assert text1.toString().equals(msg1);
        assert text2.toString().equals(msg2);
        assert text3.toString().equals(msg3);

        System.out.println(text0.toString());
        System.out.println(text1.toString());
        System.out.println(text2.toString());
        System.out.println(text3.toString());
    }

}
