package mx.nic.jool.pktgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

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
		System.out.println();

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

	public Integer readInteger(String prefix) {
		System.out.print(prefix + " (auto): ");

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

	public boolean readBool(String prefix, boolean defaultValue) {
		System.out.print(prefix + " (" + defaultValue + "): ");

		String input = readLine();
		if (input.isEmpty())
			return defaultValue;
		return Boolean.parseBoolean(input);
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

	public Integer readProtocol(String prefix) {
		PacketContentFactory.printIntProtocols();
		return readInteger(prefix);
	}

	public Integer readOptionDataType(String prefix, String defaultCaption) {
		for (OptionDataTypes optionType : OptionDataTypes.values())
			System.out.println("\t" + optionType + " = " + optionType.toWire());
		return readInteger(prefix);
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
		do {
			String stringPath = readLine("File", "cancel");
			if ("cancel".equals(stringPath))
				return null;
			try {
				return Files.readAllBytes(Paths.get(stringPath));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} while (true);
	}

	public Object read(Object object, Field field)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Class<?> clazz = field.getType();
		String prefix = field.getName();

		if (clazz == int.class)
			return readInt(prefix, field.getInt(object.getClass().newInstance()));
		if (clazz == Integer.class)
			return readInteger(prefix);
		
		if (clazz == long.class)
			return readLong(prefix, field.getLong(object.getClass().newInstance()));
		
		if (clazz == boolean.class)
			return readBoolean(prefix, field.getBoolean(object.getClass().newInstance()));
		if (clazz == Boolean.class)
			return readBoolean(prefix, (Boolean) field.get(object.getClass().newInstance()));
		
		if (clazz == String.class)
			return readLine(prefix, (String) field.get(object.getClass().newInstance()));
		if (clazz == Inet4Address.class)
			return readAddress4(prefix);
		if (clazz == Inet6Address.class)
			return readAddress6(prefix);
		if (clazz == File.class)
			return readFile();

		System.err.println("Warning: I don't know what '" + clazz + "' is.");
		return null;
	}

	@Override
	public void close() {
		inputRecord.close();
	}
}
