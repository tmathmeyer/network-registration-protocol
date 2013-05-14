package edu.wpi.tmathmeyer.security.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.protocol.client.DataHandler;
import edu.wpi.tmathmeyer.protocol.client.DataReciever;
import edu.wpi.tmathmeyer.security.network.memory.UserFileManager;
import edu.wpi.tmathmeyer.security.network.packets.DistinctOptionPacket;
import edu.wpi.tmathmeyer.security.network.packets.DynamicNumberOptionsPacket;
import edu.wpi.tmathmeyer.security.network.packets.DynamicSizeOptionPacket;
import edu.wpi.tmathmeyer.security.network.packets.HashPacket;


public class Client implements DataHandler{

	private DataOutputStream dataOut;
	private Socket connection;
	private boolean isAuthenticated;
	@SuppressWarnings("unused")
	private boolean isGuest;
	private ClientState currentState;
	
	private String username;
	private String email;
	private byte[] hashedPassword;
	private byte[] salt;
	
	private DataReciever dr;
	private ClientManager holder;
	private byte[][] unfilledRequirements; //CLIENTSIDE ONLY LOL
	
	public Client(ClientState cs, ClientManager cm, Socket s) throws IOException{
		this.holder = cm;
		this.currentState = cs;
		this.connection = s;
		this.dataOut = new DataOutputStream(s.getOutputStream());
		this.dr = new DataReciever(s, this, ClientState.allPackets);
		new Thread(this).start();
	}
	
	
	
	public void setCurrentState(ClientState cs){
		this.currentState = cs;
	}
	
	
	
	
	
	
	public ClientState getCurrentState(){
		return this.currentState;
	}
	
	
	
	@Override
	public void run(){
		this.startReciever(dr);
		while(true);
	}

