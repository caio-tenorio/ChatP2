import javafx.application.Application;
import javafx.application.Platform;
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
    Button buttonDesconectar;
    Button buttonEnviar;

    //TEXTAREA
    TextArea conversa;

    //TEXT FIELD
    TextField mensagem;
    TextField conexao;
    TextField userName;

    //IMAGEM
    Image image;
    ImageView chatImagem;

    @Override
    public void start(Stage primaryStage) {
        //primaryStage
        primaryStage.setTitle("Programação 2 - Sistemas de Informação");
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(750);
        primaryStage.setMaximized(false);
        primaryStage.setResizable(false);

        //MAIN PANE
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(10, 10, 10, 10));
        mainGrid.setVgap(10);
        mainGrid.setHgap(12);

        //LABEL
        Label labelApresenta = new Label("IMessenger");
        GridPane.setConstraints(labelApresenta, 1,0);
        labelApresenta.setAlignment(Pos.CENTER);

        //IMAGEM
        //image = new Image("Logo.png", 110, 110, false, false);
        //chatImagem = new ImageView(image);
        //GridPane.setConstraints(chatImagem, 0, 0);

        //TEXT AREA PARA CONVERSA
        conversa = new TextArea();
        conversa.setPromptText("Conversa será exibida aqui");
        conversa.setEditable(false);
        GridPane.setConstraints(conversa,1,2, 2, 2);
        conversa.setDisable(true);

        //TEXT FIELD MENSAGEM
        mensagem = new TextField();
        mensagem.setPromptText("Digite sua mensagem");
        GridPane.setConstraints(mensagem, 1,4, 2, 1);
        mensagem.setDisable(true);

        mensagem.setOnAction(event -> {
            //chatClient.send(Message.fromString(mensagem.getText()));
            chatClient.send(new TextMessage(userName.getText(), mensagem.getText()));
            mensagem.setText("");
        });

        //TEXT FIELD CONEXAO
        conexao = new TextField();
        conexao.setPromptText("Insira IP");
        GridPane.setConstraints(conexao, 2, 0);

        //TEXT FIELD USERNAME
        userName = new TextField();
        userName.setPromptText("Nome do usuário");
        GridPane.setConstraints(userName, 2, 1);


        //BUTTONS

            //CONECTAR
            buttonConectar = new Button("Conectar");
            buttonConectar.getStyleClass().add("button-conectar");
            GridPane.setConstraints(buttonConectar, 3, 0);
            buttonConectar.setOnAction(e -> actionButtonConectar());
            buttonConectar.setPrefWidth(110);

        //DESCONECTAR
            buttonDesconectar = new Button("Desconectar");
            buttonDesconectar.getStyleClass().add("button-desconectar");
            GridPane.setConstraints(buttonDesconectar, 3, 0);
            buttonDesconectar.setOnAction(e -> actionButtonDesconectar());
            buttonDesconectar.setPrefWidth(110);
            buttonDesconectar.setVisible(false);

            //ENVIAR
            buttonEnviar = new Button("Enviar");
            buttonEnviar.getStyleClass().add("button-enviar");
            GridPane.setConstraints(buttonEnviar,3, 4);
            buttonEnviar.setOnAction(e -> actionButtonEnviar());
            buttonEnviar.setPrefWidth(110);
            buttonEnviar.setDisable(true);

        //GRID CHILDS
        mainGrid.getChildren().addAll(labelApresenta, conversa, buttonConectar,buttonDesconectar, buttonEnviar, mensagem, conexao, userName);

        //SCENE
        Scene scene = new Scene(mainGrid, 600, 400);
        scene.getStylesheets().add("Chat.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        //chatClient.send(".bye");
        chatClient.send(new ByeMessage());
        chatClient.stop();
        //Platform.exit();
    }

    public void actionButtonConectar(){
        if (buttonConectar.getText() == "Conectar" ) {
            String host = conexao.getText();
            int port = 4444;
            chatClient = new ChatClient(host, port, this, userName.getText());
            conversa.setDisable(false);
            mensagem.setDisable(false);
            buttonEnviar.setDisable(false);
            conexao.setDisable(true);
            userName.setDisable(true);
            buttonConectar.setVisible(false);
            buttonDesconectar.setVisible(true);
            conversa.appendText("Olá, " + chatClient.getUserName() + "!" +  "\n");
        }
    }
        //buttonConectar.setDisable(true);

        //if (args.length != 2)
        //    System.out.println("Usage: java ChatClient host port");
        //else
        //String host = conexao.getText();
        //int port = 4444;
        //chatClient = new ChatClient(host, port, this, "Caio" );

    public void actionButtonDesconectar () {
            chatClient.send(".bye");
            chatClient.stop();
            buttonDesconectar.setVisible(false);
            buttonConectar.setVisible(true);
            buttonEnviar.setDisable(true);
            conversa.setDisable(true);
            mensagem.setDisable(true);
            conexao.setDisable(false);
            userName.setDisable(false);
            mensagem.setText("");
            conversa.appendText("Você desconectou!" + "\n");

    }


    public void actionButtonEnviar(){
        chatClient.send(mensagem.getText());
    }

    @Override
    public synchronized void handle(String msg) {
        conversa.appendText(msg + "\n");

        if (msg.equals(".bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            chatClient.stop();
        } else
            System.out.println(msg);
    }

    @Override
    public void handleMessage(Message msg) {
        conversa.appendText("--" + msg.toString() + "\n");

        if (msg.getCommand().equals("TEXT")) {
            //if (msg.getSource() != null) {
            if (msg.getTarget() != null) {
                //conversa.appendText("<" + msg.getSource() + "> ");
                conversa.appendText("<" + msg.getTarget() + "> ");
            }
            conversa.appendText(msg.getMessage() + "\n");
        }

        if (msg.equals(".bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            chatClient.stop();
        }// else
        //    System.out.println(msg);
    }
}
