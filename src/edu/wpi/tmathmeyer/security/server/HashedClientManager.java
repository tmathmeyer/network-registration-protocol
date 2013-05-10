package edu.wpi.tmathmeyer.security.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.wpi.tmathmeyer.hbdb.HomeBase;
import edu.wpi.tmathmeyer.security.SkeinHash;


public class HashedClientManager implements Runnable{
	private ServerSocket serverSocket;
	private boolean running = true;
	private byte[] password = new byte[512];
	private ArrayList<ServerSideClient> clients = new ArrayList<ServerSideClient>();
	private HomeBase database;
	
	
	public static HashedClientManager hcm;
	public static HashedClientManager getInstance(int port) throws Exception{
		if (hcm==null)hcm = new HashedClientManager(port);
		return hcm;
	}
	
	
	
	
	
	private HashedClientManager(int port) throws Exception{
		hcm = this;
		serverSocket = new ServerSocket(port, 500);
		database = HomeBase.getInstance("NRPServerDatabase", "D:/databases");
		database.query("{function:maketable; callID:startup; params:{users, acctname, hashedPassword, lastlogin, creationdate, statusmessage}}");
		new Thread(this).start();
		SkeinHash.hash("password".getBytes(), password);
	}
	
	public void run() {
        while(running) {
            try {
                Socket socket = serverSocket.accept();
                ServerSideClient ssc = new ServerSideClient(socket);
                clients.add(ssc);
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
	
	public synchronized void killClient(ServerSideClient c) {
		if (this.clients.remove(c)){}
    }
	
	public int getNumberOfUsers(){
		return this.clients.size();
	}
	
	public ServerSocket getSocket(){
		return this.serverSocket;
	}
	
	
	public void kill() throws Exception{
		running = false;
		serverSocket.close();
		for(ServerSideClient c : this.clients)c.killMe();
	}
	
	public static void main(String[] args) throws Exception{
		new HashedClientManager(8884);
	}
}
