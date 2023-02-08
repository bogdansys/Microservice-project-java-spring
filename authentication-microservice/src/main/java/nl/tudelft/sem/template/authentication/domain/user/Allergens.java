package nl.tudelft.sem.template.authentication.domain.user;
//Domain Object

import java.util.ArrayList;

public class Allergens {
    private final transient ArrayList<Integer> allergensArray;

    public Allergens() {
        allergensArray = new ArrayList<>();
    }

    public Allergens(String dbData) {
        allergensArray = new ArrayList<>();
        if (dbData != null) {
            String[] allergenArray = dbData.split(",");
            for (String allergen : allergenArray) {
                allergensArray.add(Integer.parseInt(allergen));
            }
        }
    }

    public void addAllergen(int allergen) {
        if (allergensArray.contains(allergen)) {
            return;
        }
        allergensArray.add(allergen);
    }

    public ArrayList<Integer> getAllergens() {
        return allergensArray;
    }

    public boolean contains(int allergen) {
        return allergensArray.contains(allergen);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int allergen : allergensArray) {
            sb.append(allergen);
            sb.append(",");
        }
        return sb.toString();
    }

    public int size() {
        return allergensArray.size();
    }

    public void removeAllergen(int allergen) {
        allergensArray.remove((Integer) allergen);
    }

    public boolean hasAllergen(int allergen) {
        return allergensArray.contains(allergen);
    }


}
