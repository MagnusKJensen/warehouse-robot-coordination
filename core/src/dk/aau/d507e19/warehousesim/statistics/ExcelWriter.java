package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExcelWriter {
    private static final String[] generalStatsHeader = {"", "Measurement"};
    private static final String[] orderStatsHeader = {"ID", "Start_time_in_MS", "Finish_time_in_MS", "Process_time_in_MS"};
    private static final String[] robotStatsHeader = {"ID", "Deliveries_Completed", "Distance_traveled_in_meters", "IdleTimeInSeconds"};
    private static final String[] robotSummaryHeader = {"", "Measurement", "RobotID"};
    private static final String[] orderSummaryHeader =  {"", "Measurement", "OrderID", "Products"};
    private static String PATH_TO_SIM_FOLDER;
    private static Simulation simulation;
    private DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
    private static final String GENERAL_STATS_FILENAME = "generalStats";
    private static final String ORDER_STATS_FILENAME = "orderStats";
    private static final String ROBOT_STATS_FILENAME = "robotStats";

    public ExcelWriter(Simulation simulation, String path) {
        ExcelWriter.simulation = simulation;
        // Apply patterns to the decimal formatter, that is used in the statistics files
        decimalFormatter.applyPattern("###.00");
        decimalFormatter.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatter.setGroupingUsed(false);

        PATH_TO_SIM_FOLDER = path;
    }

    public void writeRobotStats(){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + ROBOT_STATS_FILENAME;
        Workbook workbook = getOrCreateWorkbook(pathToRobotStats);

        // Create a Sheet
        String sheetName ="Tick " + simulation.getTimeInTicks();
        Sheet sheet = getOrCreateSheet(workbook, sheetName);

        createHeaders(workbook, sheet, generalStatsHeader);

        // Write robot stats
        ArrayList<Robot> robots = simulation.getAllRobots();
        int rowNum = 1;
        Row row;
        for(Robot robot : robots){
            row = sheet.createRow(rowNum++);
            createRobotRow(robot, row);
        }

        resizeAllColumnSizes(robotStatsHeader, sheet);

        String pathToFile = PATH_TO_SIM_FOLDER + ROBOT_STATS_FILENAME + ".xlsx";
        saveWorkbook(workbook, pathToFile);
    }

    public void writeOrderStats(){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + ORDER_STATS_FILENAME;
        Workbook workbook = getOrCreateWorkbook(pathToRobotStats);

        // Create a Sheet
        String sheetName = "Tick " + simulation.getTimeInTicks();
        Sheet sheet = getOrCreateSheet(workbook, sheetName);

        createHeaders(workbook, sheet, orderStatsHeader);

        // Write order stats
        ArrayList<Order> orders = simulation.getServer().getOrderManager().getOrdersFinished();
        int rowNum = 1;
        Row row;
        for(Order order : orders){
            row = sheet.createRow(rowNum++);
            createOrderRow(order, row);
        }

        resizeAllColumnSizes(orderStatsHeader, sheet);

        String pathToFile = PATH_TO_SIM_FOLDER + ORDER_STATS_FILENAME + ".xlsx";
        saveWorkbook(workbook, pathToFile);
    }

    private void writeGeneralStatsToSheets(String sheetName){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + GENERAL_STATS_FILENAME;
        Workbook workbook = getOrCreateWorkbook(pathToRobotStats);

        // Create a Sheet
        Sheet sheet = getOrCreateSheet(workbook, sheetName);

        createHeaders(workbook, sheet, generalStatsHeader);

        // Create Other rows and cells with rest of the data data
        int rowNum = 1;
        Row row = sheet.createRow(rowNum++);
        createGeneralRow("CurrentTick", simulation.getTimeInTicks(), row);

        row = sheet.createRow(rowNum++);
        createGeneralRow("availableProductsLeft", simulation.getServer().getProductsAvailable().size(), row);

        row = sheet.createRow(rowNum++);
        createGeneralRow("ordersInQueue", simulation.getServer().getOrderManager().ordersInQueue(), row);

        row = sheet.createRow(rowNum++);
        createGeneralRow("ordersFinished", simulation.getServer().getOrderManager().ordersFinished(), row);

        row = sheet.createRow(rowNum++);
        double ordersPerMinute;
        if(simulation.getTimeInTicks() == 0) ordersPerMinute = 0;
        else {
            long msSinceStart = simulation.getSimulatedTimeInMS();
            ordersPerMinute = simulation.getOrdersProcessed() / ((double) msSinceStart / 1000 / 60);
        }
        createGeneralRow("OrderPerMinute", ordersPerMinute, row);

        row = sheet.createRow(rowNum++);
        createGeneralRow("TasksInQueue", simulation.getServer().getOrderManager().tasksInQueue(), row);

        row = sheet.createRow(rowNum++);
        createGeneralRow("OrderGoal", simulation.getGoal().getStatsAsCSV(), row);

        resizeAllColumnSizes(generalStatsHeader, sheet);

        String pathToFile = PATH_TO_SIM_FOLDER + GENERAL_STATS_FILENAME + ".xlsx";
        saveWorkbook(workbook, pathToFile);
    }

    public void writeGeneralStats(){
        writeGeneralStatsToSheets("Tick " + simulation.getTimeInTicks());
    }

    public void summarizeOrderStats(){
        String pathToOrderStats = PATH_TO_SIM_FOLDER + ORDER_STATS_FILENAME;
        Workbook workbook = getOrCreateWorkbook(pathToOrderStats);

        Sheet sheet = getOrCreateSheet(workbook, "Summary");

        createHeaders(workbook, sheet, orderSummaryHeader);

        // Create Other rows and cells with rest of the data data
        int rowNum = 1;
        // Add quickest order
        Row row = sheet.createRow(rowNum++);
        Order quickestOrder = simulation.getStatisticsManager().getQuickestOrder();
        if(quickestOrder != null){
            createOrderSummaryRow("Quickest order",
                    quickestOrder.getTimeSpentOnOrderInSec(),
                    quickestOrder.getOrderID(),
                    quickestOrder.getAllProductsInOrder(),
                    row);
        } else row.createCell(0).setCellValue("Quickest order");

        // Add slowest order
        row = sheet.createRow(rowNum++);
        Order slowestOrder = simulation.getStatisticsManager().getSlowestOrder();
        if(slowestOrder != null){
            createOrderSummaryRow("Slowest order",
                    slowestOrder.getTimeSpentOnOrderInSec(),
                    slowestOrder.getOrderID(),
                    slowestOrder.getAllProductsInOrder(),
                    row);
        } else row.createCell(0).setCellValue("Slowest order");

        // Add average order
        row = sheet.createRow(rowNum++);
        double averageOrderTime = simulation.getStatisticsManager().getAverageOrderProcessingTime();
        createOrderSummaryRow("Average order time",
                averageOrderTime,
                "",
                new ArrayList<Product>(),
                row);


        resizeAllColumnSizes(orderSummaryHeader, sheet);
        saveWorkbook(workbook, pathToOrderStats + ".xlsx");
    }

    public void summarizeGeneralStats(){
        writeGeneralStatsToSheets("Summary");
    }

    public void summarizeRobotStats(){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + ROBOT_STATS_FILENAME;
        Workbook workbook = getOrCreateWorkbook(pathToRobotStats);

        Sheet sheet = getOrCreateSheet(workbook, "Summary");

        createHeaders(workbook, sheet, robotSummaryHeader);

        // Create Other rows and cells with rest of the data data
        int rowNum = 1;
        // Add average distance traveled
        Row row = sheet.createRow(rowNum++);
        int averageDistanceTraveled = simulation.getStatisticsManager().averageDistanceTraveled();
        createRobotSummaryRow("Average Distance traveled", averageDistanceTraveled, "", row);

        // Add shortest distance robot
        row = sheet.createRow(rowNum++);
        Robot shortestDistanceRobot = simulation.getStatisticsManager().getRobotWithShortestDistance();
        createRobotSummaryRow("Shortest distance",
                (int)shortestDistanceRobot.getDistanceTraveledInMeters(),
                shortestDistanceRobot.getRobotID() + "",
                row);

        // Add longest distance robot
        row = sheet.createRow(rowNum++);
        Robot longestDistanceRobot = simulation.getStatisticsManager().getRobotWithLongestDistance();
        createRobotSummaryRow("Longest distance",
                (int)longestDistanceRobot.getDistanceTraveledInMeters(),
                longestDistanceRobot.getRobotID() + "",
                row);

        // Add shortest idle time
        row = sheet.createRow(rowNum++);
        Robot leastIdleRobot = simulation.getStatisticsManager().getRobotWithLeastIdleTime();
        createRobotSummaryRow("Least idle",
                leastIdleRobot.getIdleTimeInSeconds(),
                leastIdleRobot.getRobotID() + "",
                row);

        // Add longest idle time
        row = sheet.createRow(rowNum++);
        Robot longestIdleTime = simulation.getStatisticsManager().getRobotWithLongestIdleTime();
        createRobotSummaryRow("Most idle",
                longestIdleTime.getIdleTimeInSeconds(),
                longestIdleTime.getRobotID() + "",
                row);

        // Add average idle time
        row = sheet.createRow(rowNum++);
        double averageIdleTime = simulation.getStatisticsManager().getRobotAverageIdleTime();
        createRobotSummaryRow("Average idle time",
                averageIdleTime,
                "",
                row);

        // Add fewest deliveries
        row = sheet.createRow(rowNum++);
        Robot fewestDeliveriesRobot = simulation.getStatisticsManager().getRobotFewestDeliveries();
        createRobotSummaryRow("Fewest deliveries",
                fewestDeliveriesRobot.getBinDeliveriesCompleted(),
                fewestDeliveriesRobot.getRobotID() + "",
                row);

        // Add most deliveries
        row = sheet.createRow(rowNum++);
        Robot mostDeliveriesRobot = simulation.getStatisticsManager().getRobotMostDeliveries();
        createRobotSummaryRow("Most deliveries",
                mostDeliveriesRobot.getBinDeliveriesCompleted(),
                mostDeliveriesRobot.getRobotID() + "",
                row);

        // Add most deliveries
        row = sheet.createRow(rowNum++);
        int averageDeliveries = simulation.getStatisticsManager().getRobotAverageDeliveries();
        createRobotSummaryRow("Average deliveries",
                averageDeliveries,
                "",
                row);

        resizeAllColumnSizes(robotSummaryHeader, sheet);
        saveWorkbook(workbook, pathToRobotStats + ".xlsx");
    }

    private void createHeaders(Workbook workbook, Sheet sheet, String[] header) {
        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerCellStyle);
        }
    }

    private void createRobotSummaryRow(String name, int measurement, String robotID, Row row){
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(measurement);
        row.createCell(2).setCellValue(robotID);
    }

    private void createRobotSummaryRow(String name, double measurement, String robotID, Row row){
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(measurement);
        row.createCell(2).setCellValue(robotID);
    }

    private Sheet getOrCreateSheet(Workbook workbook, String sheetName){
        Sheet sheet;
        if(workbook.getSheet(sheetName) != null) sheet = workbook.getSheet(sheetName);
        else sheet = workbook.createSheet(sheetName);
        return sheet;
    }

    private void resizeAllColumnSizes(String[] header, Sheet sheet){
        // Resize all columns to fit the content size
        for(int i = 0; i < header.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private Workbook getOrCreateWorkbook(String pathToWorkBook) {
        Workbook workbook;
        if(!new File(pathToWorkBook + ".xlsx").exists()){
            workbook = createExcelDocument(pathToWorkBook);
        } else workbook = openWorkBook(pathToWorkBook);

        return workbook;
    }

    private void saveWorkbook(Workbook workbook, String path) {
        // Write the output to a file
        FileOutputStream fileOut = null;
        try {

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Workbook openWorkBook(String pathToWorkBook) {
        InputStream is = null;
        try {
            is = new FileInputStream(pathToWorkBook + ".xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            return workbook;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Workbook createExcelDocument(String path) {
        Workbook workbook = new XSSFWorkbook();

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            String pathToFile = path + ".xlsx";
            fileOut = new FileOutputStream(pathToFile);
            workbook.write(fileOut);
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbook;
    }

    private void createRobotRow(Robot robot, Row row) {
        // Should fit header: {"ID", "Deliveries_Completed", "Distance_traveled_in_meters", "IdleTimeInSeconds"}
        row.createCell(0).setCellValue(robot.getRobotID());
        row.createCell(1).setCellValue(robot.getBinDeliveriesCompleted());
        row.createCell(2).setCellValue(robot.getDistanceTraveledInMeters());
        row.createCell(3).setCellValue(robot.getIdleTimeInSeconds());
    }

    private void createGeneralRow(String name, String measurement, Row row) {
        row.createCell(0).setCellValue(name);

        row.createCell(1).setCellValue(measurement);

    }

    private void createGeneralRow(String name, long measurement, Row row) {
        row.createCell(0).setCellValue(name);

        row.createCell(1).setCellValue(measurement);
    }

    private void createGeneralRow(String name, double measurement, Row row) {
        row.createCell(0).setCellValue(name);

        row.createCell(1).setCellValue(measurement);
    }

    private void createOrderRow(Order order, Row row) {
        // Should fit header: {"ID,", "Start_time_in_MS,", "Finish_time_in_MS,", "Process_time_in_MS"}
        row.createCell(0).setCellValue(order.getOrderID());
        row.createCell(1).setCellValue(order.getStartTimeInMS());
        row.createCell(2).setCellValue(order.getFinishTimeInMS());
        row.createCell(3).setCellValue(order.getTimeSpentOnOrderInMS());
    }

    private void createOrderSummaryRow(String name, double measurement, String orderID, ArrayList<Product> products, Row row){
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(measurement);
        row.createCell(2).setCellValue(orderID);
        if(products.isEmpty()) row.createCell(3).setCellValue("");
        else row.createCell(3).setCellValue(products.toString());
    }
}

