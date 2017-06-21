package common;

/**
 * Created by jamaj on 16/06/17.
 */
public class TextMessage extends Message {

    public TextMessage(String text) {
        super("TEXT", text);
    }

    public TextMessage(String senderNickname, String text) {
        super("TEXT", senderNickname, null, text);
    }

//    public static common.TextMessage fromString(String message) {
//
//    }
//
}
