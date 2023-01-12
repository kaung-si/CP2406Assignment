package rainfallanalyser.cp2406assignment_alpha;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.csv.*;

/**
 * CP2406 Assignment - Kaung Sithu
 * Rainfall Analyser: Alpha Version
 * This program will read rainfall data from a file that the user specifies
 * and return the analysed version of the dataset that is supposed to be used in visualiser program.
 * This version can only analyse files stored in the rainfalldata directory of the project.
 *
 * See https://github.com/kaung-si/CP2406Assignment for the project repository
 */
public class RainfallAnalyser {

    public static void main(String[] args) {
        /*Print welcome + instruction message for the user. */
        System.out.println("Welcome to the Rainfall Analyser");
        System.out.println("This program analyses the rainfall data given from various sources like BOM");
        System.out.println("It will then return the extracted Total Monthly Rainfall for the data set");
        System.out.println("as well as the minimum and maximum rainfall that occurred that month.");
        System.out.println("Enter zero to exit the program.");

        while (true) {
            try {
                String filename = getFilename();
                if (filename == null) {
                    System.out.println("Goodbye!");
                    break;
                }
                ArrayList<String> analysedRainfallData = analyseRainfallData(filename);
                saveRainfallData(analysedRainfallData, filename);
                System.out.println(filename + " successfully analysed!");

            } catch (Exception e) {
                System.out.println("Error: there was an issue");
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Writes the analysed rainfall data to the /rainfalldata_analysed/ directory.
     * Added as a separate method at the end to avoid creating unnecessary files.
     */
    private static void saveRainfallData(ArrayList<String> analysedRainfallData, String filename) {
        // Set the output file based on the file being analysed
        TextIO.writeFile(getSavePath(filename));
        TextIO.putln("year,month,total,minimum,maximum");

        for (String rainfallData: analysedRainfallData) {
            TextIO.putln(rainfallData);
        }
    }

    /**
     * Load a file to be analysed and create an ArrayList representing rainfall data for that csv file.
     * Uses the Commons CSV package from Apache (https://commons.apache.org/proper/commons-csv/).
     */
    private static ArrayList<String> analyseRainfallData(String fileName) throws Exception {

        // Check if the file is empty
        File f = new File("src/main/resources/rainfalldata/" + fileName);
        if (f.length() == 0)
            throw new Exception("That file is empty");

        // Create the FileReader and CSV reader object.
        // See https://commons.apache.org/proper/commons-csv/
        Reader reader = new FileReader("src/main/resources/rainfalldata/" + fileName);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader); // Read the file and assign to records.

        int year, month, day;
        double rainfall;
        int currentYear = 0;
        int currentMonth = 1;
        double monthlyTotal = 0.0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = 0.0;
        ArrayList<String> rainfallData = new ArrayList<>();

        for (CSVRecord record : records) {
            // Get the data from specific columns of the Rainfall Data CSV file.
            String yearText = record.get("Year");
            String monthText = record.get("Month");
            String dayText = record.get("Day");
            String rainfallText = record.get("Rainfall amount (millimetres)");

            // Analyse the data and do explicit casting into the expected output format.
            year = Integer.parseInt(yearText);
            month = Integer.parseInt(monthText);
            day = Integer.parseInt(dayText);

            // Check if the recorded date format is correct.
            if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
                System.out.println("Error: Invalid format for dates");
                throw new NumberFormatException ("Dates are out of expected range.");
            }

            // Replace the null rainfall value under Rainfall amount (millimetres) dimension with zero.
            rainfall = Objects.equals(rainfallText, "") ? 0 : Double.parseDouble(rainfallText);

            // Check to see if it's the next month. If it is the next month, save the data and reset the totals/values to 0.
            if (month != currentMonth) {
                // Check if it is the first year before saving the data
                rainfallData.add(writeCurrentData(monthlyTotal, minRainfall, maxRainfall, currentMonth, currentYear == 0? year : currentYear));
                currentYear = year;
                currentMonth = month;
                monthlyTotal = 0;
                maxRainfall = 0.0;
                minRainfall = Double.POSITIVE_INFINITY; // Setting positive infinity value as default.
            }

            // Calculate total rainfall value from "Rainfall amount (millimetres)" dimension.
            monthlyTotal += rainfall;
            if (rainfall > maxRainfall) maxRainfall = rainfall;
            if (rainfall < minRainfall) minRainfall = rainfall;
        }
        // Catch an incomplete month when exiting the for loop
        rainfallData.add(writeCurrentData(monthlyTotal, minRainfall, maxRainfall, currentMonth, currentYear));
        return rainfallData;
    }

    /**
     * Return a String representation of the months data.
     */
    private static String writeCurrentData(double monthlyTotal, double minRainfall, double maxRainfall, int month, int year) {
        return String.format("%d,%d,%1.2f,%1.2f,%1.2f", year, month, monthlyTotal, minRainfall, maxRainfall);
    }

    /**
     * Get a list of available rainfall data sets to be analysed.
     * Allows the user to pick which data set to analyse from this list.
     */
    private static String getFilename() {
        System.out.println("\nThe files available are:");
        File f = new File("src/main/resources/rainfalldata");
        String[] pathNames = f.list();

        assert pathNames != null; // Make sure pathNames array is not empty.
        // Print the list of csv file names in a folder.
        for (int i = 0; i < pathNames.length; i++) {
            System.out.println((i+1) + ": " + pathNames[i]);
        }

        System.out.println("\nWhich file would you like to analyse? Enter the corresponding number");

        int fileNumber;
        String filename;
        while (true) {
            // Check that the selected file is valid. TextIO handles a non-Int input.
            // Returns null if the value input is 0.
            try {
                fileNumber = TextIO.getInt();
                if (fileNumber == 0) { // Setting the break point for the while true loop program.
                    return null;
                }
                filename = pathNames[fileNumber - 1]; // Extract the file name part in the path name string.
                break;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("That is outside of the range of available data files to analyse.");
                System.out.println("Please choose another one");
            }
        }
        return filename;
    } // End getFilename

    /**
     * Return the path for an analysed rainfall data file
     */
    private static String getSavePath(String filename) {
        String[] filenameElements = filename.trim().split("\\."); // Segment the file path name as array elements.
        // Use the file name part in creating a new file name.
        return "src/main/resources/rainfalldata_analysed/" + filenameElements[0] + "_analysed.csv";
    }
}

