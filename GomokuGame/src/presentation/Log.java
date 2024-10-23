package presentation;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
/**
 * 
 */
public class Log {
	public static String nombre = "Programador";

	public static void record(Exception e) {
		try {
			Logger logger = Logger.getLogger(nombre);
			logger.setUseParentHandlers(false);
			FileHandler file = new FileHandler(nombre + ".log", true);
			file.setFormatter(new SimpleFormatter());
			logger.addHandler(file);

			logger.log(Level.WARNING, "Advertencia: " + e.toString(), e);

			System.err.println("Se ha producido la excepcion: " + e.getMessage());

			file.close();
		} catch (Exception oe) {
			oe.printStackTrace();
		}
	}
}
