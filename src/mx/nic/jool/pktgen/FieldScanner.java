package mx.nic.jool.pktgen;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import mx.nic.jool.pktgen.proto.Protocol;
import mx.nic.jool.pktgen.proto.optionsdata4.Ipv4Options;
import mx.nic.jool.pktgen.proto.optionsdata6.OptionDataTypes;

public class FieldScanner {

	private Scanner scanner;
	
	public FieldScanner(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public int readInt(String prefix) {
		System.out.print(prefix + ": ");
		
		do {
			try {
				String input = scanner.nextLine().trim();
				if (input.isEmpty())
					continue;
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.print("Give me the number again: ");
			}
		} while (true);
	}
	
	public int readInt(String prefix, int defaultValue) {
		System.out.print(prefix + " (" + defaultValue + "): ");
		
		do {
			try {
				String input = scanner.nextLine().trim();
				if (input.isEmpty())
					return defaultValue;
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.print("Give me the number again (" + defaultValue + "): ");
			}
		} while (true);
	}
	
	public Integer readInteger(String prefix, String defaultCaption) {
		System.out.print(prefix + " (" + defaultCaption + "): ");
		
		do {
			try {
				String input = scanner.nextLine().trim();
				if (input.isEmpty())
					return null;
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.print("Give me the number again: ");
			}
		} while (true);
	}
	
	public long readLong(String prefix, long defaultValue) {
		System.out.print(prefix + " (" + defaultValue + "): ");
		
		do {
			try {
				String input = scanner.nextLine().trim();
				if (input.isEmpty())
					return defaultValue;
				return Long.parseLong(input);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.print("Give me the number again (" + defaultValue + "): ");
			}
		} while (true);
	}
	
	public Boolean readBoolean(String prefix, Boolean defaultValue) {
		String printableDefault = (defaultValue != null) ? defaultValue.toString() : "auto";
		System.out.print(prefix + " (" + printableDefault + "): ");
		
		do {
			try {
				String input = scanner.nextLine().trim();
				if (input.isEmpty())
					return defaultValue;
				return Boolean.parseBoolean(input);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.print("Give me the boolean again (" + printableDefault + "): ");
			}
		} while (true);
	}
	
	private void printProtocols() {
		for (Protocol proto : Protocol.values())
			System.out.printf("\t %s = %d\n", proto, proto.toWire());
//			System.out.println("\t" + proto + " = " + proto.toWire());
	}
	
	public Integer readProtocol(String prefix, String defaultCaption) {
		printProtocols();
		return readInteger(prefix, defaultCaption);
	}
	
	private void printOptionDataTypes() {
		for (OptionDataTypes optionType : OptionDataTypes.values())
			System.out.println("\t" + optionType + " = " + optionType.toWire());
	}
	
	public Integer readOptionDataType(String prefix, String defaultCaption) {
		printOptionDataTypes();
		return readInteger(prefix, defaultCaption);
	}
	
	private void printIpv4Options() {
		for (Ipv4Options ipv4Option : Ipv4Options.values()) {
			System.out.println("\t"+ ipv4Option + " = " + ipv4Option.toWire());
		}
	}
	
	public Integer readIpv4OptionTypes(String prefix, String defaultCaption) {
		printIpv4Options();
		return readInteger(prefix, defaultCaption);
	}
	
	private InetAddress readAddress(String prefix) {
		System.out.print(prefix + ": ");
		
		do {
			try {
				String input;
				do {
					input = scanner.nextLine().trim();
				} while (input.isEmpty());
				return InetAddress.getByName(input);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.print("Give me the address again: ");
			}
		} while (true);
	}
	
	public Inet4Address readAddress4(String prefix) {
		InetAddress result;
		do {
			result = readAddress(prefix);
			if (result instanceof Inet4Address) {
				return (Inet4Address) result;
			}
			System.out.println("I need an IPv4 address!");
		} while (true);
	}
	
	public Inet6Address readAddress6(String prefix) {
		InetAddress result;
		do {
			result = readAddress(prefix);
			if (result instanceof Inet6Address) {
				return (Inet6Address) result;
			}
			System.out.println("I need an IPv6 address!");
		} while (true);
	}

	public String readLine(String prefix, String defaultValue) {
		System.out.print(prefix + " (" + defaultValue + "): ");
		String result = scanner.nextLine().trim();
		return result.isEmpty() ? defaultValue : result;
	}
	
	public byte[] readFile() {
		Path dir = Paths.get("input");
		String stringPath;
		byte[] file;
		int attempts = 4;
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)){
			System.out.println("Archivos en la carpeta input:");
			for (Path path : stream) {
				System.out.println(path.getFileName());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("No se puede leer la carpeta input.");
			return null;
		}
		
		do {
			stringPath = this.readLine("Archivo", null);
			
			dir = Paths.get("input/" + stringPath);
			if (Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
				break;
			}
			attempts--;
			System.err.println("Archivo no existe. Intente nuevamente."
					+ "\n" + attempts + " intentos restantes.");
		} while (attempts > 0);
		
		if (stringPath == null) {
			return null;
		}
		
		try {
			file = Files.readAllBytes(dir);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return file;
	}

}
