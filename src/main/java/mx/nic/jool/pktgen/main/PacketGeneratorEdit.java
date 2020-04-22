package mx.nic.jool.pktgen.main;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import mx.nic.jool.pktgen.pojo.Fragment;

public class PacketGeneratorEdit {

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);

		System.out.print("File: ");
		String fileName = scanner.nextLine();
		fileName = fileName.trim();
		if (!fileName.endsWith(".pkt"))
			fileName += ".pkt";

		PacketGeneratorManual.handleMenuMode(scanner, Fragment.load(new File(fileName)));
	}

}
