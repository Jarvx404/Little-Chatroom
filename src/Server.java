package src;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket ServerSocket;

    public Server(ServerSocket serverSocket){
        this.ServerSocket = serverSocket;
    }

    //accepts connections continuously and creates a separate thread to handle each connection
    public void startServer(){
        try{
            System.out.println("Server is up and running");
            while(!ServerSocket.isClosed()){
                Socket socket = ServerSocket.accept();
                System.out.println(socket);
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            closeServerSocket();
        }


    }


    public void closeServerSocket(){
        try{
            ServerSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }


}
