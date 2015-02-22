package net.pixellife.shinynotifier;

import com.pixelmonmod.pixelmon.Pixelmon;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import net.minecraft.util.ResourceLocation;

public final class DatabaseUtil {
	public static final String playerTable = "PLAYERDATA";
	public static final String captureTable = "CAPTURE";
	private static boolean checked;
	
	static Connection con;
	static boolean madeConnection = false;
	static String pixelmonDatabasePath = null;
	static String shinyDatabasePath = null;
	
	static ResourceLocation databaseLoc = new ResourceLocation(
			"shinynotifier:database/ShinyNotifier.h2.db");
	static ResourceLocation driverLoc = new ResourceLocation(
			"pixelmon:database/h2-1.3.173.jar");

	public static Connection getConnection() {
		try {
			if ((madeConnection) && (!(con.isClosed())))
				return con;
			if (pixelmonDatabasePath == null)
				pixelmonDatabasePath = Pixelmon.modDirectory + "/database/";
			if (shinyDatabasePath == null)
				shinyDatabasePath = ShinyNotifier.modDirectory + "/database/";
			File pixelmonDatabaseDir = new File(Pixelmon.modDirectory + "/database");
			if (!(pixelmonDatabaseDir.isDirectory())) {
				System.out.println("Creating database directory");
				pixelmonDatabaseDir.mkdir();
			}
			File shinyDatabaseDir = new File(ShinyNotifier.modDirectory + "/database");
			if (!(shinyDatabaseDir.isDirectory())) {
				System.out.println("Creating database directory");
				shinyDatabaseDir.mkdir();
			}
			File databaseLockFile = new File(shinyDatabasePath + "ShinyNotifier.lock.db");
			if (databaseLockFile.exists())
				databaseLockFile.delete();
			File databaseFile = new File(shinyDatabasePath + "ShinyNotifier.h2.db");
			if (databaseFile.exists()) {
				databaseFile.delete();
			}
			File updateDatabaseDir = new File(ShinyNotifier.modDirectory
					+ "/customdatabase/");
			if ((updateDatabaseDir.exists())
					&& (new File(updateDatabaseDir, "ShinyNotifier.h2.db").exists()))
				shinyDatabasePath = ShinyNotifier.modDirectory + "/customdatabase/";
			else {
				copyDatabaseFromJar();
			}
			if (!(new File(pixelmonDatabasePath + "h2-1.3.173.jar").exists()))
				copyDriverFromJar();
			((ModClassLoader) Loader.instance().getModClassLoader())
					.addFile(new File(pixelmonDatabasePath + "h2-1.3.173.jar"));
			System.out.println("Loading Database Driver");
			Class.forName("org.h2.Driver");
			System.out.println("Establishing Connection");

			con = DriverManager.getConnection("jdbc:h2:file:" + shinyDatabasePath
					+ "ShinyNotifier;MVCC=true;ACCESS_MODE_DATA=rw","minecraft","s05h!ny");
			madeConnection = true;
			return con;
		} catch (Exception e) {
			System.out.println("Could not get a connection to database");
			e.printStackTrace();
		}
		return null;
	}
	
	private static void copyDatabaseFromJar() {
		System.out.println("Extracting database");
		InputStream iStream = null;
		try {
			iStream = DatabaseUtil.class
					.getResourceAsStream("/assets/shinynotifier/database/ShinyNotifier.h2.db");
		} catch (Exception e) {
			System.err.println("Unable to getResourceAsStream for ShinyNotifier.h2.db.");
			e.printStackTrace();
		}
		
		if ( iStream == null ) {
			System.err.println("Reading database as stream returned null.");
			throw new NullPointerException();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(shinyDatabasePath + "ShinyNotifier.h2.db");
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open ShinyNotifier.h2.db for writing.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		byte[] buf = new byte[2048];
		int r;
		try {
			r = iStream.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = iStream.read(buf);
			}
			if (fos != null)
				fos.close();
		} catch (Exception e) {
			System.err.println("Failed to extract database");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void copyDriverFromJar() {
		try {
			System.out.println("Extracting driver");
			InputStream iStream2 = DatabaseUtil.class
					.getResourceAsStream("/assets/pixelmon/database/h2-1.3.173.jar");
			FileOutputStream fos2 = null;
			fos2 = new FileOutputStream(pixelmonDatabasePath + "h2-1.3.173.jar");
			byte[] buf = new byte[2048];
			int r = iStream2.read(buf);
			while (r != -1) {
				fos2.write(buf, 0, r);
				r = iStream2.read(buf);
			}
			if (fos2 != null)
				fos2.close();
		} catch (Exception e) {
			System.out.println("Failed to extract driver");
		}
	}
}