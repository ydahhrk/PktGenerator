package mx.nic.jool.pktgen;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import mx.nic.jool.pktgen.parser.AutoParser;
import mx.nic.jool.pktgen.parser.ManualParser;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.PacketContentFactory;

public class PacketGen {

	public static void main(String[] args) throws IOException {
		String mode = (args.length > 0) ? args[0] : "auto";

		switch (mode) {
		case "auto":
			handleAutoMode();
			break;
		case "manual":
			handleManualMode();
			break;
		case "edit":
			if (args.length < 2) {
				System.err.println("I need a packet file as second argument.");
				return;
			}
			handleEditMode(args[1]);
			break;
		case "random":
			handleRandomMode();
			break;
		default:
			System.err.println("Sorry; unknown operation mode. Try 'auto', 'manual', 'edit' or 'random'.");
			break;
		}
	}

	private static PacketContent findPreviousContent(Fragment frag, String headerIndex) {
		int index;
		try {
			index = Integer.parseInt(headerIndex);
		} catch (NumberFormatException e) {
			return null;
		}

		return frag.get(index);
	}

	private static void handleNewPacketMode(Parser parser, Fragment frag) throws IOException {
		try (FieldScanner scanner = new FieldScanner(new Scanner(System.in))) {
			Packet packet = new Packet();

			/* Build the packet. */
			do {
				System.out.println();
				PacketContentFactory.printStringProtocols();
				frag.print();

				String nextProto = scanner.readLine("Next", "exit");
				if ("exit".equals(nextProto))
					break;

				PacketContent newContent = PacketContentFactory.forName(nextProto);
				if (newContent != null) {
					parser.handleContent(newContent, scanner);
					frag.add(newContent);
					continue;
				}

				PacketContent oldContent = findPreviousContent(frag, nextProto);
				if (oldContent != null) {
					parser.handleContent(oldContent, scanner);
					continue;
				}

				System.err.println("Sorry; I don't understand you. Please request one of the options shown.");
			} while (true);

			/* Wrap up */
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

	private static void handleAutoMode() throws IOException {
		handleNewPacketMode(new AutoParser(), new Fragment());
	}

	private static void handleManualMode() throws IOException {
		handleNewPacketMode(new ManualParser(), new Fragment());
	}

	private static void handleEditMode(String string) throws IOException {
		handleNewPacketMode(new AutoParser(), Fragment.load(new File(string)));
	}

	private static void handleRandomMode() {
		// TODO Auto-generated method stub

	}

}
