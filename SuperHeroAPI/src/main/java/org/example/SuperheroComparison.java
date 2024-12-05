package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

public class SuperheroComparison {

    // Your superheroapi.com API key
    private static final String API_KEY = System.getenv("SUPERHERO_API_KEY"); // Ensure the API_KEY is set in the environment variable
    private static final String API_URL = "https://superheroapi.com/api/{API_KEY}/{HERO_ID}";

    public static void main(String[] args) {
        // Generate random hero IDs (e.g., between 1 and 731)
        int hero1Id = generateRandomHeroId();
        int hero2Id = generateRandomHeroId();

        // Fetch hero stats for the two randomly selected superheroes
        JsonObject hero1Data = fetchHeroData(hero1Id);
        JsonObject hero2Data = fetchHeroData(hero2Id);

        // Extract names of the superheroes
        String hero1Name = hero1Data.get("name").getAsString();
        String hero2Name = hero2Data.get("name").getAsString();

        // Display hero names and their powerstats
        System.out.println("Hero 1: " + hero1Name + " (ID: " + hero1Id + ")");
        displayPowerstats(hero1Data);
        double hero1AveragePower = calculateAveragePowerstat(hero1Data);
        System.out.println("Average Powerstat of " + hero1Name + ": " + hero1AveragePower);
        System.out.println();

        System.out.println("Hero 2: " + hero2Name + " (ID: " + hero2Id + ")");
        displayPowerstats(hero2Data);
        double hero2AveragePower = calculateAveragePowerstat(hero2Data);
        System.out.println("Average Powerstat of " + hero2Name + ": " + hero2AveragePower);
        System.out.println();

        // Compare the average powerstats and determine which superhero is stronger
        if (hero1AveragePower > hero2AveragePower) {
            System.out.println(hero1Name + " is stronger with an average powerstat of " + hero1AveragePower);
        } else if (hero2AveragePower > hero1AveragePower) {
            System.out.println(hero2Name + " is stronger with an average powerstat of " + hero2AveragePower);
        } else {
            System.out.println("Both superheroes have equal average powerstats!");
        }
    }

    /**
     * Fetches data for a specific superhero using their ID.
     */
    public static JsonObject fetchHeroData(int heroId) {
        String url = API_URL.replace("{API_KEY}", API_KEY).replace("{HERO_ID}", String.valueOf(heroId));
        String response = new RestTemplate().getForObject(url, String.class);

        // Parse the JSON response and return as JsonObject
        return JsonParser.parseString(response).getAsJsonObject();
    }

    /**
     * Displays the powerstats of the superhero.
     */
    public static void displayPowerstats(JsonObject heroData) {
        JsonObject powerstats = heroData.getAsJsonObject("powerstats");

        System.out.println("Powerstats:");
        for (Map.Entry<String, com.google.gson.JsonElement> entry : powerstats.entrySet()) {
            String statName = entry.getKey();
            // Safely check if the value is a valid integer before parsing it
            try {
                int statValue = entry.getValue().getAsInt();
                System.out.println(statName + ": " + statValue);
            } catch (NumberFormatException e) {
                System.out.println(statName + ": Invalid or missing value");
            }
        }
    }

    /**
     * Calculates the average powerstat of a superhero based on their powerstats.
     */
    public static double calculateAveragePowerstat(JsonObject heroData) {
        // Fetch the "powerstats" object from the JSON response
        JsonObject powerstats = heroData.getAsJsonObject("powerstats");

        // Sum all the powerstat values
        double totalPower = 0;
        int numberOfStats = 0;

        // Iterate through the powerstats and sum the values
        for (Map.Entry<String, com.google.gson.JsonElement> entry : powerstats.entrySet()) {
            try {
                int statValue = entry.getValue().getAsInt();
                totalPower += statValue;
                numberOfStats++;
            } catch (NumberFormatException e) {
                // If the value is invalid, we skip this entry
            }
        }

        // Avoid division by zero if there are no valid stats
        return numberOfStats > 0 ? totalPower / numberOfStats : 0;
    }

    /**
     * Generates a random hero ID between 1 and 731 (the range of IDs for superheroes).
     */
    public static int generateRandomHeroId() {
        Random random = new Random();
        return random.nextInt(731) + 1; // Random number between 1 and 731
    }
}
