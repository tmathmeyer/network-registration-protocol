This is an extension of the Synchronous-Network-Protocol found at https://github.com/tmathmeyer/Synchronous-Network-Protocol

this protocol works through the following steps to authenticate a user
client connects to server
client -> server "I would like to login"
server -> client "here are a random 512 bytes"
client -> server "I've hashed the password that the user entered, combined it with the salt, and re-hashed the result"
server -> client "that is the correct hash, this connection now has access to the functions of the server which have yet to be implemented"
client -> server {rest of the data}

for registration, the system works like this:
client connects to server
client -> server "here is my hashed password and my username"
server -> client "that is an availible username, i've save the hashed password"




An implementation of this system would look something like this:

public class TestClient extends Client{
	public Packet processPacket(Packet p){
		p = super.processPacket(p);
		if (p == null)
		{
			//the system is not authenticated, the packet was consumed in the process
		}
		else
		{
			//the system has been authenticated, the packet was not comsumed, and can be used
		}
		return null;
	}

	public void print(Object o){
		System.out.println(o.toString());
	}

	public byte[] generateSalt(){
		byte[] r = new byte[512];
		for(int i=0;i<512;i++)
		{
			r[i] = (byte)Math.random()*Byte.MAX_VALUE;
		}
		return r;
	}
}
