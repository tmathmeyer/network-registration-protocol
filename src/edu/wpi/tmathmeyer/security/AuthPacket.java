package edu.wpi.tmathmeyer.security;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;

public class AuthPacket implements Packet{

	private byte[] salt;
	private byte[] username;
	private byte type; //0x00 = register        0x01 = login

	public AuthPacket(byte[] salt, String username, byte type){
		this.salt = salt;
		this.setUsername(username.getBytes());
		this.type = type;
	}
	
	public AuthPacket(DataInputStream in) throws IOException{
		this.type = in.readByte();
		salt = new byte[512];
		for(int i = 0; i < 512; i++)
			salt[i] = in.readByte();
		short k = in.readShort();
		username = new byte[k];
		for(int i = 0; i < k; i++)
			username[i] = in.readByte();
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0xc3;
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(0xc3);
		out.writeByte(type);
		out.write(salt);
		out.writeShort(username.length);
		out.write(username);
		
	}

	public byte[] getSalt() {
		return salt;
	}

	/**
	 * @return the username
	 */
	public byte[] getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(byte[] username) {
		this.username = username;
	}

	/**
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(byte type) {
		this.type = type;
	}
	
}
