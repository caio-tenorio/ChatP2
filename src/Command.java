/**
 * Created by jamaj on 16/06/17.
 */
public abstract class Command {

    private String code;
    private String parameters;

    public Command(String code) {
        this.code = code;
        this.parameters = null;
    }

    public Command(String code, String parameters) {
        this.code = code;
        this.parameters = parameters;
    }

    public String getCode() {
        return this.code;
    }

    public String getParameters() {
        return this.parameters;
    }

    public String toString() {
        return this.getCode() + " " + this.getParameters();
    }

}
