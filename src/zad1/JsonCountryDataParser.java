package zad1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.*;

public class JsonCountryDataParser {
    public static void parseAndWriteToFile(String jsonString, String fullPathToFile) {
        try (FileWriter writer = new FileWriter(fullPathToFile)) {
            writer.write("COUNTRY_NAME\tFLAG\tCAPITAL\tPOPULATION\n");

            // Split the JSON data into individual country objects
            String[] countries = jsonString.split("\\},\\{");

            for (String countryData : countries) {
                String country = extractValue(countryData, "common\":\"(.*?)\"");
                String flag = extractValue(countryData, "png\":\"(.*?)\"");
                String capital = extractValue(countryData, "capital\":\\[\"(.*?)\"");
                String populationStr = extractValue(countryData, "population\":(\\d+)");

                int population = Integer.parseInt(populationStr);

                writer.append(country + "\t" + flag + "\t" + capital + "\t" + population + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractValue(String data, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "N/A"; // Return "N/A" if the value is not found
    }
}
