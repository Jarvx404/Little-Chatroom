package src;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;


//Look mom, I build a chatroom!
public class Client {

    //declarations
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username){
        try{
            running = true;
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeClientConnection(socket, bufferedReader, bufferedWriter);
        }
    }

    private boolean running;
    /*

       The big boy thread that listens for messages, maybe another thread is unnecesary science don't think main is occupied but still,
       just for the sake of being organized and meybe adding other features that might interfere.
       ex: file sharing
    */
    void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromServer;
                while(socket.isConnected() && running){
                    try{
                        messageFromServer = bufferedReader.readLine();
                        classifyMessage(messageFromServer);
                    }catch (IOException e){
                        closeClientConnection (socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    private void closeClientConnection(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try
        {
            if(bufferedWriter != null){bufferedWriter.close();}
            if(bufferedReader != null){bufferedReader.close();}
            if(socket != null){socket.close();}




        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        entranceText();
        Scanner scanner = new Scanner(System.in);
        System.out.println("| Enter your username here: ");
        String username = scanner.nextLine();

        Socket connectionSocket = new Socket("localhost", 1234);
        Client client = new Client(connectionSocket, username);

        client.listenForMessage();
        client.sendMessage();
    }


    //anonymous object thread for sending messages, not to block the current thread
    private void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    bufferedWriter.write(username);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    Scanner scanner = new Scanner(System.in);

                    while(socket.isConnected() && running){
                        String messageToSend = scanner.nextLine();
                        bufferedWriter.write(messageToSend);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }catch (IOException e){
                    closeClientConnection(socket, bufferedReader, bufferedWriter);
                }
            }
    }).start();

    }

    public void classifyMessage(String messageFromServer){
        String[] tokenizedMessage = messageFromServer.split(" ", 2);
        //gets command from server TODO:find a better way

        if(tokenizedMessage[0].charAt(0) == '+'){
            switch (tokenizedMessage[0]){
                case "+CUNAME":
                    this.username = tokenizedMessage[1];
                    break;
            }
        }


        /*
            ----------------- Not working quite yet -----------------

        else if(tokenizedMessage[1].charAt(2) == '@'){
            if(tokenizedMessage[1].substring(2) == username){
            System.out.println("****" + tokenizedMessage[1].substring(2) + "****");}
            else{
                System.out.println("---" + tokenizedMessage[1].substring(2) + "---");}

        }
        */
        else{
            System.out.println(messageFromServer);
        }
    }

    public static void entranceText(){
        System.out.println("------------------------- =========== -------------------------");
        System.out.println("------------------------- Little Chat -------------------------");
        System.out.println("------------------------- =========== -------------------------");
    }


}
