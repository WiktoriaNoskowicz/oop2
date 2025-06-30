public class Point {
    private float x;
    private float y;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Point(){
        this.x = 0;
        this.y = 0;
    }
    //konstruktor kopiujący tworzacy GŁĘBOKĄ KOPIĘ OBIEKTU -> czyli kopiujemy cały obiekt niezaleznie i nie jest on powiazany
    // z poczatkowym obiektem, w płytkiej mamy ciagle zależność i zmiana jednego wpływa na zmiane drugiego
    public Point(Point old){
        this.x = old.getX();
        this.y = old.getY();
    }

    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }
    public Point translated(float dx, float dy) {
        Point res = new Point(this.x, this.y);
        res.translate(dx, dy);
        return res;

    }

    @Override
    public String toString() {
        return "wspolrzedna punktu x = " + this.x + " wsporzedna punktu y = " + this.y;
    }
    public String toSvg(){
        return "<circle cx=\"" + this.x + "\" cy=\"" + this.y + "\" r=\"5\" fill=\"black\" />";

    }

}
