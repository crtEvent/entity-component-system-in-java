package entity;

import component.AgeComponent;
import component.AutoMovableComponent;
import component.Component;
import component.ComponentType;
import component.ControllableComponent;
import component.PositionComponent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameEntityGenerator {

    private static final String DIRECTORY = "src/entity-metadata";

    private GameEntityGenerator() { }

    public static List<GameEntity> generateEntities() {
        File folder = new File(DIRECTORY);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;

        List<GameEntity> entities = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String name = file.getName().substring(0, file.getName().length() - 4);
                if (name.isEmpty()) continue;

                GameEntity entity = loadEntityOrNull(name);
                assert entity != null;
                entity.updateLog();

                entities.add(entity);
            }
        }

        return entities;
    }

    private static GameEntity loadEntityOrNull(String name) {
        String fileName = String.format("%s.txt", name);
        Path filePath = Paths.get(DIRECTORY, fileName);

        File entityFile = new File(filePath.toString());

        if (!entityFile.isFile()) {
            return null; // 파일이 없는 경우
        }

        List<String> lines;

        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        assert (lines.size() == 1) : "해당 파일은 올바른 포맷이 아닙니다.";

        String[] components = lines.get(0).split(",", -1);

        GameEntity entity = new GameEntity(name);

        for (String c : components) {
            ComponentType type;

            try {
                type = ComponentType.valueOf(c.toUpperCase());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }

            entity.addComponent(generateComponentBy(type));
        }

        return entity;
    }

    private static Component generateComponentBy(ComponentType type) {
        return switch (type) {
            case AGE -> new AgeComponent();
            case POSITION -> new PositionComponent();
            case CONTROLLABLE -> new ControllableComponent();
            case AUTO_MOVABLE -> new AutoMovableComponent();
        };
    }
}
