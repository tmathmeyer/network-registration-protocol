package edu.wpi.tmathmeyer.security;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;

public class InformationPacket implements Packet {

	private String information;
	long latency;
	
	public InformationPacket(String info){
		this.setInformation(info);
	}
	
	public InformationPacket(DataInputStream in) throws IOException{
		latency = System.currentTimeMillis() - in.readLong();
		short len = in.readShort();
		byte[] inf = new byte[len];
		for(int i=0;i<len;i++)
			inf[i]=in.readByte();
		this.setInformation(new String(inf));
		
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0xc4;
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(0xc4);
		out.writeLong(System.currentTimeMillis());
		out.writeShort(getInformation().getBytes().length);
		out.write(getInformation().getBytes());
		out.flush();
	}
	
	public long getLatency(){
		return latency;
	}

	/**
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * @param information the information to set
	 */
	public void setInformation(String information) {
		this.information = information;
	}

}
