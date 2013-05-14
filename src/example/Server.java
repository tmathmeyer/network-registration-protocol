package example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.wpi.tmathmeyer.security.network.Client;
import edu.wpi.tmathmeyer.security.network.ClientManager;
import edu.wpi.tmathmeyer.security.network.ClientState;

public class Server implements ClientManager, Runnable{

	private ArrayList<Client> clients = new ArrayList<Client>();
	private ServerSocket serverSocket;
	private boolean running = true;
	
	
	public Server(int port) throws Exception{
		serverSocket = new ServerSocket(port, 500);
		Thread me = new Thread(this);
		me.start();	
	}
	
	
	public static void main(String[] args) throws Exception{
		new Server(8338);
	}
	
	@Override
	public String getAvailName() {
		return "GUEST"+Math.random();
	}

	@Override
	public void addNewClient(Client c) {
		
	}

	@Override
	public void run() {
        while(running) {
            try {
                Socket socket = serverSocket.accept();
                Client bob = new Client(ClientState.Server1, this, socket);
                clients.add(bob);
            }
            catch(Exception e){
            	running = false;
            	try {
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(0);
				}
            }
        } 
    }

}
