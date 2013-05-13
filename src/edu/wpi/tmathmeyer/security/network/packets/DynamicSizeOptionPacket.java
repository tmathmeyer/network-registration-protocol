package edu.wpi.tmathmeyer.security.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;

public class DynamicSizeOptionPacket implements Packet{
	
	public byte[] option;
	
	public DynamicSizeOptionPacket(byte[] b, int deambiguifier){
		this.option = b;
	}
	
	public DynamicSizeOptionPacket(DataInputStream dis) throws IOException{
		int size = dis.readByte();
		this.option = new byte[size];
		for(int i=0;i<size;i++)
			this.option[i] = dis.readByte();
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0x302;
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.write(this.getPacketID());
		dos.writeInt(option.length);
		dos.write(this.option);
		dos.flush();
	}
	
}
