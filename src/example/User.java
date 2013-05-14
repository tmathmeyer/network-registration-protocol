package example;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.wpi.tmathmeyer.security.network.Client;
import edu.wpi.tmathmeyer.security.network.ClientManager;
import edu.wpi.tmathmeyer.security.network.ClientState;
import edu.wpi.tmathmeyer.security.network.SkeinHash;
//import edu.wpi.tmathmeyer.security.network.packets.DistinctOptionPacket;
//import edu.wpi.tmathmeyer.security.network.packets.DynamicNumberOptionsPacket;
import edu.wpi.tmathmeyer.security.network.packets.DynamicSizeOptionPacket;

public class User implements ClientManager{
	
	Client c;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		Socket s = new Socket("localhost", 8338);
		new User(s);
	}
	
	public User(Socket s) throws IOException{
		Client c = new Client(ClientState.ClientEmpty, this, s);
		this.addNewClient(c);
		this.c.setCurrentState(ClientState.Client1);
		byte[] pass = new byte[512];
		SkeinHash.hash("pass".getBytes(), pass);
		this.c.setPassword(pass);
		this.c.sendPacket(new DynamicSizeOptionPacket("ted".getBytes(), 0));
	}
	
	@Override
	public String getAvailName() {
		return "seeeeeexy";
	}

	@Override
	public void addNewClient(Client c) {
		this.c = c;
	}

}
