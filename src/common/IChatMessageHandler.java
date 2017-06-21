package common;

/**
 * Created by Arthur on 14/06/2017.
 */
public interface IChatMessageHandler {

    public void handle(String msg);
    public void handleMessage(Message msg);

}
