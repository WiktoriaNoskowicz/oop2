import java.util.*;

public class Family {
    private Map<String,List<Person>> mapa;

    public Family() {
        this.mapa = new HashMap<>();
    }
    public void add(Person... persons){ //wariadyczna lista/arg zmienny -> przekazujemy dowolna liczbe argumentow
        for (Person p: persons) {
            String key = p.getImie() + " " + p.getNazwisko();
            this.mapa.putIfAbsent(key, new ArrayList<>()); //przypisanie do klucza listy jesli nie istnieje
            this.mapa.get(key).add(p); //do klucza w ktorym jest lista dodajemy nowa osobe
        }
    }

    public Person[] get(String key){
        List<Person> people = this.mapa.get(key); //pobiera liste przypisana do klucza
        if (people == null || people.isEmpty()){
            return null;
        }

        people.sort(Comparator.reverseOrder());
        return people.toArray(new Person[0]); //konwersja listy na tablice i zwrocenie jako Person[]
    }

}
