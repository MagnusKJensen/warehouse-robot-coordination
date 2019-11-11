package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExcelWriter {
    private static String[] generalStatsHeader = {"", "Measurement"};
    private static String[] orderStatsHeader = {"ID", "Start_time_in_MS", "Finish_time_in_MS", "Process_time_in_MS"};
    private static String[] robotStatsHeader = {"ID", "Deliveries_Completed", "Distance_traveled_in_meters", "IdleTimeInSeconds"};
    private static String PATH_TO_SIM_FOLDER;
    private static Simulation simulation;
    private DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
    private static final String GENERAL_STATS_FILENAME = "generalStats";
    private static final String ORDER_STATS_FILENAME = "orderStats";
    private static final String ROBOT_STATS_FILENAME = "robotStats";

    public ExcelWriter(Simulation simulation, String pathToSimFolder) {
        ExcelWriter.simulation = simulation;
        // Apply patterns to the decimal formatter, that is used in the statistics files
        decimalFormatter.applyPattern("###.00");
        decimalFormatter.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatter.setGroupingUsed(false);

        PATH_TO_SIM_FOLDER = pathToSimFolder;
    }

    public void writeRobotStats(){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + ROBOT_STATS_FILENAME;
        Workbook workbook = fetchWorkbook(pathToRobotStats);

        // Create a Sheet
        Sheet sheet;
        String sheetName ="Tick " + simulation.getTimeInTicks();
        if(workbook.getSheet(sheetName) != null) sheet = workbook.getSheet(sheetName);
        else sheet = workbook.createSheet(sheetName);

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
        for(int i = 0; i < robotStatsHeader.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(robotStatsHeader[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Write robot stats
        ArrayList<Robot> robots = simulation.getAllRobots();
        int rowNum = 1;
        Row row;
        for(Robot robot : robots){
            row = sheet.createRow(rowNum++);
            createRobotRow(robot, row);
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < robotStatsHeader.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String pathToFile = PATH_TO_SIM_FOLDER + ROBOT_STATS_FILENAME + ".xlsx";
        saveWorkbook(workbook, pathToFile);
    }

    private Workbook fetchWorkbook(String pathToWorkBook) {
        Workbook workbook;
        if(!new File(pathToWorkBook + ".xlsx").exists()){
            workbook = createExcelDocument(pathToWorkBook);
        } else workbook = openWorkBook(pathToWorkBook);

        return workbook;
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

    public void writeOrderStats(){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + ORDER_STATS_FILENAME;
        Workbook workbook = fetchWorkbook(pathToRobotStats);

        // Create a Sheet
        Sheet sheet;
        String sheetName = "Tick " + simulation.getTimeInTicks();
        if(workbook.getSheet(sheetName) != null) sheet = workbook.getSheet(sheetName);
        else sheet = workbook.createSheet(sheetName);

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
        for(int i = 0; i < orderStatsHeader.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(orderStatsHeader[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Write order stats
        ArrayList<Order> orders = simulation.getServer().getOrderManager().getOrdersFinished();
        int rowNum = 1;
        Row row;
        for(Order order : orders){
            row = sheet.createRow(rowNum++);
            createOrderRow(order, row);
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < orderStatsHeader.length; i++) {
            sheet.autoSizeColumn(i);
        }


        String pathToFile = PATH_TO_SIM_FOLDER + ORDER_STATS_FILENAME + ".xlsx";
        saveWorkbook(workbook, pathToFile);
    }

    private void createOrderRow(Order order, Row row) {
        // Should fit header: {"ID,", "Start_time_in_MS,", "Finish_time_in_MS,", "Process_time_in_MS"}
        row.createCell(0).setCellValue(order.getOrderID());
        row.createCell(1).setCellValue(order.getStartTimeInMS());
        row.createCell(2).setCellValue(order.getFinishTimeInMS());
        row.createCell(3).setCellValue(order.getTimeSpentOnOrder());
    }

    public void writeGeneralStats(){
        String pathToRobotStats = PATH_TO_SIM_FOLDER + GENERAL_STATS_FILENAME;
        Workbook workbook = fetchWorkbook(pathToRobotStats);

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet;
        String sheetName = "Tick " + simulation.getTimeInTicks();
        if(workbook.getSheet(sheetName) != null) sheet = workbook.getSheet(sheetName);
        else sheet = workbook.createSheet(sheetName);

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
        for(int i = 0; i < generalStatsHeader.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(generalStatsHeader[i]);
            cell.setCellStyle(headerCellStyle);
        }

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

        // Resize all columns to fit the content size
        for(int i = 0; i < generalStatsHeader.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String pathToFile = PATH_TO_SIM_FOLDER + GENERAL_STATS_FILENAME + ".xlsx";
        saveWorkbook(workbook, pathToFile);
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

    private void createGeneralRow(String name, String measurement, Row row) {
        row.createCell(0)
                .setCellValue(name);

        row.createCell(1)
                .setCellValue(measurement);

    }

    private void createGeneralRow(String name, long measurement, Row row) {
        row.createCell(0)
                .setCellValue(name);

        row.createCell(1)
                .setCellValue(measurement);
    }

    private void createGeneralRow(String name, double measurement, Row row) {
        row.createCell(0)
                .setCellValue(name);

        row.createCell(1)
                .setCellValue(measurement);
    }
}

