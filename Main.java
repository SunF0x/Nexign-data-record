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
            int i = 0;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            Map<String,Double> users = new HashMap<String,Double>();
            Map<String,Double> tarifs = new HashMap<String,Double>();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
//                i++;
//                System.out.println(i);
                String[] tokens = line.split(", ");
                int type = Integer.parseInt(tokens[0]);
                String phone = tokens[1];
                String path = String.format("C://Users/katya/Downloads/for_work/Nexign-data-record/reports/%s.txt", tokens[1]);
                File report = new File(path);
                if (report.createNewFile()) {
                    //сюда пишем заголовок файла сразу
                    Writer writer = new FileWriter(report, true);
                    PrintWriter printWriter =  new PrintWriter(writer);
                    printWriter.println(String.format("Tariff index: %s",tokens[4]));
                    printWriter.println("----------------------------------------------------------------------------");
                    printWriter.println(String.format("Report for phone number  %s:",phone));
                    printWriter.println("----------------------------------------------------------------------------");
                    printWriter.println("| Call Type |   Start Time        |     End Time        | Duration | Cost  |");
                    printWriter.println("----------------------------------------------------------------------------");
                    printWriter.close();
                }
//                System.out.println(type);
//                System.out.println(phone);
                DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date1 = format.parse(tokens[2]);
                // System.out.println(date1); // Sat Jan 02 00:00:00 GMT 2010
                Date date2 = format.parse(tokens[3]);
                // System.out.println(date2);
                int tarif = Integer.parseInt(tokens[4]);
                Duration duration = Duration.between(date1.toInstant(), date2.toInstant());
                System.out.println(duration.toSeconds());
//              System.out.println(duration.toMinutes());
//              double sum = 0;
                users.computeIfAbsent(phone, b -> GetFirstTime());
                tarifs.computeIfAbsent(phone, b -> GetTarif());
                double sum = users.get(phone);//minutes
                double local_sum = duration.toSeconds();
                if ((tarif == 11)&&(type == 2)) {
                    users.put(phone,sum);
                }
                else {
                    users.put(phone,sum+local_sum);}
                double price = 0;
                if (tarif == 6) {
                    if (users.get(phone)/60 <= 300) {
                        price = 100;//100.00
                    }
                    else {
                        price = 100+(users.get(phone)/60.0-300)*1;
                    }
                    System.out.println("Price "+ price);
                }
                else if (tarif == 11) {
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
//                        price  = (duration.toSeconds()/60.0)* 0.5;// * 0.5
                        //System.out.println("Price "+price );
                    }
                    System.out.println("Price "+price );
                }
                else if (tarif == 3) {
                    price = (users.get(phone)/60.0) * 1.5;
                    System.out.println("Price "+price );
                }
                //double last_price = tarifs.get(phone);//price before
                tarifs.put(phone,price);
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date1_f = formatter.format(date1);
                String date2_f = formatter.format(date2);
                Writer writer = new FileWriter(report, true);
                PrintWriter printWriter =  new PrintWriter(writer);
                printWriter.println(String.format("|     0%d    | %s | %s | %d:%02d:%02d |  %.2f |",type,date1_f,date2_f,duration.toSeconds()/ 3600, (duration.toSeconds() % 3600) / 60, (duration.toSeconds() % 60),price));
                printWriter.close();
                //System.out.println("-----\n"+users.size()+"\n"+users.toString());
            }
        for (String key : users.keySet()) {
                // use the key her
            String path = String.format("C://Users/katya/Downloads/for_work/Nexign-data-record/reports/%s.txt", key);
            File report = new File(path);
            Writer writer = new FileWriter(report, true);
            PrintWriter printWriter =  new PrintWriter(writer);
            printWriter.println("----------------------------------------------------------------------------");
            printWriter.println(String.format("|                                           Total Cost: |     %.2f rubles |",tarifs.get(key)));
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