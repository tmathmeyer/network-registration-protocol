package edu.wpi.tmathmeyer.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.wpi.tmathmeyer.security.CryptoInformation;
import edu.wpi.tmathmeyer.security.InformationPacket;
import edu.wpi.tmathmeyer.security.SaltPacket;
import edu.wpi.tmathmeyer.security.SkeinHash;

import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.protocol.client.DataHandler;
import edu.wpi.tmathmeyer.protocol.client.DataReciever;

public class HashedClient implements DataHandler{
	
	private DataReciever dr;
	private Socket s;
	private DataOutputStream dos;
	
	private boolean authPending = true;
	private boolean authGranted = false;
	
	private int port;
	private String host;
	
	private byte[] tus;
	
	public HashedClient(String host, int port) throws UnknownHostException, IOException{
		this.port = port;
		this.host = host;
	}
	
	public void login(String username, String password) throws IOException{
		
		this.tus = password.getBytes();
		
		this.s = new Socket(host, port);
		this.dos = new DataOutputStream(s.getOutputStream());
		this.dr = new DataReciever(s, this, CryptoInformation.pkts);
		
		new Thread(this).run();
	}
	
	
	
	@Override
	public void run() {
		this.startReciever(dr);
		System.out.println("Client Starting");
		while(authPending && !authGranted)
			if (System.currentTimeMillis()%2000000 == 0)
				System.out.println("pending...");
		while(authGranted){}
	}

	@Override
	public boolean authenticate(Packet p) throws Exception {
		System.out.println(p.getClass());
		if (p instanceof SaltPacket){
			byte[] passHash = new byte[512];
			byte[] saltPass = new byte[1024];
			byte[] saltHash = new byte[512];
			SkeinHash.hash(tus, passHash);
			for(int i=0;i<512;i++)saltPass[i]=passHash[i];
			for(int i=0;i<512;i++)saltPass[i+512]=((SaltPacket)p).getSalt()[i];
			SkeinHash.hash(saltPass, saltHash);
			new SaltPacket(saltHash).write(dos);
			return true;
		}
		
		if (p instanceof InformationPacket){
			String message = ((InformationPacket)p).getInformation();
			System.out.println(message);
			if (message.equals("login successful")){
				System.out.println("meh");
				authGranted = true;
			}
			authPending = false;
		}
		return false;
	}

	@Override
	public void closeOutStream() throws Exception {
		this.dos.flush();
		this.dos.close();
	}

	@Override
	public DataOutputStream getByteOutputStream() {
		return this.dos;
	}

	@Override
	public Socket getSocket() {
		return s;
	}

	@Override
	public byte getVersionID() {
		return 1;
	}

	@Override
	public void print(Object arg0) {
		//System.out.println(arg0);
	}

	@Override
	public Packet processPacket(Packet p) throws Exception {
		if (authPending && !authGranted){
			this.authenticate(p);
			return null;
		}
		return p;

	}

	@Override
	public void startReciever(DataReciever r) {
		new Thread(r).start();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		new HashedClient("localhost", 8884).login("admin", "password");
	}
}
