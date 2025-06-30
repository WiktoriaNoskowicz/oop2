import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Person implements Comparable<Person>, Serializable {
    private String imie;
    private String nazwisko;
    private LocalDate birthday;
    private Set<Person> children;
    private LocalDate death;

    public Person(String imie, String nazwisko, LocalDate birthday, LocalDate deathDate) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.birthday = birthday;
        this.children = new HashSet<>();
        this.death = deathDate;
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        return this.imie + " " + this.nazwisko;
    }

    public LocalDate getDeath() {
        return death;
    }

    public boolean adopt(Person child) {
        if (child == null || child == this) return false;
        return this.children.add(child);
    }


    public Person getYoungestChild() {
        if (this.children.isEmpty()) return null;
        Person youngest = this.children.iterator().next();
        for (Person child : this.children) {
            if (youngest.compareTo(child) < 0) {
                youngest = child;
            }
        }
        return youngest;
    }

    @Override
    public int compareTo(Person other) {
//        return this.birthDate.compareTo(other.birthDate);
        if (this.birthday.isAfter(other.birthday)) {
            return 1; // other starszy
        } else if (this.birthday.isBefore(other.birthday)) {
            return -1; // other młodszy
        } else {
            return 0; // other równi
        }
    }

    public List<Person> getChildren() {
        List<Person> sortedChildren = new ArrayList<>(this.children);
        Collections.sort(sortedChildren, Collections.reverseOrder());
        //collection sort dziala przez to ze klasa impl comparable i na podst comapareTo() sortuje wywolujac to dla kazdego el
        return sortedChildren;
    }

    public static Person fromCsvLine(String line) throws NegativeLifespanException {
        String[] lineParts = line.split(",");
        String name = lineParts[0].split(" ")[0];
        String surname = lineParts[0].split(" ")[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate birthDate = LocalDate.parse(lineParts[1], formatter);
        LocalDate deathDate = null;
        if (!lineParts[2].isEmpty()) {
            deathDate = LocalDate.parse(lineParts[2], formatter);
            System.out.println("Data urodzin = " + birthDate);
            System.out.println("Data śmierci = " + deathDate);
            if (deathDate.isBefore(birthDate)) {
                throw new NegativeLifespanException("Data śmierci osoby " + name + " " + surname + " nie zgadza się");
            }
        }
        return new Person(name, surname, birthDate, deathDate);
    }

    public static List<Person> fromCsv(String path) throws NegativeLifespanException {

        List<Person> ppl = new ArrayList<>();
        Set<String> pplFullNames = new HashSet<>();
        Map<String, Person> pplMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                Person person = Person.fromCsvLine(line);
                String[] lineParts = line.split(",");
                String fullName = person.getImie() + " " + person.getNazwisko();
                if (pplFullNames.contains(fullName)) { //czy obiekt juz istnije
                    throw new AmbiguousPersonException("W pliku istnieje osoba o takim imieniu");
                }
                if (person != null) {
                    pplFullNames.add(fullName);
                    ppl.add(person);
                    pplMap.put(fullName, person); //aby latwo odnalesc rodzicow

                    if (lineParts.length > 3) { //czy linia zawiera czwarta kolumne czyli nazwe pierwszego rodzica
                        if (!lineParts[3].isEmpty()) { //i czy nie jest puste
                            Person parent1 = pplMap.get(lineParts[3]);
                            if (parent1 != null) {
                                try {
                                    checkParentngAge(parent1, person);
                                    parent1.adopt(person);
                                } catch (ParentingAgeException e) {
                                    System.out.println("Błąd " + e.getMessage());
                                    System.out.println("Czy na pewno dodać?");
                                    Scanner scanner = new Scanner(System.in);
                                    String input = scanner.nextLine();
                                    if (input.equalsIgnoreCase("y")) {
                                        parent1.adopt(person);
                                    }
                                }

                            }
                        }
                    }

                    if (lineParts.length > 4) { // odczytujemy 2 rodzica, sprawdzamy wiek i pytamy czy dodac
                        if (!lineParts[4].isEmpty()) {
                            Person parent2 = pplMap.get(lineParts[4]);
                            if (parent2 != null) {
                                try {
                                    checkParentngAge(parent2, person);
                                    parent2.adopt(person);
                                } catch (ParentingAgeException e) {
                                    System.out.println("Błąd " + e.getMessage());
                                    System.out.println("Czy na pewno dodać?");
                                    Scanner scanner = new Scanner(System.in);
                                    String input = scanner.nextLine();
                                    if (input.equalsIgnoreCase("y")) {
                                        parent2.adopt(person);
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }  catch (FileNotFoundException e) {
            System.out.println("Nie znaloeziono pliku w metodzie fromCsv w klasie Pearson");
        } catch (IOException e) {
            System.out.println("Błąd IOException w metodzie fromCsv w klasie Pearson");
        } catch (AmbiguousPersonException e) {
            throw new RuntimeException(e);
        }
        return ppl;
    }
    public static void checkParentngAge(Person parent, Person child) throws ParentingAgeException {
        if (parent.getBirthday().plusYears(15).isAfter(child.getBirthday())) {
            throw new ParentingAgeException("Rodzic jest młodszy niz 15 lat");
        }
        if (parent.getDeath() != null && parent.getDeath().isBefore(child.getBirthday())) {
            throw new ParentingAgeException("Rodzic nie żyje w chwili urodzin dziecka");
        }
    }
    public static void saveToTxt(List<Person> people, String filePath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (PrintWriter writer = new PrintWriter(filePath)) {
            for (Person person : people) {
                StringBuilder sb = new StringBuilder();
                sb.append(person.getImie()).append(" ").append(person.getNazwisko()).append(",");
                sb.append(person.getBirthday().format(formatter)).append(",");
                sb.append(person.getDeath() != null ? person.getDeath().format(formatter) : "").append(",");

                // Rodzice
                List<String> parents = new ArrayList<>();
                for (Person potentialParent : people) {
                    if (potentialParent.getChildren().contains(person)) {
                        parents.add(potentialParent.getImie() + " " + potentialParent.getNazwisko());
                    }
                }

                // Dodaj maksymalnie dwóch rodziców
                if (parents.size() > 0) sb.append(parents.get(0));
                sb.append(",");
                if (parents.size() > 1) sb.append(parents.get(1));

                writer.println(sb.toString());
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisu do pliku txt: " + e.getMessage());
        }
    }
    public static void toBinaryFile(List<Person> persons, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) { //otwieranie strumienia
            //ObjectOutputStream pozwala zapisywac cale obiekty
            // SERIALIZABLE MUSI byc impelentowany
            oos.writeObject(persons);
            System.out.println("Zapisano osoby do pliku binarnego: " + path);
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisu do pliku binarnego: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked") //wylaczenie ostrzezenia o niebezpiecznym rzutowaniu
    public static List<Person> fromBinaryFile(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {//Sprawdza, czy odczytany obiekt to lista (List<?>).
                return (List<Person>) obj; //Jeśli tak, rzutuje go na List<Person> i zwraca.
            } else {
                System.out.println("Plik nie zawiera listy osób.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Błąd podczas odczytu z pliku binarnego: " + e.getMessage());
        }
        return new ArrayList<>();
    }

}


