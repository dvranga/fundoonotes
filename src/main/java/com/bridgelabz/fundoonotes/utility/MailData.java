package com.bridgelabz.fundoonotes.utility;


import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Service
public class MailData {

    private String bookingTime = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    private String Header = "\t\t\t\t\t\t\t\t\tORDER CONFIRMATION\n\n";
    private String shopName = "\t\t\t\t\t\t\t\t\t\tBookStore Private Limited.\n\n";
    private String shopAdd = "N0 42,\n15th Cross,&14th Main Road\nHsr Layout Opposite to BDA Complex,\nKarnataka 560102\n\n";
    private String sincere = "Sincerely,\nBookstore Private Limited\nadmin@booksStore,in\n";
    private String content= "Thank you again for your order.\n\n"+"We are received your order  and will contact you as soon as your package is shipped\n";
    private String acknowledge="We acknowledge the receipt of your purchase order ";


    public String  getOrderMail(Long orderId, double totalPrice) {
        return Header + bookingTime + "\n\n" + shopAdd + "Dear  " + ",\n\n" +
                "Order Number : "+orderId+"\n"+"Total Book Price : Rs."+totalPrice+"\n\n"+acknowledge + content + sincere;
    }
}
