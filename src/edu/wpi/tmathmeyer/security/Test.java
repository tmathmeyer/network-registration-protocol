package edu.wpi.tmathmeyer.security;

public class Test {
	public static void main(String[] atgs){
		byte[] shit = new byte[512];
		byte[] crap = "password".getBytes();
		SkeinHash.hash(crap, shit);
		System.out.println(new String(shit));
	}
}
