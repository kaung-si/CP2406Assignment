package rainfallanalyser.cp2406assignment_alpha;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * CP2406 Assignment - Kaung Sithu
 * Rainfall Visualiser: Alpha Version
 * Based off of the supplied starter code for the assignment.
 * The drawPicture() class was the only code that was changed for this version of the project.
 * Requires the user to specify the file path for the analysed rainfall data.
 *
 * See https://github.com/kaung-si/CP2406Assignment for the project repository.
 */
public class RainfallVisualiser extends Application {

    /**
     * Draws a picture. The parameters width and height give the size
     * of the drawing area, in pixels.
     */
    public void drawPicture(GraphicsContext g, int width, int height) {

        // Create the x-axis and y-axis
        int border_width = 20;
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeLine(border_width, border_width, border_width, height - border_width);
        g.strokeLine(border_width, height - border_width, width - border_width, height - border_width);
        TextIO.getln(); // Remove the first line

        ArrayList<Double> allMonthlyTotals = new ArrayList<>(); // Create array type variable to store monthly rainfall.

        double maxMonthlyTotal = Double.NEGATIVE_INFINITY;
        int firstYear = 2050;
        int lastYear = 0;
        // Read the file first and create an array of the monthly totals.
        while (!TextIO.eof()) {
            String[] line = TextIO.getln().trim().strip().split(","); // Split by column-wise and store column values that belongs to one row.
            double monthlyTotal = Double.parseDouble(line[2]); // Name the index number 2 of line array.
            allMonthlyTotals.add(monthlyTotal); // Assign total monthly rainfall to index 2 of line array.
            if (monthlyTotal > maxMonthlyTotal)
                maxMonthlyTotal = monthlyTotal;

            int year = Integer.parseInt(line[0]);
            if (year < firstYear)
                firstYear = year;
            else if (year > lastYear)
                lastYear = year;
        }

        double xAxisLength = width - 2 * border_width;
        double yAxisLength = height - 2 * border_width;
        double currentXPos = border_width;
        // Adjust the bar width depending on the the number of monthly rainfall.
        double barWidth = xAxisLength / allMonthlyTotals.size();

        g.setFill(Color.LIGHTGREEN);
        g.setLineWidth(0.5);

        for (Double monthlyTotal : allMonthlyTotals) {
            // Get the height of the column relative to the maximum height
            double columnHeight = (monthlyTotal / maxMonthlyTotal) * yAxisLength;

            // Draw the rectangle representing the rainfall and draw a black outline
            g.fillRect(currentXPos, height - border_width - columnHeight, barWidth, columnHeight);
            g.strokeRect(currentXPos, height - border_width - columnHeight, barWidth, columnHeight);

            currentXPos += barWidth; // Calculate the x-coordinate for drawing the next bar on the graph.
        }

        // Add a title and axis names.
        g.setFill(Color.BLACK);
        g.setFont(Font.font(24));
        g.fillText("Rainfall: " + firstYear + " to " + lastYear, width/2.5, border_width);

        g.setFont(Font.font(15));
        g.fillText("Months", width/2.0, height-5);

        g.rotate(-90);
        g.fillText("Rainfall (millimeters)",-height/1.6, border_width-5);
    } // End drawPicture().

    /**
     * Create a blank canvas, draw a bar graph on it and set title, border, border color for the canvas.*/
    //------ Implementation details: DO NOT EDIT THIS ------
    public void start(Stage stage) {
        int width = 200 * 6 + 40;
        int height = 500;
        Canvas canvas = new Canvas(width, height); // create canvas.
        drawPicture(canvas.getGraphicsContext2D(), width, height); // draw the graph.
        BorderPane root = new BorderPane(canvas);
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444"); // specify border width and color.
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rainfall Visualiser"); // set title of the program.
        stage.show();
        stage.setResizable(false);
    }
    //------ End of Implementation details ------


    public static void main(String[] args) {
        System.out.print("Enter path: ");
        var path = TextIO.getln();

        // Used for testing
        // var path = "src/main/resources/rainfalldata_analysed/MountSheridanStationCNS_analysed.csv";
        // var path = "src/main/resources/rainfalldata_analysed/IDCJAC0009_031205_1800_Data_analysed.csv";
        TextIO.readFile(path);
        launch();
    }

} // end RainfallVisualiser
