package mx.nic.jool.pktgen.main;

import java.io.IOException;
import java.util.Scanner;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.menu.MainMenu;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;

public class PacketGeneratorManual {

	public static void main(String[] args) throws IOException {
		handleMenuMode(new Scanner(System.in), new Fragment());
	}

	/**
	 * Modifies <code>frag</code> according to user input.
	 */
	public static void handleMenuMode(Scanner in, Fragment frag) throws IOException {
		MainMenu menu = new MainMenu();
		FieldScanner scanner = new FieldScanner(in);
		menu.handle(scanner, frag);

		/* Wrap up */
		Packet packet = new Packet();
		packet.add(frag);
		packet.postProcess();

		/* Output */
		boolean success = false;
		do {
			String outputFile = scanner.readLine("Output filename", "output");
			try {
				packet.export(outputFile);
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Try again: ");
			}
		} while (!success);
	}

}
