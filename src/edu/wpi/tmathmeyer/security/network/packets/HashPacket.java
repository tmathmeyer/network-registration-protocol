package edu.wpi.tmathmeyer.security.network.packets;

public class HashPacket extends DynamicSizeOptionPacket{

	public HashPacket(byte[] salt) {
		super(salt, 0);
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0x304;
	}
}
