package mx.nic.jool.pktgen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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

import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.proto.PacketContentFactory;
import mx.nic.jool.pktgen.proto.optionsdata6.OptionDataTypes;

public class FieldScanner implements AutoCloseable {

	private Scanner scanner;
	/**
	 * Transparently prints stdin to a file, so the user can pipe it to another
	 * execution of this program.
	 */
	private PrintStream inputRecord;
	
	public FieldScanner(Scanner scanner) throws FileNotFoundException {
		this.scanner = scanner;
		this.inputRecord = new PrintStream(new FileOutputStream("src.txt"));
	}
	
	private String readLine() {
		int indexOfTag;
		String input = scanner.nextLine().trim();
		
		inputRecord.println(input);
		
		indexOfTag = input.indexOf("#");
		if (indexOfTag > 0)
			input = input.substring(0, indexOfTag);
		else if (indexOfTag == 0)
			input = "";
		
		return input.trim();
	}
	
	public int readInt(String prefix) {
		System.out.print(prefix + ": ");
		
		do {
			try {
				String input = readLine();
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
				String input = readLine();
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
				String input = readLine();
				System.out.println();
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
				String input = readLine();
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
				String input = readLine();
				if (input.isEmpty())
					return defaultValue;
				return Boolean.parseBoolean(input);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.print("Give me the boolean again (" + printableDefault + "): ");
			}
		} while (true);
	}
	
	public Integer readProtocol(String prefix, String defaultCaption) {
		PacketContentFactory.printIntProtocols();
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
	
	private InetAddress readAddress(String prefix) {
		System.out.print(prefix + ": ");
		
		do {
			try {
				String input;
				do {
					input = readLine();
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
		String result = readLine();
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
	
	public Object read(String prefix, String defaultValue, Type type) {
		
		switch (type) {
			case INT:
				return readInt(prefix, Integer.parseInt(defaultValue));
			case INTEGER:
			case IPV4_OPTION_TYPE:
				return readInteger(prefix, defaultValue);
			case LONG:
				return readLong(prefix, Long.parseLong(defaultValue));
			case BOOLEAN:
				if (defaultValue == null || defaultValue.equalsIgnoreCase("null"))
					return readBoolean(prefix, null);
				else
					return readBoolean(prefix, Boolean.parseBoolean(defaultValue));
			case PROTOCOL:
				return readProtocol(prefix, defaultValue);
			case OPTION_DATA_TYPE:
				return readOptionDataType(prefix, defaultValue);
			case INET4ADDRESS:
				return readAddress4(prefix);
			case INET6ADDRESS:
				return readAddress6(prefix);
			case STRING:
				return readLine(prefix, defaultValue);
			case FILE:
				return readFile();
			default:
				return null;
		}
	}
	
	@Override
	public void close() {
		inputRecord.close();
	}
}
