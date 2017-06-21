package common;

/**
 * Created by jamaj on 16/06/17.
 */
public class NickMessage extends Message {

    public NickMessage(String desiredNickname) throws InvalidNicknameException {
        super("NICK", desiredNickname);
        if (desiredNickname == null) {
            throw new InvalidNicknameException("O nickname não pode ser nulo.");

        } else if (desiredNickname.length() > 25) {
            throw new InvalidNicknameException("O nickname não pode conter mais de 25 caracteres.");

        } else if (desiredNickname.equals("")) {
            throw new InvalidNicknameException("O nickname não pode ser vazio.");

        }

    }

//    public static common.NickMessage fromString(String messageStr) {
//
//    }
//
}
