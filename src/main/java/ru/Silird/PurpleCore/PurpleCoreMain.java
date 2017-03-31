package ru.Silird.PurpleCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PurpleCoreMain {
    private static final Logger logger = LoggerFactory.getLogger(PurpleCoreMain.class);
    public static void main(String[] args) {
        logger.info("Start cores...");

        try {

            Configuration configuration = null;

            try {
                configuration = ConfigurationFactory.getInstance().getConfiguration();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                ConfigurationFactory.generateContext();
                System.exit(1);
            }

            System.out.println("java " +
                    "-Dfile.encoding=UTF-8 " +
                    "-Xms" + configuration.getJavaXms() + "m " +
                    "-Xmx" + configuration.getJavaXmx() + "m " +
                    "-cp " + getCores(configuration.getCores()) + " " +
                    "cpw.mods.fml.relauncher.ServerLaunchWrapper");

            Process p = Runtime.getRuntime().exec("java " +
                    "-Dfile.encoding=UTF-8 " +
                    "-Xms" + configuration.getJavaXms() + "m " +
                    "-Xmx" + configuration.getJavaXmx() + "m " +
                    "-cp " + getCores(configuration.getCores()) + " " +
                    "cpw.mods.fml.relauncher.ServerLaunchWrapper");

            CompletableFuture futureIn = CompletableFuture.runAsync(() -> {
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                try {
                    String line = in.readLine();
                    while (line != null) {
                        System.out.println(line);
                        try {
                            line = in.readLine();
                        }
                        catch (IOException ex) {
                            logger.error("Failed to read from input stream: " + ex.getCause() + ": " + ex.getMessage() + "\n" +
                                    Arrays.toString(ex.getStackTrace()));
                        }
                    }
                    in.close();
                }
                catch (IOException ex) {
                    logger.error("Failed in inputStream: " + ex.getCause() + ": " + ex.getMessage() + "\n" +
                            Arrays.toString(ex.getStackTrace()));
                    System.exit(1);
                }
            });


            CompletableFuture futureErr = CompletableFuture.runAsync(() -> {
                BufferedReader err = new BufferedReader(new InputStreamReader(p.getInputStream()));
                try {
                    String line = err.readLine();
                    while (line != null) {
                        System.out.println(line);
                        try {
                            line = err.readLine();
                        }
                        catch (IOException ex) {
                            logger.error("Failed to read from error stream: " + ex.getCause() + ": " + ex.getMessage() + "\n" +
                                    Arrays.toString(ex.getStackTrace()));
                        }
                    }
                    err.close();
                }
                catch (IOException ex) {
                    logger.error("Failed in errorStream: " + ex.getCause() + ": " + ex.getMessage() + "\n" +
                            Arrays.toString(ex.getStackTrace()));
                    System.exit(1);
                }
            });

            CompletableFuture futureOut = CompletableFuture.runAsync(() -> {

                Scanner scanner = new Scanner(System.in);
                //logger.warn(scanner.nextLine());
                OutputStream out = p.getOutputStream();
                while (true) {
                    String message;
                    try {
                        String scan = scanner.nextLine();
                        message = scan + "\n";
                        out.write(message.getBytes());
                        out.flush();

                        if (scan.equals("stop")) {
                            break;
                        }
                    }
                    catch (IOException ex) {
                        logger.error("Failed to write in outPutStream: " + ex.getCause() + ": " + ex.getMessage() + "\n" +
                                Arrays.toString(ex.getStackTrace()));
                        System.exit(1);
                    }
                }
            });

            CompletableFuture.allOf(futureIn, futureErr, futureOut).get();

        }
        catch (IOException ex) {
            logger.error("Failed to start cores: " + ex.getCause() + ": " + ex.getMessage() + "\n" +
                    Arrays.toString(ex.getStackTrace()));
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static String getCores(List<String> cores) throws IOException {
        /*
        // Открытия файла
        File file = new File("PurpleCore.conf");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                logger.warn("Файл конфигурации не был создан");
            }
            throw new FileNotFoundException(file.getName());
        }

        String result = "";

        //Объект для чтения файла в буфер
        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String line = in.readLine();
            result += line;
            while((line = in.readLine()) != null) {
                result += ":" + line;
            }
        }
        if (result.equals("")) {
            return null;
        }

        return result;
        */
        String result = "";

        boolean first = true;
        for (String core : cores){
            if (!first) {
                result += ":";
            }
            else {
                first = false;
            }
            result += core;
        }

        return result;
    }
}
