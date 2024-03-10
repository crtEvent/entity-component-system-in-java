package system;

import entity.GameEntity;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameSystemLauncher {

    public static void launchSystems(List<GameEntity> entities) {
        List<Runnable> systems = Arrays.asList(
            () -> new AgeSystem().launch(entities),     // 나이 시스템 작동
            () -> new ControlSystem().launch(entities), // 컨트롤 시스템 작동
            () -> new AutoMoveSystem().launch(entities) // 자동 이동 시스템 작동
        );

        CompletableFuture<?>[] futures = systems.stream()
            .map(CompletableFuture::runAsync)
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }
}
