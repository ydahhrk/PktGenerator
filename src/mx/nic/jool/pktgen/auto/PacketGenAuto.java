package mx.nic.jool.pktgen.auto;

import java.io.File;
import java.io.IOException;

import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;

public class PacketGenAuto {

	public static void main(String[] args) throws IOException {
		BasicTests basic;
		
		cleanDir("result/stateful/sender/");
		cleanDir("result/stateful/receiver/");
		cleanDir("result/stateless/sender/");
		cleanDir("result/stateless/receiver/");

		// -----------------------------------
		
		basic = new BasicTests();
		basic.generateTests();

		// -----------------------------------
		Ipv4Header.stateless();
		Ipv6Header.stateless();

		FragTests frag = new FragTests();
		frag.generateTests("result/stateless/");

		Ipv4Header.stateful();
		Ipv6Header.stateful();

		frag = new FragTests();
		frag.generateTests("result/stateful/");

		// -----------------------------------

		System.out.println("Done.");
	}

	private static void cleanDir(String dirName) {
		File dir = new File(dirName);

		if (!dir.exists() && !dir.mkdirs())
			System.out.println("No puedo crear el dir " + dirName);
		
		for (File file : dir.listFiles()) {
			if (!file.delete()) {
				System.out.println("rm failed on file " + file.getName());
			}
		}
	}

}
