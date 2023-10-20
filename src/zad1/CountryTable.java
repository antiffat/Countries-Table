package zad1;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.imageio.ImageIO;

public class CountryTable {
	ArrayList<String> rawParsedFile; // instance var, will hold lines of data read from file

	
    public CountryTable(String countriesFileName) {
    	String fullPathToFile = System.getProperty("user.dir") + "/" + countriesFileName; // concatenates 2 strings, current working directory and filename
    	//.getProperty() is used to access system properties, "user.dir" is a system property - represents the current working dir
    	
    	WorldCountriesAPI wca = new WorldCountriesAPI();
    	wca.refreshCountriesFile(fullPathToFile);
    	
    	rawParsedFile = new ArrayList<>();
    	
    	try (BufferedReader br = new BufferedReader(new FileReader(fullPathToFile))) {
    		String line;
    		
    		while ((line = br.readLine()) != null) {
    			this.rawParsedFile.add(line);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    // method to return splitted line
    String[] getCountryDataByIndex(int index) {
    	String rawCountryData = this.rawParsedFile.get(index);
    	return rawCountryData.split("\\t+");
    }
     
    
    public JTable create() {
        String[] columnNames = this.getCountryDataByIndex(0);
        String[][] data = new String[this.rawParsedFile.size()][columnNames.length];

        for (int i = 1; i < this.rawParsedFile.size(); i++) {
            String[] country = this.getCountryDataByIndex(i);
            for (int j = 0; j < country.length; j++) {
                data[i][j] = country[j];
            }
        }

        JTable t = new JTable(data, columnNames);

        t.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof String) {
                    String imageUrlString = (String) value; // CASTING THE VALUE TO A STRING
                    try {
                        URL imageUrl = new URL(imageUrlString);

                        // Apply the User-Agent header to the URL connection
                        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                        Image img = ImageIO.read(connection.getInputStream());

                        
                        int widthOfCell = table.getColumnModel().getColumn(column).getWidth();
                        int HeightOfCell = table.getRowHeight(row);
                        int WidthOfImage = img.getWidth(null);
                        int HeightOfImage = img.getHeight(null);
                        // null is used as the ImageObserver parameter because, in this context, 
                        // I'm not interested in monitoring the image loading process, and you only want to obtain the image's width or height.

                        double ScaleOfWidth = (double) widthOfCell / WidthOfImage; // double = get more accurate answer
                        double ScaleOfHeight = (double) HeightOfCell / HeightOfImage;
                        double scale = Math.min(ScaleOfWidth, ScaleOfHeight);

                        int scaledWidth = (int) (scale * WidthOfImage);
                        int scaledHeight = (int) (scale * HeightOfImage);

                        ImageIcon icon = new ImageIcon(img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH));
                        setIcon(icon);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Clear the icon if the cell value is not a URL
                    setIcon(null);
                }
                return this;
            }
        });
        
        // MAKE RED FONT COLOR WHERE THE POP IS > 20M
        t.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
        	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        		
        		if (value instanceof String) {
                    String populationStr = (String) value;
                    
                    try {
                        int population = Integer.parseInt(populationStr);
                        int POPULATION_HIGHLIGHTING_LIMIT = 20_000_000;

                        if (population > POPULATION_HIGHLIGHTING_LIMIT) {
                            setBackground(Color.RED);
                        } else {
                            setBackground(table.getBackground());
                        }
                        
                    } catch (NumberFormatException e) {
                        setBackground(table.getBackground());
                    }
                    
                } else {
                    setBackground(table.getBackground());
                }
        		return this;
        	}
        });

        return t;
    }

}