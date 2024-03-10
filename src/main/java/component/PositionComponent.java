package component;

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
