package edu.wpi.tmathmeyer.security.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import edu.wpi.tmathmeyer.security.CryptoInformation;
import edu.wpi.tmathmeyer.security.InformationPacket;
import edu.wpi.tmathmeyer.security.SaltPacket;
import edu.wpi.tmathmeyer.security.SkeinHash;

import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.protocol.client.DataHandler;
import edu.wpi.tmathmeyer.protocol.client.DataReciever;

public class ServerSideClient implements DataHandler{
	private Socket s;
	private String name;
	private int userID;
	private DataInputStream in;
    private DataOutputStream out;
    private boolean authenticated = false;
    private byte[] randomKey = random512Byte();
    private DataReciever dr;
	
	public ServerSideClient(Socket s) throws Exception{
		this.s = s;
		this.in = new DataInputStream(s.getInputStream());
        this.out = new DataOutputStream(s.getOutputStream());
        this.dr = new DataReciever(s, this, CryptoInformation.pkts);
        new Thread(this).start();
		this.authenticate(null);
		
	}
	
	public void sendPacket(Packet p){
		try {
			p.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void killMe(){
		try {
			in.close();
			out.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.startReciever(dr);
		while(!authenticated){}
		System.out.println("WIN");
	}
	
	public byte[] random512Byte(){
		byte[] b = new byte[512];
		new Random().nextBytes(b);
		return b;
	}

	@Override
	public boolean authenticate(Packet p) throws Exception {
		if (p == null){
			System.out.println("starting authentication");
			sendPacket(new SaltPacket(randomKey));
		}
		else if (p instanceof SaltPacket){
			System.out.println("checking authentication");
			byte[] saltPass = new byte[1024];
			byte[] saltHash = new byte[512];
			for(int i=0;i<512;i++)saltPass[i]=HashedClientManager.password[i];
			for(int i=0;i<512;i++)saltPass[i+512]=randomKey[i];
			SkeinHash.hash(saltPass, saltHash);
			boolean good = true;
			for(int i = 0; i < 512; i++)
				if (saltHash[i] != ((SaltPacket)p).getSalt()[i])good = false;
			sendPacket(new InformationPacket(good?"login successful":"invalid login credentials"));
		}
			
		return true;
	}

	@Override
	public void closeOutStream() throws Exception {
		this.out.flush();
		this.out.close();
	}

	@Override
	public DataOutputStream getByteOutputStream() {
		return this.out;
	}

	@Override
	public Socket getSocket() {
		return this.s;
	}

	@Override
	public byte getVersionID() {
		return 1;
	}

	@Override
	public void print(Object o) {
		System.out.println(o);
	}

	@Override
	public Packet processPacket(Packet p) throws Exception {
		if (!authenticated)
			this.authenticate(p);
		return p;
	}

	@Override
	public void startReciever(DataReciever r) {
		new Thread(r).start();
	}
}
