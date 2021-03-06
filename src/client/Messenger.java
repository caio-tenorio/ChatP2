package client;

import common.IChatMessageHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import common.*;

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
        GridPane.setConstraints(labelApresenta, 1, 0);
        labelApresenta.setAlignment(Pos.CENTER);

        //IMAGEM
        //image = new Image("Logo.png", 110, 110, false, false);
        //chatImagem = new ImageView(image);
        //GridPane.setConstraints(chatImagem, 0, 0);

        //TEXT AREA PARA CONVERSA
        conversa = new TextArea();
        conversa.setPromptText("Conversa será exibida aqui");
        conversa.setEditable(false);
        GridPane.setConstraints(conversa, 1, 2, 2, 2);
        conversa.setDisable(true);

        //TEXT FIELD MENSAGEM
        mensagem = new TextField();
        mensagem.setPromptText("Digite sua mensagem");
        GridPane.setConstraints(mensagem, 1, 4, 2, 1);
        mensagem.setDisable(true);

        mensagem.setOnAction(event -> {
            actionButtonEnviar();
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
        GridPane.setConstraints(buttonEnviar, 3, 4);
        buttonEnviar.setOnAction(e -> actionButtonEnviar());
        buttonEnviar.setPrefWidth(110);
        buttonEnviar.setDisable(true);

        //GRID CHILDS
        mainGrid.getChildren().addAll(labelApresenta, conversa, buttonConectar, buttonDesconectar, buttonEnviar, mensagem, conexao, userName);

        //SCENE
        Scene scene = new Scene(mainGrid, 600, 400);
        scene.getStylesheets().add("Chat.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (chatClient != null) {
            chatClient.send(new ByeMessage());
            chatClient.stop();
        }
    }

    public void estadoConectar(){
        buttonDesconectar.setVisible(false);
        buttonConectar.setVisible(true);
        buttonEnviar.setDisable(true);
        conversa.setDisable(true);
        mensagem.setDisable(true);
        conexao.setDisable(false);
        userName.setDisable(false);
        mensagem.setText("");

    }

    public void estadoDesconectar() {
        conversa.setDisable(false);
        mensagem.setDisable(false);
        buttonEnviar.setDisable(false);
        conexao.setDisable(true);
        userName.setDisable(true);
        buttonConectar.setVisible(false);
        buttonDesconectar.setVisible(true);
        conversa.appendText("Olá, " + chatClient.getUserName() + "!" + "\n");
    }

    public void alertaErro (String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void actionButtonConectar() {
        if (buttonConectar.getText() == "Conectar") {
            String host = conexao.getText();
            int port = 4444;
            try {
                chatClient = new ChatClient(host, port, this, userName.getText());
                estadoDesconectar();

            } catch (ErroConectandoException ece) {
                System.out.println(ece.getMessage());
                estadoConectar();
                //JOptionPane.showMessageDialog(null, "Sua conexão não foi completada! Confira se o host é válido!", "Erro Conectando", JOptionPane.ERROR_MESSAGE);
                alertaErro("Erro Conectando", "Sua conexão não foi completada!", "Confira se o host é válido!");
            } catch (InvalidNicknameException ine) {
                System.out.println(ine.getMessage());
                estadoConectar();
                //JOptionPane.showMessageDialog(null, "Sua conexão não foi completada! Confira se o host é válido!", "Erro Conectando", JOptionPane.ERROR_MESSAGE);
                alertaErro("Erro conectando!", "Sua conexão não foi completada!", ine.getMessage());
            }
        }
    }



    public void actionButtonDesconectar() {
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

    public void actionButtonEnviar() {
//        chatClient.send(new TextMessage(userName.getText(), mensagem.getText()));
//        mensagem.setText("");

        String line = mensagem.getText();
        String user = userName.getText();
        if (line.startsWith("/:")) {
            chatClient.send(Message.fromString(line.substring(2)));
        } else {
            if (!line.startsWith("/")) {
                chatClient.send(new TextMessage(user, line));
            } else {
                if (line.startsWith("/quit") || line.startsWith("/bye")) {
                    chatClient.send(new ByeMessage());
                }
            }
        }
        mensagem.setText("");
    }

    @Override
    public synchronized void handle(String msg) {

        if (msg.equals(".bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            chatClient.stop();
        } else
            System.out.println(msg);
        conversa.appendText(msg + "\n");
    }

    @Override
    public synchronized void handleMessage(Message msg) {
        if (msg.getCommand().equals("TEXT")) {
            if (msg.getSource() != null) {
                conversa.appendText("<" + msg.getSource() + "> ");
            }
            conversa.appendText(msg.getMessage() + "\n");
        }

        if (msg.getCommand().equals("NICK")) {
            System.out.println("Seu nickname agora é " + msg.getSource());

            conversa.appendText("Seu nickname agora é " + msg.getSource() + "\n");
            this.userName.setText(msg.getSource());
        }

        if (msg.getCommand().equals("BYE")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            chatClient.stop();
        } else {
            //System.out.println(msg);
        }

        if (msg.getCommand().equals("ERROR")) {
            // TRATAR OS ERROS!
            System.out.println(msg.toString());
            if (msg.getMessage() != null) {
                conversa.appendText("ERRO: " + msg.getMessage() + "\n");
            }
        }
    }
}
