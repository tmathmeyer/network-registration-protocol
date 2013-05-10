package edu.wpi.tmathmeyer.security.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import edu.wpi.tmathmeyer.security.CryptoInformation;
import edu.wpi.tmathmeyer.security.InformationPacket;
import edu.wpi.tmathmeyer.security.AuthPacket;
import edu.wpi.tmathmeyer.security.SkeinHash;

import edu.wpi.tmathmeyer.hbdb.HomeBase;
import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.protocol.chat.MessagePacket;
import edu.wpi.tmathmeyer.protocol.client.DataHandler;
import edu.wpi.tmathmeyer.protocol.client.DataReciever;

public class ServerSideClient implements DataHandler{
	private Socket s;
	private String name;
	private DataInputStream in;
    private DataOutputStream out;
    private boolean authenticated = false;
    private byte[] randomKey = random512Byte();
    private DataReciever dr;
    private HomeBase database;
	
	public ServerSideClient(Socket s) throws Exception{
		this.s = s;
		this.in = new DataInputStream(s.getInputStream());
        this.out = new DataOutputStream(s.getOutputStream());
        this.dr = new DataReciever(s, this, CryptoInformation.pkts);
        dr.addValidPacket(new MessagePacket("HELLO WORLD", "meh"));
        database = HomeBase.getInstance("NRPServerDatabase", "D:/databases");
        new Thread(this).start();
	}
	
	public void sendPacket(Packet p){
		try {
			p.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void killMe() throws Exception{
		try {
			in.close();
			out.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.authenticated = true;
		HashedClientManager.getInstance(8884).killClient(this);
	}

	@Override
	public void run() {
		this.startReciever(dr);
		
		while(!authenticated){}
		//now ur auth;
	}
	
	public byte[] random512Byte(){
		byte[] b = new byte[512];
		new Random().nextBytes(b);
		return b;
	}

	@Override
	public boolean authenticate(Packet p) throws Exception {
		if (p == null){
			System.out.println("starting authentication for new user at: "+s.getInetAddress());
			sendPacket(new AuthPacket(randomKey, HashedClientManager.getInstance(8884).getSocket().getInetAddress().toString(), (byte) 0x00));
		}
		else if (p instanceof AuthPacket){
			String username = new String(((AuthPacket) p).getUsername());
			if (((AuthPacket) p).getType() == 0x01){
				System.out.println("checking authentication for user at: "+s.getInetAddress());
				byte[] saltPass = new byte[1024];
				byte[] saltHash = new byte[512];
				String result = database.query("{function:get; callID:getPassword; params:{users, "+username+", hashedPassword}}");
				if (result.contains("SUCCESS")){
					result = result.substring(result.lastIndexOf(","));
					result = result.replaceAll("}","").trim();
					saltPass=this.stringToBytes(result);
					for(int i=0;i<512;i++)saltPass[i+512]=randomKey[i];
					SkeinHash.hash(saltPass, saltHash);
					boolean good = true;
					for(int i = 0; i < 512; i++)
						if (saltHash[i] != ((AuthPacket)p).getSalt()[i])good = false;
					sendPacket(new InformationPacket(good?"login successful":"invalid login credentials"));
					return good;
				}
				else return false;
			}
			else{
				String hash = this.bytesToString(((AuthPacket)p).getSalt());
				String call = "{function:insert; callID:register!; params:{users, "+username+", hashedPassword, "+ hash +"}}";
				String result =  database.query(call);
				System.out.println(result);
				return result.contains("SUCCESS");
				//return true;
			}
		}
		return false;
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
		//System.out.println(o);
	}

	@Override
	public Packet processPacket(Packet p) throws Exception {
		if (!authenticated){
			this.authenticated = this.authenticate(p);
			if (!this.authenticated)
				this.killMe();
			else
				sendPacket(new InformationPacket("login successful"));
		}
		return p;
	}

	@Override
	public void startReciever(DataReciever r) {
		new Thread(r).start();
		try {
			this.authenticate(null);
		} catch (Exception e) {e.printStackTrace();}
	}

	@Override
	public void kill() {
		try {
			killMe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String bytesToString(byte[] b){
		String s = "";
		for(Byte y : b)s+="$"+y;
		return s.substring(1);
	}
	
	public byte[] stringToBytes(String s){
		String[] b = s.split("$");
		byte[] k = new byte[b.length];
		for(int i=0;i<b.length;i++)
			k[i]=Byte.parseByte(b[i]);
		return k;
	}
}
