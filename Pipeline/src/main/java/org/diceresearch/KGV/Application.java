package org.diceresearch.KGV;

import org.diceresearch.KGV.Pipeline.Pipeline.RunCopaalPipeline;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if (args == null || args.length == 0) {
            System.out.println("no arguments ! use h to get help");
            return;
        }

        if (args.length ==  1 && args[0].equals("h")){
            System.out.println("this is help");
            System.out.println("1 . use 'copaal' for check the veracity of file with COPAAL ");
            System.out.println("\t \t copaal [fileToProcess] [locationForSaveTheResults ] [fileNameConst] [copaalEndPoint] [acceptablePredicates]");
        }

        if (args.length == 6 && args[0].equals("copaal")){
            String fileToProcess  = args[1];
            String locationForSaveTheResults = args[2];
            String fileNameConst = args[3];
            String copaalEndPoint = args[4];
            String acceptablePredicates = args[5];
            RunCopaalPipeline p = new RunCopaalPipeline(fileToProcess, locationForSaveTheResults, fileNameConst, copaalEndPoint, acceptablePredicates);
            p.run();
        }
        System.out.println("Done !");
    }
}

