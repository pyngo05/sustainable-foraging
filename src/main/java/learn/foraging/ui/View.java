package learn.foraging.ui;

import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class View {

    private final ConsoleIO io;

    public View(ConsoleIO io) {
        this.io = io;
    }

    public MainMenuOption selectMainMenuOption() {
        displayHeader("Main Menu");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (MainMenuOption option : MainMenuOption.values()) {
            if (!option.isHidden()) {
                io.printf("%s. %s%n", option.getValue(), option.getMessage());
            }
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max - 1);
        return MainMenuOption.fromValue(io.readInt(message, min, max));
    }

    public LocalDate getForageDate() {
        displayHeader(MainMenuOption.VIEW_FORAGES_BY_DATE.getMessage());
        return io.readLocalDate("Select a date [MM/dd/yyyy]: ");
    }

    public String getForagerNamePrefix() {
        return io.readRequiredString("Forager last name starts with: ");
    }

    public Forager chooseForager(List<Forager> foragers) {
        if (foragers.size() == 0) {
            io.println("No foragers found");
            return null;
        }

        int index = 1;
        for (Forager forager : foragers.stream().limit(25).collect(Collectors.toList())) {
            io.printf("%s: %s %s%n", index++, forager.getFirstName(), forager.getLastName());
        }
        index--;

        if (foragers.size() > 25) {
            io.println("More than 25 foragers found. Showing first 25. Please refine your search.");
        }
        io.println("0: Exit");
        String message = String.format("Select a forager by their index [0-%s]: ", index);

        index = io.readInt(message, 0, index);
        if (index <= 0) {
            return null;
        }
        return foragers.get(index - 1);
    }

    public Category getItemCategory() {
        displayHeader("Item Categories");
        int index = 1;
        for (Category c : Category.values()) {
            io.printf("%s: %s%n", index++, c);
        }
        index--;
        String message = String.format("Select a Category [1-%s]: ", index);
        return Category.values()[io.readInt(message, 1, index) - 1];
    }

    public String getForagerState() {
        displayHeader("Search for Foragers by State");
        String state = io.readString("State: ");
        return state;
    }

    public Item chooseItem(List<Item> items) {

        displayItems(items);

        if (items.size() == 0) {
            return null;
        }

        int itemId = io.readInt("Select an item id: ");
        Item item = items.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElse(null);

        if (item == null) {
            displayStatus(false, String.format("No item with id %s found.", itemId));
        }

        return item;
    }

    public Forage makeForage(Forager forager, Item item) {
        Forage forage = new Forage();
        forage.setForager(forager);
        forage.setItem(item);
        forage.setDate(io.readLocalDate("Forage date [MM/dd/yyyy]: "));
        String message = String.format("Kilograms of %s: ", item.getName());
        forage.setKilograms(io.readDouble(message, 0.001, 250.0));
        return forage;
    }

    public Item makeItem() {
        displayHeader(MainMenuOption.ADD_ITEM.getMessage());
        Item item = new Item();
        item.setCategory(getItemCategory());
        item.setName(io.readRequiredString("Item Name: "));
        item.setDollarPerKilogram(io.readBigDecimal("$/Kg: ", BigDecimal.ZERO, new BigDecimal("7500.00")));
        return item;
    }

    public Forager makeForager() {
        displayHeader(MainMenuOption.ADD_FORAGER.getMessage());
        Forager forager = new Forager();
        forager.setFirstName(io.readRequiredString("First Name: "));
        forager.setLastName(io.readRequiredString("Last Name: "));
        forager.setState(io.readRequiredString("State: "));
        return forager;
    }

    public GenerateRequest getGenerateRequest() {
        displayHeader(MainMenuOption.GENERATE.getMessage());
        LocalDate start = io.readLocalDate("Select a start date [MM/dd/yyyy]: ");
        if (start.isAfter(LocalDate.now())) {
            displayStatus(false, "Start date must be in the past.");
            return null;
        }

        LocalDate end = io.readLocalDate("Select an end date [MM/dd/yyyy]: ");
        if (end.isAfter(LocalDate.now()) || end.isBefore(start)) {
            displayStatus(false, "End date must be in the past and after the start date.");
            return null;
        }

        GenerateRequest request = new GenerateRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setCount(io.readInt("Generate how many random forages [1-500]?: ", 1, 500));
        return request;
    }

    public void enterToContinue() {
        io.readString("Press [Enter] to continue.");
    }

    // display only
    public void displayHeader(String message) {
        io.println("");
        io.println(message);
        io.println("=".repeat(message.length()));
    }

    public void displayException(Exception ex) {
        displayHeader("A critical error occurred:");
        io.println(ex.getMessage());
    }

    public void displayStatus(boolean success, String message) {
        displayStatus(success, List.of(message));
    }

    public void displayStatus(boolean success, List<String> messages) {
        displayHeader(success ? "Success" : "Error");
        for (String message : messages) {
            io.println(message);
        }
    }

    public void displayForages(List<Forage> forages) {
        if (forages == null || forages.isEmpty()) {
            io.println("No forages found.");
            return;
        }
        for (Forage forage : forages) {
            io.printf("%s %s - %s:%s - Value: $%.2f%n",
                    forage.getForager().getFirstName(),
                    forage.getForager().getLastName(),
                    forage.getItem().getName(),
                    forage.getItem().getCategory(),
                    forage.getValue()
            );
        }
    }

    public void displayForagers(List<Forager> foragers) {
        if (foragers == null || foragers.isEmpty()) {
            io.println("No foragers found.");
            return;
        }
        for (Forager forager : foragers) {
            io.printf("%s %s%n",
                    forager.getFirstName(),
                    forager.getLastName()
            );
        }
    }

    public void displayItems(List<Item> items) {

        if (items.size() == 0) {
            io.println("No items found");
        }

        for (Item item : items) {
            io.printf("%s: %s, %s, %.2f $/kg%n", item.getId(), item.getName(), item.getCategory(), item.getDollarPerKilogram());
        }
    }

    public void displayItemWeights(Map<Item, Double> itemWeights) {
        if (itemWeights == null || itemWeights.isEmpty()) {
            io.println("No items.");
            return;
        }

        io.println("Item ID\tItem Name\tWeight (kg)");
        itemWeights.forEach((item, weight) -> io.printf("%d\t\t%s\t%f\n", item.getId(), item.getName(), weight));
    }
}
