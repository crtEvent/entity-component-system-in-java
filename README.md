- 스터디에서 상속과 합성에 대해 공부하던 중 발견한 Entity Component System. 이해가 안되서 구현 방법을 찾아보고 구현해보기로 함


# Entity Component System (ECS)

- 객체와 객체의 상태와 행동을 Entity, Component, System 등의 구성 요소로 분리하여 관리함으로써, 유연성과 성능을 개선하는 아키텍처 패턴.
- 합성을 사용해 코드 변경 없이 오브젝트를 자유롭게 만들 수 있게 해준다.
- 주로 게임 개발에서 게임 월드의 오브젝트들을 표현하기 위해 사용된다.

# 전체 구조

![](https://velog.velcdn.com/images/ape/post/6121a562-756c-4c4f-a581-da7fb7457a64/image.png)


- **Component** : 데이터, 게임의 상태만 가진다. 케릭터의 위치, 체력, 레벨, 직업, 아이템 분류 등을 Component로 표현할 수 있다.
- **System**: 기능 구현, 데이터를 변경하는 행동만 가진다. 게임 로직과 규칙을 System에 구현한다. 전투 시스템, 중력 시스템, 밤낮이 바뀌는 시스템, 이동 시스템 등
- **Entity** : Component들을 담는 그릇. id로 구분한다. 게임의 모든 오브젝트를 대표하는 식별자 역할. 엔티티는 데이터나 행동을 가지지 않고 오직 Component의 집합만 가진다.

# Entity와 Component 구조

- NPC를 Entity로 만든다고 가정해 보자. 현재 기획상 NPC는 나이 정보를 가지고 있으며 특정 위치에 고정되어 있게 구현하고자 한다. 구조는 다음과 같이 설계될 것이다.

![](https://velog.velcdn.com/images/ape/post/9a627d16-d739-4399-b225-eb2c68cf726a/image.png)

- NPC는 현재 나이 정보를 가지고 있는 `AgeComponent`와 위치 정보를 가지고 있는 `PositionComponent`를 가지고 있다.
- 만약 게임의 기획이 바뀌어 NPC를 공격할 수 있게 구현해야 한다면?

![](https://velog.velcdn.com/images/ape/post/3704b309-de1b-4b3e-ad7f-6830695178cd/image.png)

- Entity에 공격할 수 있는 상태임을 뜻하는 `AttackableComponent`를 추가해 주기만 하면 된다.

# 만약 상속으로 Entity를 구현했다면?

- 기존에 상속 구조가 아래와 같이 구현되어 있다고 가정하자.

![](https://velog.velcdn.com/images/ape/post/ca2c9a1f-cb5a-4a17-b25b-9f0aab46162f/image.png)

- 공격 가능함에 관련된 상태와 행동이 `AttackableCharacter`에 구현되어 있고, `Monster`와 `Animal`이 `AttackableCharacter`를 상속 받아 공격과 관련된 코드를 재사용 하고 있다.
- NPC도 공격 가능하게 하려면 어떻게 해야 할까?
- 아마 `NPC`가 `AttackableCharacter`를 상속도록 상속 구조를 변경하게 될 것이다. 이렇게 되면 변경이 있을 때 마다 상속 구조를 수정하고, 구현을 다시 해야 하는 경우도 있을 것 같다.
- Entity Component System를 사용하면 기존에 있던 Component를 추가해 주는 것 만으로도 Entity를 쉽게 변경할 수 있다.

# 구현해 보기

- Component로 Entity를 조립하는 방법을 구현하고자 한다. 예시로 만들어 볼 Entity는 `Player`, `Tree`, `Monster` 세 가지이다.
    - `Player`는 나이와 위치 정보를 가지며 방향키를 사용해 위치를 조작할 수 있다.
    - `Tree`는 나이와 위치 정보를 가지며 움직일 수 없다.
    - `Monster`는 나이와 위치 정보를 가지며 스스로로 움직인다.

## Component 구현
[GitHub 주소](https://github.com/crtEvent/entity-component-system-in-java)

### ComponentType

- 먼저 Component의 타입을 만든다.
- ECS는 기존에 구현되어 있던 Component들을 자유롭게 조합할 수 있게 하는 패턴이다. 기존에 없던 새로운 Component가 추가된다면 `ComponentType`에 코드가 추가 되어야 한다.
```java
public enum ComponentType { 
    AGE, POSITION, CONTROLLABLE, AUTO_MOVABLE 
}
```

### Component

- Component는 타입을 가지며, Component를 상속 받는 객체들은 각자의 상태를 가진다. 추후에 구현할 System들이 Component들의 상태를 변경하게 된다.
```java
public abstract class Component {

    private final ComponentType type;

    public Component(ComponentType type) {
        this.type = type;
    }

    public boolean isMatchType(ComponentType type) {
        return this.type == type;
    }

}
```


### AgeComponent
- 나이를 표현하는 age 필드를 가지고 있다.
```java
public class AgeComponent extends Component {

    private int age;

    public AgeComponent() {
        super(ComponentType.AGE);
    }

    public int getAge() { return age; }

    public void incrementAge() { this.age++; }
}
````

### PositionComponent

- x, y 좌표 필드를 가지고 있다.
```java
public class PositionComponent extends Component {

    private int x;
    private int y;

    public PositionComponent() {
        super(ComponentType.POSITION);
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public void stepRight() { this.x++; }

    public void stepLeft() { this.x--; }

    public void stepUp() { this.y++; }

    public void stepDown() { this.y--; }
}
```

### ControllableComponent

- 이 Component를 가지고 있으면 직접 조작할 수 있는 Entity로 간주한다.
```java
public class ControllableComponent extends Component {

    public ControllableComponent() {
        super(ComponentType.CONTROLLABLE);
    }
}
```

### AutoMovableComponent

- 이 Component를 가지고 있으면 스스로 움직이는 Entity로 간주한다.
```java
public class AutoMovableComponent extends Component {

    public AutoMovableComponent() {
        super(ComponentType.AUTO_MOVABLE);
    }
}
```

## Entity 구현

### GameEntity

```java
public class GameEntity {

    private String name;
    private final ArrayList<Component> components = new ArrayList<>();

    public GameEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public Component getComponent(ComponentType type) {
        for(Component c : components) {
            if(c.isMatchType(type)) {
                return c;
            }
        }
        return null;
    }

    public void updateLog() {
        System.out.printf("⭐️ Update GameObject - %s%n", this.name);
        for(Component component : this.components) {
            System.out.printf("\t\uD83D\uDD38 Update Component - %s%n", component.getClass().getSimpleName());
        }
        System.out.println();
    }

}
```

- Entity는 이름과 Component들을 가지고 있다.
    - (Entity는 유일한 식별자를 가져야 하지만 유일한 식별자를 할당하는 로직은 여기선 생략한다.)
-  `addComponent()`: Component를 추가할 수 있는 메서드
- `getComponent(ComponentType type)`: Entity가 가지고 있는 Component 하나를 반환하는 메서드
- `updateLog()`: 생성된 Entity의 상태를 출력해 주는 메서드. 로깅용으로 추가했다

### GameEntityGenerator

- Entity text 파일에 정의된 정보를 읽어 Entity를 생성해 주는 객체.
- Entity text 파일에는 Entity가 가지고 있는 Component Type의 이름을 쉼표로 구분해서 정의되어 있다.

```text
-- Player.txt
age,position,controllable

-- Tree.txt
age,position

-- Monster.txt
age,position,auto_movable
```

- Entity 정보를 text 파일로 빼놓은 이유는 개발자가 아닌 사람들이 Entity를 쉽게 추가하고, 수정할 수 있게 하기 위함이다.
- 게임에 필요한 Entity들을 text 파일에 정의해 두고 게임이 실행되면 코드 단에서 Entity text 파일들을 읽어 Entity를 생성하게 하여 코드를 수정하지 않고도 Entity를 추가/수정할 수 있다.

![](https://blog.kakaocdn.net/dn/bkWQ3z/btsFIhfzFHC/Zk72T8hQcsDWkaO32J281k/img.png)

-   위와 같이 게임 에디터 툴에서 아이템, 케릭터 스탯 등을 수정하면 Entity text 파일이 수정되고, 게임이 실행될 때 Entity text 파일에 저장된 정보대로 Entity가 생성되는 흐름이라고 생각하면 된다.
- 이제 에디터 툴에서 기획자들이 직접 Entity를 추가하거나 변경해서 쉽게 여러 상황에서의 게임 테스트를 할 수 있게 된다.

```java
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
```

- `loadObjectOrNull(String name)`: 전달 받은 name에 해당하는 Entity text 파일을 읽어 text 파일에 있는 정보대로 Entity를 생성해 반환한다.
- `generateComponentBy(ComponentType type)`: ComponentType에 맞는 Component를 생성해 준다.

## System 구현

- Entity가 가지고 있는 Component들의 상태를 변경해 줄 System들

### GameSystem

- `launch()`: System을 실행시켜 주는 메서드
```java
public interface GameSystem {  
    void launch(List<GameEntity> entities);  
}
```

### AgeSystem

- 시간이 흐름에 따라 나이를 증가시켜 주는 시스템
- 5초마다 AgeComponent의 age 필드를 1씩 증가시켜 준다
```java
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
```

### ControlSystem

- 캐릭터를 조작하는 시스템
- wasd키를 입력받아 입력받은 방향에 따라 PositionComponent의 좌표를 변경시켜 준다.
```java
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
```

### AutoMoveSystem
- 캐릭터를 스스로 이동하게 하는 시스템
- 2초마다 랜덤한 방향으로 PositionComponent의 좌표를 1씩 증감해 준다.
```java
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
````

### GameSystemLauncher

- 각각의 System 마다 스레드를 할당하여 모든 게임 시스템을 동시에 비동기적으로 실행한다.
```java
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
```

## World

- Entity들을 초기화 하고 System들을 실행시켜 준다.
```java
public class World {  
  
    public void initGame() {  
        List<GameEntity> entities = GameEntityGenerator.generateEntities();  
        GameSystemLauncher.launchSystems(entities);  
    }  
}
```

## 실행 결과

```
⭐️ Update GameObject - Player
    🔸 Update Component - AgeComponent
    🔸 Update Component - PositionComponent
    🔸 Update Component - ControllableComponent

⭐️ Update GameObject - Monster
    🔸 Update Component - AgeComponent
    🔸 Update Component - PositionComponent
    🔸 Update Component - AutoMovableComponent

⭐️ Update GameObject - Tree
    🔸 Update Component - AgeComponent
    🔸 Update Component - PositionComponent

💚 Launch System - AgeSystem

💚 Launch System - AutoMoveSystem

💚 Launch System - ControlSystem

👻 Monster 이동 -> 좌표: [1, 0]
👻 Monster 이동 -> 좌표: [2, 0]
⏳ Player 나이 먹음 -> 나이: 1
⏳ Monster 나이 먹음 -> 나이: 1
⏳ Tree 나이 먹음 -> 나이: 1
👻 Monster 이동 -> 좌표: [2, 1]
👻 Monster 이동 -> 좌표: [1, 1]
a
🏃 Player 이동 -> 좌표: [-1, 0]
⏳ Player 나이 먹음 -> 나이: 2
⏳ Monster 나이 먹음 -> 나이: 2
⏳ Tree 나이 먹음 -> 나이: 2
👻 Monster 이동 -> 좌표: [1, 0]
a
🏃 Player 이동 -> 좌표: [-2, 0]
👻 Monster 이동 -> 좌표: [1, -1]
s
🏃 Player 이동 -> 좌표: [-2, -1]
👻 Monster 이동 -> 좌표: [1, 0]
⏳ Player 나이 먹음 -> 나이: 3
⏳ Monster 나이 먹음 -> 나이: 3
⏳ Tree 나이 먹음 -> 나이: 3
👻 Monster 이동 -> 좌표: [1, -1]
👻 Monster 이동 -> 좌표: [2, -1]
s
🏃 Player 이동 -> 좌표: [-2, -2]
⏳ Player 나이 먹음 -> 나이: 4
⏳ Monster 나이 먹음 -> 나이: 4
⏳ Tree 나이 먹음 -> 나이: 4
👻 Monster 이동 -> 좌표: [2, -2]
```
- 각 시스템들이 동시에 실행되고 있는것을 확인할 수 있다.

---
# 참고 및 출처
- [ECS (Entity Component System)](https://tsyang.tistory.com/69)
- [[공지] 번외) Entity Component System](https://m.blog.naver.com/kiseop91/221855589754)
- [개체지향 프로그래밍 및 설계 (Java)](https://www.udemy.com/course/object-oriented-programming-and-design-by-pocu/)

---

# 기타
## 아키텍처 패턴? 디자인 패턴과 다른 건가?
- 아키텍처 패턴은 소프트웨어 전체 시스템의 기본적인 구조를 정의하며, 시스템의 구성요소, 그들 사이의 관계, 그리고 그들의 상호작용 방법을 포함함
- 디자인 패턴은 소프트웨어의 설계 문제를 해결하기 위해 사용되는 반복 사용 가능한 솔루션