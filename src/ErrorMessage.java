/**
 * Created by jamaj on 21/06/17.
 */
public class ErrorMessage extends Message {

    public ErrorMessage(String errorCode, String message) {
        super("ERROR", errorCode, message);
    }

}
