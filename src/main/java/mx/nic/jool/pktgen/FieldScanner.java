package mx.nic.jool.pktgen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.proto.PacketContentFactory;

public class FieldScanner {

	private Scanner scanner;

	public FieldScanner(Scanner scanner) throws FileNotFoundException {
		this.scanner = scanner;
	}

	private String readLine() {
		int indexOfTag;
		String input = scanner.nextLine().trim();
		System.out.println();

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
		return readInteger(prefix, "auto");
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

	public byte[] readByteArray(String prefix) {
		System.out.println(prefix + ":");

		byte[] bytes = readBytesFromFile();
		if (bytes != null)
			return bytes;

		return readBytesFromStdin();
	}

	private byte[] readBytesFromFile() {
		boolean readFromFile = readBoolean("Read from file?", false);
		if (!readFromFile)
			return null;

		byte[] result = readFile();
		if (result == null)
			return null;

		System.out.println("Length: " + result.length);
		boolean customLength = readBoolean("Truncate?", false);
		if (customLength) {
			int length = readInt("New lengh", 4);
			result = Arrays.copyOf(result, length);
		}

		return result;
	}

	private byte[] readFile() {
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

	private byte[] readBytesFromStdin() {
		int length = readInt("Length", 4);
		byte[] result = new byte[length];
		boolean auto = readBoolean("Automatic insert (0,1,2,3..255,0,1,2,...)", true);
		for (int i = 0; i < length; i++)
			result[i] = (byte) (auto ? (i & 0xFF) : readInt("byte " + i, i % 0xFF));

		return result;
	}

	/**
	 * Prints <code>object</code> in standard output and requests modifications
	 * to the user.
	 * 
	 * This is the core of "auto" mode.
	 */
	public void scan(Object object) {
		Field[] fields = collectHeaderFields(object);
		if (fields.length == 0) {
			System.err.println("Nothing to change.");
			return;
		}

		do {
			showFields(fields, object);

			String fieldToModify = readLine("Field", "exit");
			if (fieldToModify.equalsIgnoreCase("exit"))
				break;

			if (fieldToModify == null || fieldToModify.isEmpty())
				continue;

			for (Field field : fields) {
				if (!field.getName().equalsIgnoreCase(fieldToModify))
					continue;

				try {
					field.set(object, read(object, field));
				} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
					throw new IllegalArgumentException("Strange & unlikely error condition; see below.", e);
				}
			}
		} while (true);
	}

	private Field[] collectHeaderFields(Object object) {
		ArrayList<Field> resultList = new ArrayList<>();

		Class<?> clazz = object.getClass();
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(HeaderField.class) != null) {
					field.setAccessible(true);
					resultList.add(field);
				}
			}

			clazz = clazz.getSuperclass();
		} while (clazz != null);

		Field[] resultArray = new Field[resultList.size()];
		return resultList.toArray(resultArray);
	}

	private void showFields(Field[] fields, Object obj) {
		System.out.printf("%s Fields:\n", obj.getClass().getSimpleName());
		for (Field field : fields) {
			System.out.printf("\t(%15s) ", field.getName());

			Object fieldValue;
			try {
				fieldValue = field.get(obj);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Strange & unlikely error condition; see below.", e);
			}

			System.out.print(": ");
			if (fieldValue == null)
				System.out.println("(auto)");
			else if (fieldValue instanceof ScannableHeaderField)
				((ScannableHeaderField) fieldValue).print();
			else if (fieldValue instanceof byte[])
				System.out.println(Arrays.toString((byte[]) fieldValue));
			else
				System.out.println(fieldValue);
		}
	}

	private Object read(Object object, Field field)
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

		if (clazz == byte[].class)
			return readByteArray(prefix);
		if (clazz == String.class)
			return readLine(prefix, (String) field.get(object.getClass().newInstance()));
		if (clazz == Inet4Address.class)
			return readAddress4(prefix);
		if (clazz == Inet6Address.class)
			return readAddress6(prefix);

		if (ScannableHeaderField.class.isAssignableFrom(clazz)) {
			ScannableHeaderField scannableField = (ScannableHeaderField) field.get(object);
			/* Must not be null! */
			scannableField.readFromStdIn(this);
			return scannableField;
		}

		System.err.println("Warning: I don't know what '" + clazz + "' is.");
		return null;
	}
}
