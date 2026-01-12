package pl.bartek537.snapdrop.features.share;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Service
public class SlugService {

    private final Faker faker = new Faker();

    public String generateSlug() {
        String adjective = faker.word().adjective();
        String color = faker.color().name();
        String animal = faker.animal().name();

        return String.format("%s-%s-%s", adjective, color, animal).toLowerCase().replace(" ", "-");
    }
}
