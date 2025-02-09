package com.ilyasbugra.excusegenerator.loader;

import com.github.javafaker.Faker;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private static final int BATCH_SIZE = 8000;
    private final ExcuseRepository excuseRepository;

    @Value("${excuse.loader.count:50000}")
    private int requestedExcuseCount;

    public DataLoader(ExcuseRepository excuseRepository) {
        this.excuseRepository = excuseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        long excuseCount = excuseRepository.count();
        logger.info("Database has {} excuses", excuseCount);


//        if (excuseCount >= requestedExcuseCount) {
//            logger.debug("Skipping data load. DB has enough data available!");
//            return;
//        }
        // Skipping the data load
        if (excuseCount < -1) {
            logger.warn("Skipping data load. DataLoader as of right now is not functional.");
            return;
        }

        Faker faker = new Faker();
        List<Excuse> excuses = new ArrayList<>();

        // Timer setup just in case of infinite loops or some other weird error
        long batchInsertionTime = System.currentTimeMillis(); // we are resetting after each batch insertion
        long maxDurationBeforeAbort = 60 * 1000; // 1 minute

        logger.info("Starting data generation to reach a total of {} rows. Current count: {}",
                String.format("%,d", requestedExcuseCount),
                String.format("%,d", excuseCount));

        for (long i = excuseCount; i < requestedExcuseCount; i++) {
            // Timer check
            if (System.currentTimeMillis() - batchInsertionTime > maxDurationBeforeAbort) {
                logger.error("Data insertion time exceeded 1 minute. Aborting!!");
                break;
            }

            // Excuse Generation
            final String excuseMessage = getRandomExcuseMessage(faker);
            final String category = getRandomCategory(faker);

            final String trimmedExcuseMessage = excuseMessage.length() > 255
                    ? excuseMessage.substring(0, 255)
                    : excuseMessage;

            final String trimmedCategory = category.length() > 50
                    ? category.substring(0, 50)
                    : category;

            Excuse excuse = Excuse.builder()
                    .excuseMessage(trimmedExcuseMessage)
                    .category(trimmedCategory)
                    .build();

            excuses.add(excuse);

            // batched adding
            if (i % BATCH_SIZE == 0) {
                logger.trace("Inserting batch of {} excuses", excuses.size());
                excuseRepository.saveAll(excuses);
                logger.debug("Inserted batch of {} excuses", excuses.size());

                // Resetting the Timer
                batchInsertionTime = System.currentTimeMillis();

                excuses.clear();
                logger.info("Inserted {} rows so far...", i);
            }
        }
        // if anything remains after loop just add them
        if (!excuses.isEmpty()) {
            excuseRepository.saveAll(excuses);
            logger.info("Final batch of {} excuses inserted.", excuses.size());
        }

        logger.info("Data generation complete! Total rows in DB: {}", String.format("%,d", excuseRepository.count()));
    }

    private String getRandomExcuseMessage(Faker faker) {
        return switch (faker.random().nextInt(5)) {
            case 0 -> faker.chuckNorris().fact();
            case 1 -> faker.hitchhikersGuideToTheGalaxy().quote();
            case 2 -> faker.yoda().quote();
            case 3 -> faker.rickAndMorty().quote();
            case 4 -> faker.backToTheFuture().quote();
            default -> faker.lorem().sentence(10);
        };
    }

    private String getRandomCategory(Faker faker) {
        return switch (faker.random().nextInt(4)) {
            case 0 -> faker.company().buzzword();
            case 1 -> faker.company().industry();
            case 2 -> faker.hacker().noun();
            case 3 -> faker.company().bs();
            default -> "Misc";
        };
    }
}
