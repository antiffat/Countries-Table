package zad1;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

public class WorldCountriesAPI {
	public void refreshCountriesFile(String fullPathToFile) {
		try {
			// URL obj for the api
			URL url = new URL("https://restcountries.com/v3.1/all");
			
			// open a connection to the url
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET"); // 'GET' is HTTP request method, indicating that the code intents to retrieve data from the API
			
			// read the response from api
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			// object to save the read lines from the reader
			StringBuilder response = new StringBuilder(); // StringBuilder is mutable. String is immutable. mutable = flexible
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line); 
			}
			reader.close();
			
			// close the connection
			connection.disconnect();
			JsonCountryDataParser.parseAndWriteToFile(response.toString(), fullPathToFile);
			// structure: parseAndWriteToFile(String jsonString, String fullPathToFile)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
