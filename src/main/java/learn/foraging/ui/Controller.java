package learn.foraging.ui;

import learn.foraging.data.DataException;
import learn.foraging.domain.ForageService;
import learn.foraging.domain.ForagerService;
import learn.foraging.domain.ItemService;
import learn.foraging.domain.Result;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {

    private final ForagerService foragerService;
    private final ForageService forageService;
    private final ItemService itemService;
    private final View view;

    public Controller(ForagerService foragerService, ForageService forageService, ItemService itemService, View view) {
        this.foragerService = foragerService;
        this.forageService = forageService;
        this.itemService = itemService;
        this.view = view;
    }

    public void run() {
        view.displayHeader("Welcome to Sustainable Foraging");
        try {
            runAppLoop();
        } catch (DataException ex) {
            view.displayException(ex);
        }
        view.displayHeader("Goodbye.");
    }

    private void runAppLoop() throws DataException {
        MainMenuOption option;
        do {
            option = view.selectMainMenuOption();
            switch (option) {
                case VIEW_FORAGES_BY_DATE:
                    viewByDate();
                    break;
                case VIEW_ITEMS:
                    viewItems();
                    break;
                case VIEW_FORAGERS:
                    viewForagers();
                    break;
                case ADD_FORAGE:
                    addForage();
                    break;
                case ADD_FORAGER:
                    addForager();
                    break;
                case ADD_ITEM:
                    addItem();
                    break;
                case REPORT_KG_PER_ITEM:
                    displayItemWeightsByDate();
                    break;
                case REPORT_CATEGORY_VALUE:
                    displayCategoryValuesByDate();
                    break;
                case GENERATE:
                    generate();
                    break;
            }
        } while (option != MainMenuOption.EXIT);
    }

    // reports on total weight foraged for all items on a particular date
    public void displayItemWeightsByDate() {

        // get date from user
        LocalDate date = view.getForageDate();

        // get forages on that date
        List<Forage> forages = forageService.findByDate(date);

        // group forages by item, summing the weight of each item
        // (should this logic be in the service layer?)
        Map<Item, Double> itemWeights = forages.stream()
                .collect(Collectors.groupingBy(
                        Forage::getItem, // key
                        Collectors.summingDouble(Forage::getKilograms) // aggregated value
                ));

        // display items with their total weight
        view.displayItemWeights(itemWeights);

    }

    // reports on total value foraged for all categories on a particular date
    public void displayCategoryValuesByDate() {

        // get date from user
        LocalDate date = view.getForageDate();

        // get forages on that date
        List<Forage> forages = forageService.findByDate(date);

        // group forages by category, summing the value of each category
        // (should this logic be in the service layer?)
        Map<Category, Double> categoryValues = forages.stream()
                .collect(Collectors.groupingBy(
                        forage -> forage.getItem().getCategory(), // key
                        Collectors.summingDouble(forage -> new BigDecimal(forage.getKilograms()).multiply(forage.getItem().getDollarPerKilogram()).doubleValue()) // aggregated value
                ));

        // display items with their total weight
        view.displayCategoryValues(categoryValues);

    }

    // top level menu
    private void viewByDate() {
        LocalDate date = view.getForageDate();
        List<Forage> forages = forageService.findByDate(date);
        view.displayForages(forages);
        view.enterToContinue();
    }

    private void viewItems() {
        view.displayHeader(MainMenuOption.VIEW_ITEMS.getMessage());
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        view.displayHeader("Items");
        view.displayItems(items);
        view.enterToContinue();
    }

    private void viewForagers() {
        view.displayHeader(MainMenuOption.VIEW_FORAGERS.getMessage());
        String state = view.getForagerState();
        List<Forager> foragers = foragerService.findByState(state);
        view.displayHeader("Foragers");
        view.displayForagers(foragers);
        view.enterToContinue();
    }

    private void addForage() throws DataException {
        view.displayHeader(MainMenuOption.ADD_FORAGE.getMessage());
        Forager forager = getForager();
        if (forager == null) {
            return;
        }
        Item item = getItem();
        if (item == null) {
            return;
        }
        Forage forage = view.makeForage(forager, item);
        Result<Forage> result = forageService.add(forage);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forage %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void addItem() throws DataException {
        Item item = view.makeItem();
        Result<Item> result = itemService.add(item);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Item %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void addForager() throws DataException {
        Forager forager = view.makeForager();
        Result<Forager> result = foragerService.add(forager);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forager %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void generate() throws DataException {
        GenerateRequest request = view.getGenerateRequest();
        if (request != null) {
            int count = forageService.generate(request.getStart(), request.getEnd(), request.getCount());
            view.displayStatus(true, String.format("%s forages generated.", count));
        }
    }

    // support methods
    private Forager getForager() {
        String lastNamePrefix = view.getForagerNamePrefix();
        List<Forager> foragers = foragerService.findByLastName(lastNamePrefix);
        return view.chooseForager(foragers);
    }

    private Item getItem() {
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        return view.chooseItem(items);
    }
}
