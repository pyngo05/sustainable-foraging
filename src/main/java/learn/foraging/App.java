package learn.foraging;

import learn.foraging.data.ForageFileRepository;
import learn.foraging.data.ForagerFileRepository;
import learn.foraging.data.ItemFileRepository;
import learn.foraging.domain.ForageService;
import learn.foraging.domain.ForagerService;
import learn.foraging.domain.ItemService;
import learn.foraging.ui.ConsoleIO;
import learn.foraging.ui.Controller;
import learn.foraging.ui.View;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {

        ApplicationContext container = new ClassPathXmlApplicationContext("dependency-configuration.xml");

        ConsoleIO io = new ConsoleIO();
        View view = container.getBean(View.class);

        ForageFileRepository forageFileRepository = container.getBean(ForageFileRepository.class );
        ForagerFileRepository foragerFileRepository = container.getBean(ForagerFileRepository.class);
        ItemFileRepository itemFileRepository = container.getBean(ItemFileRepository.class);

        ForagerService foragerService = container.getBean(ForagerService.class);
        ForageService forageService = container.getBean(ForageService.class);
        ItemService itemService = container.getBean(ItemService.class);

        Controller controller = container.getBean(Controller.class);
//        View view = new View(io);
//
//        ForageFileRepository forageFileRepository = new ForageFileRepository("./data/forage_data");
//        ForagerFileRepository foragerFileRepository = new ForagerFileRepository("./data/foragers.csv");
//        ItemFileRepository itemFileRepository = new ItemFileRepository("./data/items.txt");
//
//        ForagerService foragerService = new ForagerService(foragerFileRepository);
//        ForageService forageService = new ForageService(forageFileRepository, foragerFileRepository, itemFileRepository);
//        ItemService itemService = new ItemService(itemFileRepository);
//
//        Controller controller = new Controller(foragerService, forageService, itemService, view);
        controller.run();
    }
}
