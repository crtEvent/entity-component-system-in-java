package entity;

import component.Component;
import component.ComponentType;
import java.util.ArrayList;

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
