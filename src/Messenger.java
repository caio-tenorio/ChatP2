import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Messenger extends Application implements IChatMessageHandler {

    private ChatClient chatClient = null;

    public static void main(String[] args) {
        launch(args);
    }

    //BUTTONS
    Button buttonConectar;
    Button buttonEnviar;

    //TEXTAREA
    TextArea conversa;

    //TEXT FIELD
    TextField mensagem;
    TextField conexao;

    //IMAGEM
    //Image image;
    //ImageView chatImagem;

    @Override
    public void start(Stage primaryStage) {
        //primaryStage
        primaryStage.setTitle("Programação 2 - Sistemas de Informação");
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMaximized(true);

        //MAIN PANE
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(10, 10, 10, 10));
        mainGrid.setVgap(10);
        mainGrid.setHgap(12);

        //LABEL
        Label labelApresenta = new Label("Fucking Messenger");
        GridPane.setConstraints(labelApresenta, 1,0);
        //labelApresenta.setAlignment(Pos.CENTER);

        //IMAGEM
       // image = new Image("chat.jpeg");
        //chatImagem = new ImageView(image);
        //GridPane.setConstraints(chatImagem, 3, 1, 2, 2);

        //TEXT AREA PARA CONVERSA
        conversa = new TextArea();
        conversa.setPromptText("Conversa será exibida aqui");
        conversa.setEditable(false);
        GridPane.setConstraints(conversa,1,1, 2, 2);

        //TEXT FIELD MENSAGEM
        mensagem = new TextField();
        mensagem.setPromptText("Digite sua mensagem");
        GridPane.setConstraints(mensagem, 1,3, 2, 1);

        //TEXT FIELD CONEXAO
        conexao = new TextField();
        conexao.setPromptText("Insira IP");
        GridPane.setConstraints(conexao, 2, 0);

        //BUTTONS

        //CONECTAR
        buttonConectar = new Button("Conectar");
        buttonConectar.getStyleClass().add("button-conectar");
        GridPane.setConstraints(buttonConectar, 3, 0);
        buttonConectar.setOnAction(e -> actionButtonConectar());

        //ENVIAR
        buttonEnviar = new Button("Enviar");
        buttonEnviar.getStyleClass().add("button-enviar");
        GridPane.setConstraints(buttonEnviar,3, 3);
        buttonEnviar.setOnAction(e -> actionButtonEnviar());

        //GRID CHILDS
        mainGrid.getChildren().addAll(labelApresenta, conversa, buttonConectar, buttonEnviar, mensagem, conexao);

        //SCENE
        Scene scene = new Scene(mainGrid, 600, 400);
        scene.getStylesheets().add("Chat.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void actionButtonConectar(){

        //this.chatClient = null;

        //if (args.length != 2)
        //    System.out.println("Usage: java ChatClient host port");
        //else
        String host = conexao.getText();
        int port = 4444;
        chatClient = new ChatClient(host, port, this);

    }

    public void actionButtonEnviar(){
        chatClient.send(mensagem.getText());
    }

    @Override
    public void handle(String msg) {
        conversa.appendText(msg + "\n");

        if (msg.equals(".bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            chatClient.stop();
        } else
            System.out.println(msg);
    }
}
