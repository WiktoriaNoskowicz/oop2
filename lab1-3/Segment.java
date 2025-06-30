
public class Segment {
    private Point start;
    private Point end;

    //    Poprzez stworzenie NOWYCH OBIEKTÓW a nie przekazywanie REFERENCJI obiekt
//    Segment jest niewrażliwy na zmiany punktów z których został stworzony
    public Segment(Point start, Point end) {
        this.start = new Point(start);
        this.end = new Point(end);
    }

    public Segment(Segment old) {
        this.start = new Point(old.getStart().getX(),old.getStart().getY());
        this.end = new Point(old.getEnd().getX(),old.getEnd().getY());
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public float len() {
            float distance = (float)Math.hypot(this.start.getX() - this.end.getX(), this.start.getY() - this.end.getY());
            return distance;
//        return (float) Math.sqrt(Math.pow(this.p2.x - this.p1.x, 2) + Math.pow(this.p2.y - this.p1.y, 2));
     }
    @Override
    public String toString() {
        return "poczatek punktu = " + this.getStart() + " koniec punktu = " + this.getEnd();
    }
    static Segment max(Segment[] arr) {
        Segment najdl = arr[0];
        for (int i =1;i<arr.length;i++) {
            if (arr[i].len() > najdl.len()){
                najdl = arr[i];
            }

        }  return najdl; //zwraca najdluzszy segment
    }
    public Segment perpendicular() {
        // Obliczamy kierunek oryginalnego segmentu
        float dx = this.end.getX() - this.start.getX();
        float dy = this.end.getY() - this.start.getY();

        // Obliczamy segment prostopadły
        float perpendicularDx = -dy;
        float perpendicularDy = dx;

        // Normalizujemy prostopoadły segment (ma długość 1)
        double perpendicularLength = Math.sqrt(perpendicularDx * perpendicularDx + perpendicularDy * perpendicularDy);
         perpendicularDx /= perpendicularLength;
        perpendicularDy /= perpendicularLength;


        // Obliczamy środkowy punkt obecnego segmentu
        float midX = (this.start.getX() + this.end.getX()) / 2;
        float midY = (this.start.getY() + this.end.getY()) / 2;

        // Tworzymy nowy segment
        Point newStart = new Point(midX + perpendicularDx, midY + perpendicularDy);
        Point newEnd = new Point(midX - perpendicularDx, midY - perpendicularDy);

        // Zwracamy prostopadły segment
        return new Segment(newStart, newEnd);
    }

    public Segment perpendicular(float length) {
        // Obliczamy kierunek oryginalnego segmentu
        float dx = this.end.getX() - this.start.getX();
        float dy = this.end.getY() - this.start.getY();

        // Obliczamy segment prostopadły
        float perpendicularDx = -dy;
        float perpendicularDy = dx;

        // Normalizujemy prostopoadły segment (ma długość 1)
        double perpendicularLength = Math.sqrt(perpendicularDx * perpendicularDx + perpendicularDy * perpendicularDy);
        perpendicularDx /= perpendicularLength;
        perpendicularDy /= perpendicularLength;

        perpendicularDx *= length;
        perpendicularDy *= length;

        // Obliczamy środkowy punkt obecnego segmentu
        float midX = (this.start.getX() + this.end.getX()) / 2;
        float midY = (this.start.getY() + this.end.getY()) / 2;

        // Tworzymy nowy segment
        Point newStart = new Point(midX + perpendicularDx, midY + perpendicularDy);
        Point newEnd = new Point(midX - perpendicularDx, midY - perpendicularDy);

        // Zwracamy prostopadły segment
        return new Segment(newStart, newEnd);}


}
