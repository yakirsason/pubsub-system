package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {

    private String confFilePath;
    private final List<ParallelAgent> agents = new ArrayList<>();

    public void setConfFile(String path) {
        this.confFilePath = path;
    }

    @Override
    public void create() {
        if (confFilePath == null || confFilePath.isEmpty()) {
            throw new IllegalStateException("Configuration file path was not set");
        }

        List<String> lines = readAllLines(confFilePath);

        // every agent is described by 3 lines: className, subs, pubs
        if (lines.size() % 3 != 0) {
            throw new IllegalArgumentException("Bad config file format: number of lines must be divisible by 3");
        }

        for (int i = 0; i < lines.size(); i += 3) {
            String className = lines.get(i).trim();
            String subsLine = lines.get(i + 1).trim();
            String pubsLine = lines.get(i + 2).trim();

            String[] subs = splitCsv(subsLine);
            String[] pubs = splitCsv(pubsLine);

            Agent created = createAgentByReflection(className, subs, pubs);

            // wrap every agent with ParallelAgent (threads requirement)
            ParallelAgent pa = new ParallelAgent(created);
            agents.add(pa);
        }
    }

    @Override
    public void close() {
        for (ParallelAgent pa : agents) {
            try {
                pa.close();
            } catch (Exception ignored) {}
        }
        agents.clear();
    }

    @Override
    public String getName() {
        return "Generic Config";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    // ---------- helpers ----------

    private static List<String> readAllLines(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // ignore empty lines
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file: " + path, e);
        }
        return lines;
    }

    private static String[] splitCsv(String line) {
        if (line.isEmpty()) return new String[0];
        String[] parts = line.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    private static Agent createAgentByReflection(String className, String[] subs, String[] pubs) {
        try {
            Class<?> cls = Class.forName(className);
            Constructor<?> ctor = cls.getConstructor(String[].class, String[].class);
            Object instance = ctor.newInstance(subs, pubs);

            if (!(instance instanceof Agent)) {
                throw new IllegalArgumentException(className + " does not implement Agent");
            }

            return (Agent) instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create agent: " + className, e);
        }
    }
}
