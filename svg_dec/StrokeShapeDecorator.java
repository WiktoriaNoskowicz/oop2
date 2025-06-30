public class StrokeShapeDecorator extends ShapeDecorator {
    private String color;
    private Double width;

    public StrokeShapeDecorator(Shape shape, String color, Double width) {
        super(shape);
        this.color = color;
        this.width = width;
    }
    public String toSvg(String param) {
        String formattedParams = String.format("stroke=\"%s\" stroke-width=\"%f\" %s",this.color,this.width,param);
        return this.decoratedShape.toSvg(formattedParams); //dawac this
    }

}
