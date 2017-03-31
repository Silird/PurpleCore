package ru.Silird.PurpleCore;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigurationFactory {
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationFactory.class);
    private ApplicationContext context = new FileSystemXmlApplicationContext(//System.getProperty("user.dir") +
            "/PurpleCoreConfig.xml");

    private static ConfigurationFactory instance;

    public static ConfigurationFactory getInstance() {
        if (instance == null) {
            instance = new ConfigurationFactory();
        }
        return instance;
    }

    public Configuration getConfiguration() {
        return context.getBean("configuration", Configuration.class);
    }

    public static void generateContext() {
        try {
            char separator;
            if (SystemUtils.IS_OS_LINUX) {
                separator = '/';
            } else {
                separator = '\\';
            }
            File file = new File(System.getProperty("user.dir") + separator + "PurpleCoreConfig.xml");
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent.mkdirs()) {
                    if (!file.createNewFile()) {
                        logger.warn("Файл конфигурации не был создан");
                    }
                }

                try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), StandardCharsets.UTF_8))) {
                    //Записываем текст в файл
                    out.print("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
                            "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                            "       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\">\n" +
                            "\n" +
                            "    <bean id=\"configuration\" class=\"ru.Silird.PurpleCore.Configuration\">\n" +
                            "        <property name=\"cores\" >\n" +
                            "            <list>\n" +
                            "                <value>Launcher.jar</value>\n" +
                            "                <value>LauncherAuthlib.jar</value>\n" +
                            "                <value>KCauldron.jar</value>\n" +
                            "            </list>\n" +
                            "        </property>\n" +
                            "        <property name=\"javaXmx\" value=\"10000\"/>\n" +
                            "        <property name=\"javaXms\" value=\"256\"/>\n" +
                            "    </bean>\n" +
                            "\n" +
                            "</beans>");
                }

                logger.info("Файл конфигурации rabbit успешно создан!");
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
