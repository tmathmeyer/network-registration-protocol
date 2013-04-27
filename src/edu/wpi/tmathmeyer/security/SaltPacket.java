package edu.wpi.tmathmeyer.security;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;

public class SaltPacket implements Packet{

	private byte[] salt;

	public SaltPacket(byte[] salt){
		this.salt = salt;
	}
	
	public SaltPacket(DataInputStream in) throws IOException{
		salt = new byte[512];
		for(int i = 0; i < 512; i++)
			salt[i] = in.readByte();
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0xc3;
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(0xc3);
		out.write(salt);
		out.flush();
	}

	public byte[] getSalt() {
		return salt;
	}
	
}
