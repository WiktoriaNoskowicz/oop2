public class SolidFillShapeDecorator extends  ShapeDecorator{
    private String color;
    public SolidFillShapeDecorator(Shape shape, String color) {
        super(shape);
        this.color = color;
    }
    @Override
    public String toSvg(String param) {
        String formattedString = String.format("fill=\"%s\" %s ",color,param );
        return this.decoratedShape.toSvg(formattedString); //wywolanie tej samej metody na rzecz obiektu decoratedShape
    }

}
