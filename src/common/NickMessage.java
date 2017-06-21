package common;

/**
 * Created by jamaj on 16/06/17.
 */
public class NickMessage extends Message {

    public NickMessage(Message msg) throws InvalidNicknameException {
        super(msg.getCommand(), msg.getSource(), msg.getTarget(), msg.getMessage());
        this.validate();
    }

    public NickMessage(String desiredNickname) throws InvalidNicknameException {
        super("NICK", desiredNickname, null, "");
        //setDesiredNickname(desiredNickname);
        this.validate();
    }

    public String getDesiredNickname() throws InvalidNicknameException {
        this.validate();
        return this.getSource();
    }

    public void setDesiredNickname(String desiredNickname) throws InvalidNicknameException {
        this.setSource(desiredNickname);
        this.validate();
    }

    public void validate() throws InvalidNicknameException {
        String nickname = this.getSource();

        if (nickname == null) {
            throw new InvalidNicknameException("O nickname não pode ser nulo.");

        } else if (nickname.length() > 25) {
            throw new InvalidNicknameException("O nickname não pode ter mais de 25 caracteres.");

        } else if (nickname.equals("")) {
            throw new InvalidNicknameException("O nickname não pode ser vazio.");

        }
    }

}
