package mx.nic.jool.pktgen.menu;

import java.util.LinkedList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.Util;
import mx.nic.jool.pktgen.pojo.Fragment;

/**
 * A logical subgroup of entries in a {@link MainMenu}.
 * 
 * Technically a {@link MainMenuEntry}, except it's not executable.
 */
public class MenuSection extends MainMenuEntry {

	private List<MainMenuEntry> entries = new LinkedList<>();

	public MenuSection(String name) {
		super(name, null);
	}

	public void add(MainMenuEntry entry) {
		entries.add(entry);
	}

	@Override
	public void print(int tabs) {
		Util.printTabs(tabs);
		System.out.println(getShortName() + ":");
		for (MainMenuEntry entry : entries)
			entry.print(tabs + 1);
	}

	@Override
	public void execute(FieldScanner scanner, Fragment frag) {
		throw new IllegalArgumentException("Menu sections cannot be executed.");
	}

}
