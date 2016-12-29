package mx.nic.jool.pktgen.proto;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;
import mx.nic.jool.pktgen.proto.l4.Layer4Header;

public class PacketContentFactory {

	private static List<PacketContent> contents;

	private static void initClasses() {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath());
		Reflections reflections = new Reflections(configBuilder);
		Set<Class<? extends PacketContent>> types = reflections.getSubTypesOf(PacketContent.class);

		contents = new ArrayList<>();
		for (Class<? extends PacketContent> clazz : types) {
			if (Modifier.isAbstract(clazz.getModifiers()))
				continue;

			PacketContent content;
			try {
				content = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("Could not instance class " + clazz, e);
			}

			contents.add(content);
		}
	}

	public static List<PacketContent> getContents() {
		if (contents == null)
			initClasses();
		return contents;
	}

	public static PacketContent forName(String proto) {
		for (PacketContent content : contents) {
			if (content.getShortName().equals(proto))
				return content.createClone();
		}
		return null;
	}

	public static void printStringProtocols() {
		System.out.println("Available headers:");

		System.out.println("\tLayer 3 headers:");
		for (PacketContent content : getContents())
			if (content instanceof Layer3Header)
				printStringProtocol(content);

		System.out.println("\tLayer 4 headers:");
		for (PacketContent content : getContents())
			if (content instanceof Layer4Header)
				printStringProtocol(content);

		System.out.println("\tPayload:");
		for (PacketContent content : getContents())
			if (content instanceof Payload)
				printStringProtocol(content);
		System.out.println();
	}

	private static void printStringProtocol(PacketContent content) {
		System.out.print("\t\t(");
		System.out.printf("%7s", content.getShortName());
		System.out.print(") ");
		System.out.println(content.getClass().getSimpleName());
	}

	public static void printIntProtocols() {
		for (PacketContent content : getContents()) {
			if (content.getHdrIndex() >= 0) {
				System.out.print(content.getClass());
				System.out.print(": ");
				System.out.println(content.getHdrIndex());
			}
		}
	}

	public static PacketContent forNexthdr(Integer nexthdr) {
		for (PacketContent content : getContents())
			if (content.getHdrIndex() == nexthdr)
				return content.createClone();
		return null;
	}

}
