import java.io.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        File file = new File("cdr.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            Map<String,Double> users = new HashMap<>();
            Map<String,Double> tarifs = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                // reading and parsing line
                String[] tokens = line.split(", ");
                int type = Integer.parseInt(tokens[0]);
                String phone = tokens[1];
                DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date1 = format.parse(tokens[2]);
                Date date2 = format.parse(tokens[3]);
                int tariff = Integer.parseInt(tokens[4]);

                // forming header of file reports
                String path = String.format("reports/%s.txt", phone);
                File report = new File(path);
                if (report.createNewFile()) {
                    Writer writer = new FileWriter(report, true);
                    PrintWriter printWriter =  new PrintWriter(writer);
                    printWriter.println(String.format("Tariff index: %s",tariff));
                    printWriter.println("----------------------------------------------------------------------------");
                    printWriter.println(String.format("Report for phone number  %s:",phone));
                    printWriter.println("----------------------------------------------------------------------------");
                    printWriter.println("| Call Type |   Start Time        |     End Time        | Duration | Cost  |");
                    printWriter.println("----------------------------------------------------------------------------");
                    printWriter.close();
                }

                // adding phone to dict users (time in seconds) and tariffs (price)
                users.computeIfAbsent(phone, b -> GetFirstTime());
                tarifs.computeIfAbsent(phone, b -> GetTarif());

                // finding duration between start and finish call
                Duration duration = Duration.between(date1.toInstant(), date2.toInstant());

                // adding time of call to users dict
                double sum = users.get(phone); // seconds
                double local_sum = duration.toSeconds();
                if ((tariff == 11)&&(type == 2)) {
                    users.put(phone,sum);
                }
                else {
                    users.put(phone,sum+local_sum);
                }

                // counting price for each tariff
                double price = 0;
                if (tariff == 6) {
                    if (users.get(phone)/60 <= 300) {
                        price = 100;//100.00
                    }
                    else {
                        price = 100+(users.get(phone)/60.0-300)*1;
                    }
                    // System.out.println("Price "+ price);
                }
                else if (tariff == 11) {
                    if (type == 2) {
                        price  = tarifs.get(phone);
                    }
                    else if (type == 1) {
                        if (users.get(phone)/60 <= 100) {
                            price = (users.get(phone)/60.0)* 0.5;//100.00
                        }
                        else {
                            price = 100*0.5+(users.get(phone)/60.0-100)*1.5;
                        }
                    }
                    // System.out.println("Price "+price );
                }
                else if (tariff == 3) {
                    price = (users.get(phone)/60.0) * 1.5;
                    // System.out.println("Price "+price );
                }

                // adding new final price of call to tariffs dict
                tarifs.put(phone,price);

                // adding line to report
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date1_f = formatter.format(date1);
                String date2_f = formatter.format(date2);
                Writer writer = new FileWriter(report, true);
                PrintWriter printWriter =  new PrintWriter(writer);
                printWriter.println(String.format("|     0%d    | %s | %s | %d:%02d:%02d |  %.2f |",
                        type,date1_f,date2_f,duration.toSeconds()/ 3600, (duration.toSeconds() % 3600) / 60,
                        (duration.toSeconds() % 60),price));
                printWriter.close();
            }
            // adding footer with final price
            for (String key : users.keySet()) {
                String path = String.format("reports/%s.txt", key);
                File report = new File(path);
                Writer writer = new FileWriter(report, true);
                PrintWriter printWriter =  new PrintWriter(writer);
                printWriter.println("----------------------------------------------------------------------------");
                printWriter.println(String.format("|                                           Total Cost: |     %.2f rubles |",
                        tarifs.get(key)));
                printWriter.println("----------------------------------------------------------------------------");
                printWriter.close();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static Double GetFirstTime() {
        return 0.00;
    }

    public static Double GetTarif() {
        return 0.00;
    }
}