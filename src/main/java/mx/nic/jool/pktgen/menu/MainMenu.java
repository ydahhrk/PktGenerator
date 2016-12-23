package mx.nic.jool.pktgen.menu;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class MainMenu {

	private HashMap<String, MenuEntry> map;

	private MenuSection newHeaders;
	private MenuSection options;

	public MainMenu() {
		map = new HashMap<>();

		initNewHeaders();
		initOptions();
	}

	private void initNewHeaders() {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath());
		Reflections reflections = new Reflections(configBuilder);
		Set<Class<? extends PacketContent>> types = reflections.getSubTypesOf(PacketContent.class);

		newHeaders = new MenuSection("Available headers");

		Layer[] layers = Layer.values();
		MenuSection[] layerSections = new MenuSection[layers.length];
		for (int i = 0; i < layerSections.length; i++) {
			layerSections[i] = new MenuSection(layers[i].toString());
			newHeaders.add(layerSections[i]);
		}

		for (Class<? extends PacketContent> clazz : types) {
			if (Modifier.isAbstract(clazz.getModifiers()))
				continue;

			PacketContent packetContent;
			try {
				packetContent = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("Could not instance class " + clazz, e);
			}

			addEntry(new NewPacketContentMenuEntry(packetContent), layerSections[packetContent.getLayer().ordinal()]);
		}
	}

	private void initOptions() {
		options = new MenuSection("Other options");
		addEntry(new RecomputeCsumMenuEntry(), options);
		addEntry(new RecomputeLengthsMenuEntry(), options);
	}

	private void addEntry(MenuEntry entry, MenuSection section) {
		String key = entry.getShortName();
		MenuEntry collision = map.put(key, entry);
		if (collision != null)
			throw new IllegalArgumentException("There is more than one class whose short name is " + key + ".");
		section.add(entry);
	}

	private void print(Fragment frag) {
		newHeaders.print(0);
		System.out.println();
		frag.print();
		System.out.println();
		options.print(0);
		System.out.println();
	}

	public void handle(Parser parser, FieldScanner scanner, Fragment frag) {
		do {
			print(frag);

			String userChoice = scanner.readLine("Next", "exit");
			if ("exit".equals(userChoice))
				return;

			MenuEntry chosenEntry = getMenuEntry(userChoice, frag);
			if (chosenEntry != null)
				chosenEntry.execute(parser, scanner, frag);
			else
				System.err.println("Sorry; I don't understand you. Please request one of the options shown.");
		} while (true);
	}

	private MenuEntry getMenuEntry(String userChoice, Fragment frag) {
		MenuEntry chosenEntry = map.get(userChoice);
		if (chosenEntry != null)
			return chosenEntry;

		try {
			return new ExistingPacketContentMenuEntry(frag.get(Integer.parseInt(userChoice)));
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
}
