package mx.nic.jool.pktgen.proto;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;
import mx.nic.jool.pktgen.proto.l4.Layer4Header;

/**
 * Keeps tracks of all the implementors of {@link Header} so others can instance
 * them without knowing what they are.
 */
public class HeaderFactory {

	/** One instance per {@link Header} class in the classpath. */
	private static List<Header> headers;

	/**
	 * Initializes {@link #headers}.
	 */
	private static void initHeaders() {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath());
		Reflections reflections = new Reflections(configBuilder);
		Set<Class<? extends Header>> types = reflections.getSubTypesOf(Header.class);

		headers = new ArrayList<>();
		for (Class<? extends Header> clazz : types) {
			if (Modifier.isAbstract(clazz.getModifiers()))
				continue;

			Header header;
			try {
				header = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("Could not instance class " + clazz, e);
			}

			headers.add(header);
		}
	}

	/**
	 * Returns all the {@link Header}s in the classpath.
	 * <p>
	 * Do not modify them. (Yes, I could return a copy, but this is not a
	 * library API and the cost-benefit ratio doesn't make it worth it.)
	 */
	public static List<Header> getHeaders() {
		if (headers == null)
			initHeaders();
		return headers;
	}

	/**
	 * Returns the {@link Header} in the classpath whose
	 * {@link Header#getShortName()} is <code>proto</code>.
	 */
	public static Header forName(String proto) {
		for (Header header : getHeaders()) {
			if (header.getShortName().equals(proto))
				return header.createClone();
		}
		return null;
	}

	/**
	 * Standard outputs every {@link Header} in the classpath, sorted by layer.
	 */
	public static void printStringProtocols() {
		System.out.println("Available headers:");

		System.out.println("\tLayer 3 headers:");
		for (Header header : getHeaders())
			if (header instanceof Layer3Header)
				printStringProtocol(header);

		System.out.println("\tLayer 4 headers:");
		for (Header header : getHeaders())
			if (header instanceof Layer4Header)
				printStringProtocol(header);

		System.out.println("\tPayload:");
		for (Header header : getHeaders())
			if (header instanceof Payload)
				printStringProtocol(header);
		System.out.println();
	}

	/**
	 * Standard outputs {@link Header}'s short name and its class name.
	 */
	private static void printStringProtocol(Header header) {
		System.out.print("\t\t(");
		System.out.printf("%7s", header.getShortName());
		System.out.print(") ");
		System.out.println(header.getClass().getSimpleName());
	}

	/**
	 * Standard the classpath's {@link Header}s' class names and their
	 * IANA-assigned identifiers.
	 */
	public static void printIntProtocols() {
		for (Header header : getHeaders()) {
			if (header.getHdrIndex() >= 0) {
				System.out.print(header.getClass().getSimpleName());
				System.out.print(": ");
				System.out.println(header.getHdrIndex());
			}
		}
	}

	/**
	 * Returns the {@link Header} whose IANA-assigned identifier is
	 * <code>nextHdr</code>.
	 */
	public static Header forNexthdr(Integer nexthdr) {
		for (Header header : getHeaders())
			if (header.getHdrIndex() == nexthdr)
				return header.createClone();
		return null;
	}

}
