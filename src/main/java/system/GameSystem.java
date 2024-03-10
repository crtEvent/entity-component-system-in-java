package system;

import entity.GameEntity;
import java.util.List;

public interface GameSystem {
    void launch(List<GameEntity> entities);
}
