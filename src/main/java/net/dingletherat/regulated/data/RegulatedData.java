package net.dingletherat.regulated.data;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import net.fabricmc.loader.api.FabricLoader;

public class RegulatedData {
	private static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("regulated.json");
	public static DataFile dataFile = new DataFile();

	public static class DataFile {
		public final List<String> SPECIAL_CONDITIONS = new ArrayList<>();
		public final List<String> ENABLED = new ArrayList<>();
		public final List<String> EFFECTS_OPS = new ArrayList<>();
		public final List<String> GIVABLES = new ArrayList<>();
	}

	public static void saveData() {
		try (Writer writer = Files.newBufferedWriter(DATA_PATH)) {
			new Gson().toJson(dataFile, writer);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void loadData() {
		if (!Files.exists(DATA_PATH)) return;
		try (Reader reader = Files.newBufferedReader(DATA_PATH)) {
			dataFile = new Gson().fromJson(reader, DataFile.class);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
