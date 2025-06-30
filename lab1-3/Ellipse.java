public class Ellipse extends Shape{
    private Point center;
    private float radiusX;
    private float radiusY;
    //private Style style; jest to w shape wiec tego nie piszemy

    public Ellipse(Point center,float radiusX, float radiusY, Style style) {
        super(style);
        this.center = center;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    public Point getCenter() {
        return center;
    }


    public float getRadiusX() {
        return radiusX;
    }

    public float getRadiusY() {
        return radiusY;
    }

    @Override
    public String toSvg(float offsetX, float offsetY) {

        return "<epllipse  center x =\"" + this.center.getX() + "center y =\"" + this.center.getY() + "\" radius x =\"" + this.getRadiusX() + "\" radius y =\"" + this.getRadiusY()+ "strokeWidth =\"" + this.style.toSvg() + "/>";
    }
}
