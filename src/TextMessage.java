/**
 * Created by jamaj on 16/06/17.
 */
public class TextMessage extends Message {
    public TextMessage(String text) {
        super("TEXT", text);
    }

    public TextMessage(String username, String text) {
        super("TEXT", username, null, text);
    }

//    public static TextMessage fromString(String message) {
//
//    }
}
