package com.trairas;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;



public class Cliente extends JFrame{

    private JTextField enterField; //insere informações fornecidas pelo usuário
    private JTextArea displayArea; //exibe informações para o usuário
    private ObjectOutputStream output; //gera o fluxo de saida para o servidor
    private ObjectInputStream input; //gera o fluxo de entrada a partir do servidor
    private String message = "";//mensagem do servidor
    private String chatServer; //servidor de host para esse aplicativo
    private Socket client; //socket para comunicação
    
    //inicializa chatServer e configura a GUI  
    public Cliente(String host){
        
        super("Cliente");
    
        chatServer =  host;
    
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(
                new ActionListener(){
                    //envia a menssagem ao servidor
                    
                    public void actionPerformed(ActionEvent event){
                       sendData(event.getActionCommand());
                        enterField.setText("");
                    }
        });
        
        add(enterField,BorderLayout.NORTH);
        displayArea = new JTextArea(); //cria displayArea
        add(new JScrollPane(displayArea),BorderLayout.CENTER);
        
        setSize(300,150);
        setVisible(true);
    }
    
    public void runCliente(){
    
try{//config o servidor para receber conexões; processa as conexoes
  
        connectToServer();//cria um socket para fazer a conexao
        getStreams(); //obtem os fluxos de entrada e saida
        ProcessConection();//processa a conexao
    }catch(EOFException e){
    displayMessage("\nClient Termined connection");
    }
    catch(IOException oi){
    oi.printStackTrace();
    }
    finally{
    closeConection(); //fecha a conexao
    }
}
  
    //processa a conexão com o cliente
private void  ProcessConection() throws IOException{
    
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

    
    
    //ontém fluxos para enviar e receber dados
private void getStreams() throws IOException{
    //configura o fluxo de saida de dados
output = new ObjectOutputStream(client.getOutputStream());
output.flush();//esvazia o buffer de saida para enviar as informações
    //configura o fluxo de entrada de dados
input = new ObjectInputStream(client.getInputStream());
 
    displayMessage("\n I/O streams\n");
}

    
 private void connectToServer()throws IOException{
 
     displayMessage("Attemping connection\n");
 //cria um socket para fazer a connexão
     client = new Socket(InetAddress.getByName(chatServer),12345);
     
     //exibe informções sobre a connexão
     displayMessage("Connected to "+client.getInetAddress().getHostName());
     
 }   
    
    
private void sendData(String message){

try{

output.writeObject(message);
output.flush();//esvazia a saida para o cliente
displayMessage("\nCLIENTE>>"+message);
}catch(IOException io){displayArea.append("\nError writing objetc");}

}

//fecha os fluxos e os sockets
private void closeConection(){

    displayMessage("\nTerminating connection\n");
    setTextFieldEditable(false);//desativa a enterField
    
    try{
    output.close();
    input.close();
    client.close();
    }catch(IOException io){}
    
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
