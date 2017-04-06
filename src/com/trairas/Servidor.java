package com.trairas;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class Servidor extends JFrame{
    
    private JTextField enterField; //insere a mensagem do usuário
    private JTextArea displayArea; //exibe as informações para o usuário
    private ObjectOutputStream output; //gera o fluxo de saida para o cliente
    private ObjectInputStream input; // gera o fluxo de entrada a apartir do cliente
    private ServerSocket server; //socket de servidor
    private Socket connection; //conexao com o cliente
    private int counter =  1;

    //configura a GUI
public Servidor(){

    super("Server");
    
    enterField = new JTextField(); //crai a enterFiel
    enterField.setEditable(false);
    enterField.addActionListener(
            new ActionListener(){
            //envia a mensagem ao cliente
            public void actionPerformed(ActionEvent event){
            sendData(event.getActionCommand());
             enterField.setText("");
            }//fim do metodo actionPerformed
            }//fim da classe interna anonima
    );//fim da chamada para addActionListener
    
    add(enterField,BorderLayout.NORTH);
    
    displayArea = new JTextArea();//cria displayArea
    add(new JScrollPane(displayArea),BorderLayout.CENTER);
    
    setSize(300,150);
    setVisible(true);
}    //fim do construtor do servidor


public void runServer(){
    
try{//config o servidor para receber conexões; processa as conexoes
    server = new ServerSocket(12345,100); //cria ServerSpcket
    
    
    try{
        waitForConnection();
        getStreams();
        ProcessConection();
    }catch(EOFException e){
    displayMessage("\nServer Termined connection");
    }
    finally{
    closeConection(); //fecha a conexao
    ++counter;
    }
    
}
catch(IOException io){
io.printStackTrace();
}
}

//espera que a conexao chegue e então exibe informações sobre a conexão
private void waitForConnection()throws IOException{

    displayMessage("Waiting for connection");
    connection = server.accept(); //permite que o servidor aceite a conexão
    displayMessage("Connection "+counter+" received from: "+connection.getInetAddress().getHostName());
}

//ontém fluxos para enviar e receber dados
private void getStreams() throws IOException{
    //configura o fluxo de saida de dados
output = new ObjectOutputStream(connection.getOutputStream());
    //configura o fluxo de entrada de dados
input = new ObjectInputStream(connection.getInputStream());
 
    displayMessage("\n I/O streams\n");
}

//processa a conexão com o cliente
private void  ProcessConection() throws IOException{
String message = "Connection sucessful!";
    sendData(message);//envia uma menssagem de conexão bem sucedida
    
    //ativa a enterField de modo que o usuário do servidor possa enviar menssagens  
    setTextFieldEditable(true);
    
    //processa as menssagens enviadas pelo cliente
    do{
    
        try{//lê e exibe a menssagem
            message = (String) input.readObject();//lê uma nova menssagem
            displayMessage("\n"+message);
        }catch(ClassNotFoundException c){
        displayMessage("\nUnknowm object type received");
        }
    
    }while(!message.equals("CLIENT>>> TERMINATE"));
}

//fecha os fluxos e os sockets
private void closeConection(){

    displayMessage("\nTerminating connection\n");
    setTextFieldEditable(false);//desativa a enterField
    
    try{
    output.close();
    input.close();
    connection.close();
    }catch(IOException io){}
    
}

//envia menssagem ao cliente
private void sendData(String message){

try{
output.writeObject("SERVER>> " +message);
output.flush();//esvazia a saida para o cliente
displayMessage("\nSERVER>> "+message);
}catch(IOException io){displayArea.append("\nError writing objetc");}

}

//manipula a displayArea na  thread de despacho de eventos
private void displayMessage(final String messageToDisplay){
    SwingUtilities.invokeLater(new Runnable(){
        public void run(){
        displayArea.append(messageToDisplay);
        }
    });
}

//manipula a displayArea na Thread de despacho de eventos
private void setTextFieldEditable(boolean b) {
    SwingUtilities.invokeLater(new Runnable(){
        public void run(){
        enterField.setEditable(b);
        }
    });
}
    

    
}
