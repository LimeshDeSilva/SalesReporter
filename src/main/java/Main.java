import io.CsvSalesReader;
import model.ProductSale;
import output.ConsoleOutput;
import output.FileOutput;
import output.OutputStrategy;
import report.SalesReportGenerator;
import service.SalesReportService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        if(args.length < 2) {
            System.err.println("Error: Missing required command-line arguments.");
            printUsage();
            System.exit(1);
        }
        String csvFilePath = args[0];
        String outputMethod = args[1].toLowerCase();
        String outputFilePath = args.length > 2 ? args[2] : null;

        if ("file".equalsIgnoreCase(outputMethod) && outputFilePath == null) {
            System.err.println("Error: Output file path is required when output-method is 'file'.");
            printUsage();
            System.exit(1);
        } else if (!"console".equalsIgnoreCase(outputMethod) && !"file".equalsIgnoreCase(outputMethod)) {
            System.err.println("Error: Invalid output method '" + args[1] + "'. Must be 'console' or 'file'.");
            printUsage();
            System.exit(1);
        }

        try {
            CsvSalesReader reader = new CsvSalesReader();
            List<ProductSale> sales = reader.read(csvFilePath);
            if (sales.isEmpty()) {
                System.err.println("Error: CSV file contains no data.");
                return;
            }
            SalesReportService service = new SalesReportService();
            SalesReportGenerator generator = new SalesReportGenerator(service);
            String report = generator.generate(sales);
            OutputStrategy output;

            if ("console".equalsIgnoreCase(outputMethod)) {
                output = new ConsoleOutput();
            } else {
                output = new FileOutput(outputFilePath);
            }

            output.writeReport(report);
            if ("file".equalsIgnoreCase(outputMethod)) {
                System.out.println("Report successfully saved to " + outputFilePath);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: CSV file not found at path: " + csvFilePath);
        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: CSV contains invalid numeric format. " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("\nUsage:");
        System.out.println("  java SalesReporter <csv-file-path> <output-method> [output-file-path]");
        System.out.println("Examples:");
        System.out.println("  java SalesReporter sales.csv console");
        System.out.println("  java SalesReporter sales.csv file output_report.txt");
    }
}
