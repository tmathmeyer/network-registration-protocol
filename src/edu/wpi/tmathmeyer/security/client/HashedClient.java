package edu.wpi.tmathmeyer.security.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.wpi.tmathmeyer.security.CryptoInformation;
import edu.wpi.tmathmeyer.security.InformationPacket;
import edu.wpi.tmathmeyer.security.AuthPacket;
import edu.wpi.tmathmeyer.security.SkeinHash;

import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.protocol.chat.MessagePacket;
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
	private String username;
	
	public HashedClient(String host, int port) throws UnknownHostException, IOException{
		this.port = port;
		this.host = host;
	}
	
	public void login(String username, String password) throws IOException{
		
		this.tus = password.getBytes();
		this.username = username;
		
		this.s = new Socket(host, port);
		this.dos = new DataOutputStream(s.getOutputStream());
		this.dr = new DataReciever(s, this, CryptoInformation.pkts);
		dr.addValidPacket(new MessagePacket("HELLO WORLD", "meh"));
		
		new Thread(this).run();
	}
	
	
	
	@Override
	public void run() {
		this.startReciever(dr);
		System.out.println("Client Starting");
	}

	@Override
	public boolean authenticate(Packet p) throws Exception {
		if (p instanceof AuthPacket){
			byte[] passHash = new byte[512];
			byte[] saltPass = new byte[1024];
			byte[] saltHash = new byte[512];
			SkeinHash.hash(tus, passHash);
			for(int i=0;i<512;i++)saltPass[i]=passHash[i];
			for(int i=0;i<512;i++)saltPass[i+512]=((AuthPacket)p).getSalt()[i];
			SkeinHash.hash(saltPass, saltHash);
			new AuthPacket(passHash, username, (byte) 0x00).write(dos);
			//new AuthPacket(saltHash, username, (byte) 0x01).write(dos);
			return true;
		}
		
		if (p instanceof InformationPacket){
			String message = ((InformationPacket)p).getInformation();
			if (message.equals("login successful"))
				authGranted = true;
			authPending = false;
		}
		return false;
	}
	
	public void print(byte[] b){
		for(Byte bb : b)System.out.println(bb);
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
		if (authPending && !authGranted)
			this.authenticate(p);
		return p;

	}

	@Override
	public void startReciever(DataReciever r) {
		new Thread(r).start();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		new HashedClient("localhost", 8884).login("admin", "password");
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}
}
