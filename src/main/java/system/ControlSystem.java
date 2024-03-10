package system;

import component.ComponentType;
import component.ControllableComponent;
import component.PositionComponent;
import entity.GameEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ControlSystem implements GameSystem {

    private static final BufferedReader reader = new BufferedReader(
        new InputStreamReader(java.lang.System.in));

    @Override
    public void launch(List<GameEntity> entities) {
        System.out.printf("\uD83D\uDC9A Launch System - ControlSystem%n%n");

        while (true) {
            for (GameEntity entity : entities) {
                ControllableComponent controllable = (ControllableComponent) entity.getComponent(
                    ComponentType.CONTROLLABLE);
                PositionComponent position = (PositionComponent) entity.getComponent(
                    ComponentType.POSITION);

                if (controllable != null && position != null) {
                    try {
                        String command = reader.readLine();


                        switch (command) {
                            case "d" -> position.stepRight();
                            case "a" -> position.stepLeft();
                            case "w" -> position.stepUp();
                            case "s" -> position.stepDown();
                            default -> System.out.println("wasd 키로 이동해 주세요.");
                        }

                        System.out.printf("\uD83C\uDFC3 %s 이동 -> 좌표: [%d, %d]%n",
                            entity.getName(), position.getX(), position.getY());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
