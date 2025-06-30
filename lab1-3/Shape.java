public abstract class Shape {
    protected Style style; //widza go tylko klasy potomne

    public Shape(Style style) {
        this.style = style;
    }

    public abstract String toSvg(float offsetX, float offsetY);
}
