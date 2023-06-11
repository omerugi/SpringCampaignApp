package com.example.mabaya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
// TODO: Exception handler
// TODO: Use logger
// TODO: Add documentation
// TODO: maybe use flyway to handle migration

public class MabayaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MabayaApplication.class, args);
    }


//    @Bean
//    CommandLineRunner initDatabase(ProductRepo productRepo, CampaignRepo campaignRepo) {
//        return args -> {
//            Faker faker = new Faker();
//            Random rand = new Random();
//            String[] categories = {"Electronics", "Books", "Clothing", "Home", "Sport"};
//
//            // Create 15 different products
//            List<Product> products = new ArrayList<>();
//            for (int i = 0; i < 15; i++) {
//                String category = categories[i % 5];
//                Product product = new Product();
//                String productSerialNumber;
//                do {
//                    productSerialNumber = faker.idNumber().valid();
//                } while (productRepo.findById(productSerialNumber).isPresent());
//                product.setProductSerialNumber(productSerialNumber);
//                product.setTitle(faker.commerce().productName());
//                product.setCategory(category);
//                product.setPrice(Double.valueOf(faker.commerce().price()));
//                product.setActive(true);
//                products.add(product);
//            }
//            productRepo.saveAll(products);
//
//            // Create 20 different campaigns
//            for (int i = 0; i < 20; i++) {
//                Campaign campaign = new Campaign();
//                campaign.setName(faker.company().name());
//                campaign.setStartDate(LocalDate.now().minusDays(rand.nextInt(9)));
//                campaign.setBid(rand.nextDouble() * 100);
//                campaign.setActive(true);
//
//                Set<Product> campaignProducts = new HashSet<>();
//                campaignProducts.add(products.get(i % products.size()));
//                campaignProducts.add(products.get((i + 1) % products.size()));
//                campaignProducts.add(products.get((i + 2) % products.size()));
//
//                campaign.setProducts(campaignProducts);
//                campaignRepo.save(campaign);
//            }
//        };
//
//    }
}
