/**
 * Created by jamaj on 16/06/17.
 */
public class MessageCommand extends Command {

    public MessageCommand(String message) {
        super("MSG", ":" + message);
    }

}