	@Override
	public boolean authenticate(Packet p) throws Exception {
		//the packet is good for this state. otherwise, do nothing
		if (this.currentState.isAllowablePacketType(p)){
			//for case (server has yet to hear ANYTHING from the client)
			if (this.currentState.equals(ClientState.Server1)){
				if (p instanceof DistinctOptionPacket){
					DistinctOptionPacket castedPacket = (DistinctOptionPacket)p;
					if (castedPacket.option == 0){
						//sets up client as a guest
						this.username = this.holder.getAvailName();
						this.isGuest = true;
						this.currentState = ClientState.Server5;
					}
					else if (castedPacket.option == 1){
						// this user wants to register.
						this.currentState = ClientState.Server3;
					}
				}
				else{ //it is a DSOP
					byte[] usr = ((DynamicSizeOptionPacket)p).option;
					username = new String(usr);
					if (UserFileManager.availableUsername(username)){
						this.currentState = ClientState.Server4;
						//send away a packet telling them the username was vacated
					}
					else{
						this.currentState = ClientState.Server2;
						salt = generateSalt();
						// also send a hash to the client
					}
				}
			}
			//for case:  server has recieved the username from the player.
			// 			 the server has either replied with a hash and is waiting,
			//			 or, the server has replied saying that the username is 
			//           non-existant, and is waiting for a response on whether or not to register
			else if (this.currentState.equals(ClientState.Server2)){
				if (p instanceof DistinctOptionPacket){
					this.currentState = ClientState.Server1;
				}
				else{ //otherwise, it is a hashed password
					byte[] usersHashedPassword = ((HashPacket)p).option;
					byte[] savedUserPassword = UserFileManager.getHashedPassword(username);
					for(int i=0;i<512;i++)savedUserPassword[i] = (byte) (savedUserPassword[i]+salt[i]);
					byte[] generatedPassword = new byte[512];
					SkeinHash.hash(savedUserPassword, generatedPassword);
					if (usersHashedPassword == generatedPassword){
						//login was good. notify the user
						this.currentState = ClientState.Server5;
					}
					else{
						//login was bad. notify the user
						this.currentState = ClientState.Server2;
					}
				}
			}
			else if (this.currentState.equals(ClientState.Server3)){
				boolean u_bool = username==null;
				boolean p_bool = hashedPassword==null;
				boolean e_bool = email==null;
				boolean s_bool = salt==null;
				if (s_bool)salt=generateSalt();
				byte[][] data = ((DynamicNumberOptionsPacket)p).options;
				//fill in sent data
				for(int i=0;i<data.length;i++){
					if (u_bool){
						username = new String(data[i]);
						u_bool=false;
					}
					else if (p_bool){
						hashedPassword = data[i];
						p_bool=false;
					}
					else if (e_bool){
						email = new String(data[i]);
						e_bool=false;
					}
				}
				//send remaining info request
				byte[][] reqinfo = new byte[(u_bool?1:0) + (p_bool?1:0) + (e_bool?1:0)][];
				if (reqinfo.length == 0){
					if (UserFileManager.register(username, hashedPassword))
						this.currentState = ClientState.Server5;
					else{
						this.currentState = ClientState.Server1;
						this.sendPacket(new DistinctOptionPacket((byte) 8));
					}
					//notify the user that they have registered
				}
				else{
					for(int i=0;i<reqinfo.length;i++){
						if (u_bool){
							reqinfo[i] = "username".getBytes();
							u_bool=false;
						}
						else if (p_bool){
							reqinfo[i] = "password".getBytes();
							p_bool=false;
						}
						else if (e_bool){
							reqinfo[i] = "email".getBytes();
							e_bool=false;
						}
					}
					//send reqinfo to the user, do not change state
				}
			}
			else if (this.currentState.equals(ClientState.Server4)){
				//always a simple option
				DistinctOptionPacket dop = (DistinctOptionPacket)p;
				//do you want to register?
				//0 is no
				//1 is yes
				if (dop.option == 0){
					this.currentState = ClientState.Server1;
					username = null;
					email = null;
					hashedPassword = null;
					salt = generateSalt();
				}
				else
					this.currentState = ClientState.Server3;
			}
			if (this.currentState.equals(ClientState.Server5)){
				this.sendPacket(new DistinctOptionPacket((byte) 9));
				return true;
			}
			
			
			
			
			
			
			
			
			
			
			if (this.currentState.equals(ClientState.Client1)){
				if (p instanceof HashPacket){
					HashPacket hp = (HashPacket)p;
					salt = hp.option;
					for(int i=0;i<512;i++)hashedPassword[i]+=salt[i];
					byte[] val = new byte[512];
					SkeinHash.hash(hashedPassword, val);
					for(int i=0;i<512;i++)hashedPassword[i]-=salt[i];
					this.currentState = ClientState.Client2;
					this.sendPacket(new HashPacket(val));
				}
				else{
					this.currentState = ClientState.ClientEmpty;
				}
			}
			else if (this.currentState.equals(ClientState.Client2)){
				DistinctOptionPacket dos = (DistinctOptionPacket)p;
				this.currentState = ClientState.ClientEmpty;
				if (dos.option == 9){
					this.print("you have registered as a guest");
					return true;
				}
				else if (dos.option == 8){
					this.print("that username has already been registered");
					return true;
				}
				else{
					this.print("send moar");
				}
			}
			else if (this.currentState.equals(ClientState.Client3)){
				if (p instanceof DynamicNumberOptionsPacket){
					DynamicNumberOptionsPacket hp = (DynamicNumberOptionsPacket)p;
					this.unfilledRequirements = hp.options;
				}
				else{
					this.currentState = ClientState.ClientEmpty;
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void sendPacket(Packet p){
		try {
			p.write(getByteOutputStream());
		} catch (IOException e) {
			this.print(e);
		}
	}
	
	
	
	
	
	
	
	public byte[] generateSalt(){
		return new byte[512];
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










	/**
	 * @return the unfilledRequirements
	 */
	public byte[][] getUnfilledRequirements() {
		return unfilledRequirements;
	}
}
