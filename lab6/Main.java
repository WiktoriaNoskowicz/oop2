import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws NegativeLifespanException {
    //System.out.println(Person.fromCsvLine("Marek Kowalski,15.05.1899,25.06.1957,,")); //przyjmowanie linii pliku jako argument

        Person p1 = new Person("Jan", "Kowalski", LocalDate.of(1973, 1, 1), LocalDate.of(2025, 4, 4));
        Person p2 = new Person("Jakub", "Nowak", LocalDate.of(1983, 11, 11), null);
        Person p3 = new Person("Anna", "Kowal", LocalDate.of(1993, 3, 13), null);

        List<Person> people = new ArrayList<>();

        people.add(p1);
        people.add(p2);
        people.add(p3);

        people.get(0);

        System.out.println(p1.adopt(p1));
        System.out.println(p1.adopt(p2));
        System.out.println(p1.adopt(p3));

        System.out.println(p1.getChildren().get(0));

        Family fam1 = new Family();
       fam1.add(p1, p2, p3);
        System.out.println(fam1.get("Jakub Nowak"));

        System.out.println(Person.fromCsvLine("Ewa Kowalska,03.11.1901,05.03.1990,,"));

        //System.out.println(Person.fromCsv("family(3).csv"));
       // Person.saveToTxt(Person.fromCsv("family(3).csv"),"sciezka");

        System.out.println("\n--- Zadanie 7: Testowanie zapisu i odczytu binarnego ---");
        List<Person> peopleToBinary = new ArrayList<>();
        peopleToBinary.add(new Person("Karol", "Nowak", LocalDate.of(2000, 1, 1), null));
        peopleToBinary.add(new Person("Zofia", "Kowalska", LocalDate.of(1985, 5, 10), LocalDate.of(2022, 12, 25)));

        Person.toBinaryFile(peopleToBinary, "people.bin");
        List<Person> readPeopleBinary = Person.fromBinaryFile("people.bin");
        for (Person person : readPeopleBinary) {
            System.out.println(person);
        }


    }
}