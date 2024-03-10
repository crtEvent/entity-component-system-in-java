package component;

public class AgeComponent extends Component {

    private int age;

    public AgeComponent() {
        super(ComponentType.AGE);
    }

    public int getAge() { return age; }

    public void incrementAge() { this.age++; }
}
