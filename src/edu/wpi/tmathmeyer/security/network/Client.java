package edu.wpi.tmathmeyer.security.network;

import java.io.DataOutputStream;
import java.net.Socket;

import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.protocol.client.DataHandler;
import edu.wpi.tmathmeyer.protocol.client.DataReciever;

public class Client implements DataHandler{

	private DataOutputStream dataOut;
	private Socket connection;
	private boolean isAuthenticated;
	private ClientState currentState;
	
	
	
	
	
	
	
	
	public ClientState getCurrentState(){
		return this.currentState;
	}
	
	
	
	@Override
	public void run(){
		
	}

	@Override
	public boolean authenticate(Packet p) throws Exception {
		
		
		
		return false;
	}

	@Override
	public void closeOutStream() throws Exception {
		this.dataOut.close();
	}

	@Override
	public DataOutputStream getByteOutputStream() {
		return this.dataOut;
	}

	@Override
	public Socket getSocket() {
		return this.connection;
	}

	@Override
	public byte getVersionID() {
		return 1;
	}

	@Override
	public void kill() {
		try {
			this.closeOutStream();
		} catch (Exception e) {
			this.print("cannot be closed: ");
			this.print(e.getMessage());
		}
	}

	@Override
	public void print(Object arg0) {
		System.out.println(arg0);
	}

	@Override
	public Packet processPacket(Packet p) throws Exception {
		if (! this.isAuthenticated){
			this.isAuthenticated = this.authenticate(p);
		}
		else{
			
		}
		return p;
	}

	@Override
	public void startReciever(DataReciever dr) {
		new Thread(dr).start();
	}
	
	
}
