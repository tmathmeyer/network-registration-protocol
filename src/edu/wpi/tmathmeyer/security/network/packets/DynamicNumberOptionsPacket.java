package edu.wpi.tmathmeyer.security.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;

public class DynamicNumberOptionsPacket implements Packet{
	
	public byte[][] options;
	
	public DynamicNumberOptionsPacket(byte[][] b){
		this.options = b;
	}
	
	public DynamicNumberOptionsPacket(DataInputStream dis) throws IOException{
		int number = dis.readInt();
		options = new byte[number][];
		for(int i=0;i<number;i++){
			int size = dis.readInt();
			options[i] = new byte[size];
			for(int j=0;j<size;j++)
				options[i][j] = dis.readByte();
		}
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0x303;
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.write(this.getPacketID());
		for(int i=0;i<options.length;i++){
			dos.writeInt(options[i].length);
			dos.write(options[i]);
		}
		dos.flush();
	}
}
