package system;

import component.AgeComponent;
import component.ComponentType;
import entity.GameEntity;
import java.util.List;

public class AgeSystem implements GameSystem {

    @Override
    public void launch(List<GameEntity> entities) {
        System.out.printf("\uD83D\uDC9A Launch System - AgeSystem%n%n");

        while (true) {
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (GameEntity entity : entities) {
                AgeComponent age = (AgeComponent) entity.getComponent(ComponentType.AGE);
                if (age != null) {
                    age.incrementAge();

                    System.out.printf("⏳ %s 나이 먹음 -> 나이: %d%n",
                        entity.getName(),
                        age.getAge());
                }
            }

        }
    }
}
