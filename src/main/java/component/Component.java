package component;

public abstract class Component {

    private final ComponentType type;

    public Component(ComponentType type) {
        this.type = type;
    }

    public boolean isMatchType(ComponentType type) {
        return this.type == type;
    }

}
