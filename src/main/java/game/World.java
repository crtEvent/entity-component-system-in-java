package game;

import entity.GameEntityGenerator;
import entity.GameEntity;
import java.util.List;
import system.GameSystemLauncher;

public class World {

    public void initGame() {
        List<GameEntity> entities = GameEntityGenerator.generateEntities();
        GameSystemLauncher.launchSystems(entities);
    }
}
