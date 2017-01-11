package mx.nic.jool.pktgen;

/**
 * Random global stuff that doesn't deserve its own class nor really fits
 * anywhere else.
 */
public class Util {

	/**
	 * Prints a bunch tabs in standard ouput.
	 * 
	 * @param tabs
	 *            number of tabs you want printed.
	 */
	public static void printTabs(int tabs) {
		for (int i = 0; i < tabs; i++)
			System.out.print("\t");
	}
}
