package src;

import java.io.*;
import java.util.ArrayList;
import java.net.Socket;

public class ClientHandler implements Runnable{


    //static list with all clients belongs to the class <3
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    ClientHandler(Socket socket){
        try{

            this.socket = socket;
            //gets the character streams of the client's IO streams
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close(); //closing the wrapper will close the underlying socket streams
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(String s, ClientHandler clientHandler){
        try {
            clientHandler.bufferedWriter.write(s);
            clientHandler.bufferedWriter.write('\n'); //sending newline bc client waits for it
            clientHandler.bufferedWriter.flush(); //flush the buffer because the message (most likely) does not fill the buffer
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    private void targetedMessage(String ts, String targetName){
        for(ClientHandler clientHandler : clientHandlers){
            if(clientHandler.clientUsername.equals(targetName)){
                sendMessage(ts, clientHandler);
            }
        }
    }
    private void broadcastMessage(String s) {
        for(ClientHandler clientHandler : clientHandlers){
                if(!clientHandler.clientUsername.equals(clientUsername)) {
                   sendMessage(s, clientHandler);
                }
        }
    }

    //removing clients from the handler
    public void removeClientHandler(){
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
        clientHandlers.remove(this);
    }


    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                classifyMessage(messageFromClient);
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void classifyMessage(String messageFromClient){
        String[] tokenizedMessage = messageFromClient.split(" ", 3);
        if(tokenizedMessage[0].charAt(0) == '/'){
            switch (tokenizedMessage[0]){
                case "/nickname":
                    this.clientUsername = tokenizedMessage[1];
                    targetedMessage("Username changed to: " + clientUsername, clientUsername);
                    targetedMessage("+CUNAME " + clientUsername, clientUsername);
                    break;
                case "/private":
                    targetedMessage(clientUsername + " -PRIVATE--> :" + tokenizedMessage[2], tokenizedMessage[1]);
                    System.out.println(clientUsername + " -PRIVATE--> :" + tokenizedMessage[1]);
                    break;
            }
        }
        else{
            broadcastMessage(clientUsername + ": " + messageFromClient);
        }
    }


}
