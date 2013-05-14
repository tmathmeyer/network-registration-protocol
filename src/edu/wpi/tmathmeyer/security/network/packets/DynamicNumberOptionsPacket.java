/*******************************************************************************
 * Copyright (c) 2013 Ted Meyer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ted Meyer - initial API and implementation
 ******************************************************************************/
package edu.wpi.tmathmeyer.security.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;

public class DynamicNumberOptionsPacket implements Packet{
	
	public byte[][] options;
	
	public DynamicNumberOptionsPacket(byte[][] b, int deambiguer){
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
		dos.writeInt(this.options.length);
		for(int i=0;i<options.length;i++){
			dos.writeInt(options[i].length);
			dos.write(options[i]);
		}
		dos.flush();
	}
}
