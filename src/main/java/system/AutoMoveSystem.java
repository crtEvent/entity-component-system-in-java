package system;

import component.AutoMovableComponent;
import component.ComponentType;
import component.PositionComponent;
import entity.GameEntity;
import java.util.List;
import java.util.Random;

public class AutoMoveSystem implements GameSystem {

    private static final Random random = new Random();

    @Override
    public void launch(List<GameEntity> entities) {
        System.out.printf("\uD83D\uDC9A Launch System - AutoMoveSystem%n%n");

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (GameEntity entity : entities) {
                AutoMovableComponent autoMovalble = (AutoMovableComponent) entity.getComponent(
                    ComponentType.AUTO_MOVABLE);
                PositionComponent position = (PositionComponent) entity.getComponent(ComponentType.POSITION);

                if (autoMovalble != null && position != null) {
                    int randomDirection = random.nextInt(4);

                    switch (randomDirection) {
                        case 0 -> position.stepRight();
                        case 1 -> position.stepLeft();
                        case 2 -> position.stepUp();
                        case 3 -> position.stepDown();
                    }

                    System.out.printf("\uD83D\uDC7B %s 이동 -> 좌표: [%d, %d]%n",
                        entity.getName(), position.getX(), position.getY());
                }
            }

        }
    }
}
