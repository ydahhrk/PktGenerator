package mx.nic.jool.pktgen.menu;

import java.util.HashMap;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.HeaderFactory;

/**
 * The global menu. The one that offers to add and modify headers and has a few
 * other utility options.
 */
public class MainMenu {

	/**
	 * Quick accesor to each menu entry.
	 * 
	 * Existing menu entries (numericals) are computed dynamically and as such
	 * are not indexed here.
	 */
	private HashMap<String, MainMenuEntry> map;

	/** Section of the menu that offers adding headers to the new packet. */
	private MenuSection newHeaders;
	// The section of the menu that offers modifying headers is generated
	// dynamically and is therefore implicit.
	/** Section of the menu that offers several predefined packet actions. */
	private MenuSection miscOptions;

	public MainMenu() {
		map = new HashMap<>();
		initNewHeaders();
		initMiscOptions();
	}

	/**
	 * Initializes {@link #newHeaders}.
	 */
	private void initNewHeaders() {
		newHeaders = new MenuSection("Available headers");

		Layer[] layers = Layer.values();
		MenuSection[] layerSections = new MenuSection[layers.length];
		for (int i = 0; i < layerSections.length; i++) {
			layerSections[i] = new MenuSection(layers[i].toString());
			newHeaders.add(layerSections[i]);
		}

		for (Header headers : HeaderFactory.getHeaders())
			addEntry(new NewHeaderMenuEntry(headers), layerSections[headers.getLayer().ordinal()]);
	}

	/**
	 * Initializes {@link #miscOptions}.
	 */
	private void initMiscOptions() {
		miscOptions = new MenuSection("Other options");
		addEntry(new DeleteHeaderMenuEntry(), miscOptions);
		addEntry(new RecomputeCsumMenuEntry(), miscOptions);
		addEntry(new RecomputeLengthsMenuEntry(), miscOptions);
	}

	/**
	 * Quick one-liner for adding a menu entry to both the map and the
	 * corresponding section list.
	 */
	private void addEntry(MainMenuEntry entry, MenuSection section) {
		String key = entry.getShortName();
		MainMenuEntry collision = map.put(key, entry);
		if (collision != null)
			throw new IllegalArgumentException("There is more than one Header whose short name is " + key + ".");
		section.add(entry);
	}

	/**
	 * Standard output print.
	 */
	private void print(Fragment frag) {
		newHeaders.print(0);
		System.out.println();
		frag.print(0);
		System.out.println();
		miscOptions.print(0);
		System.out.println();
	}

	/**
	 * Edits <code>frag</code> according to user input. Returns when the user is
	 * done editing.
	 * 
	 * @param scanner
	 *            Standard input reader.
	 * @param frag
	 *            the packet the user wants to edit.
	 */
	public void handle(FieldScanner scanner, Fragment frag) {
		do {
			print(frag);

			String userChoice = scanner.readLine("Next", "exit");
			if ("exit".equals(userChoice))
				return;

			MainMenuEntry chosenEntry = getMenuEntry(userChoice, frag);
			if (chosenEntry != null)
				chosenEntry.execute(scanner, frag);
			else
				System.err.println("Sorry; I don't understand you. Please request one of the options shown.");
		} while (true);
	}

	/**
	 * Returns the menu entry whose identifier is <code>userChoice</code>.
	 */
	private MainMenuEntry getMenuEntry(String userChoice, Fragment frag) {
		MainMenuEntry chosenEntry = map.get(userChoice);
		if (chosenEntry != null)
			return chosenEntry;

		try {
			return new ExistingHeaderMenuEntry(frag.get(Integer.parseInt(userChoice)));
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			return null;
		}
	}
}
