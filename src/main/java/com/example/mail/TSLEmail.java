package com.example.mail;

import com.example.util.Config;
import org.springframework.util.StopWatch;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TSLEmail {

    public static Session getSession() {
        System.out.println("#################### TLSEmail Start ####################");
        String propertiesFilename = "mail.properties";

        Properties props = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(propertiesFilename)) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + propertiesFilename + " resource file: " + e);
        }

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.mailUsername, Config.mailPassword);
            }
        };
        Session session = Session.getInstance(props, auth);
        return session;

//        EmailUtil.sendEmail(session, toEmail,"TLSEmail Testing Subject", "TLSEmail Testing Body");

    }

    public static void sendEmail(Session session, String subject, String fromRecipient, List<String> toRecipients, List<String> CcRecipients, List<String> BccRecipients){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            // Set
            //msg.setReplyTo(InternetAddress.parse("xx@gmail.com", false));
            msg.setFrom(new InternetAddress(fromRecipient, "NoReply-JD"));
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());

            // Set To
            for (String toEmail: toRecipients) {
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            }
            for (String CcEmail: CcRecipients) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CcEmail, false));
            }
            for (String BccEmail: BccRecipients) {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(BccEmail, false));
            }


            // Content
//            msg.setText(body, "UTF-8");

            // BodyPart - html
            BodyPart messageBodyPart = new MimeBodyPart();
            String content = Files.readString(Paths.get(Objects.requireNonNull(EmailUtil.class.getResource("/template-mail.html")).toURI()));
            System.out.println(String.format(content, "Shao Hong", "PDM"));
            messageBodyPart.setContent(String.format(content, "Shao Hong", "PDM"),"text/html");

//            Files.readString();

            // Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (Workbook wb = new Workbook(os, "DemoExcel", "1.0"); ) {
                StopWatch watch = new StopWatch();
                watch.start();

                // Worksheet1
                Worksheet ws = wb.newWorksheet("Test");

                ws.value(0, 0, "Column 1");
                ws.value(0, 1, "Column 2");
                ws.value(0, 2, "Column 3");
                ws.value(0, 3, "Column 4");
                ws.value(0, 4, "Column 5");

                for (int i = 1; i < 10; i++) {
                    String value = "data-" + i;
                    ws.value(i, 0, (String) null);
                    ws.value(i, 1, value);
                    ws.value(i, 2, "");
                    ws.value(i, 3, "\"d\"`_s@#$%^&*!#(@-[]a'\\s\"");
                    ws.value(i, 4, value);
                }

                // Worksheet2
                Worksheet ws2 = wb.newWorksheet("Test2");
                ws2.value(0, 0, "Column 1");
                ws2.value(0, 1, "Column 2");
                ws2.value(0, 2, "Column 3");

                ws.keepInActiveTab();
                wb.finish();
                watch.stop();
                System.out.println("Processing time :: " + watch.getTotalTime(TimeUnit.SECONDS) + "s");
            }

            DataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/vnd.ms-excel");
            os.flush();
            os.close();
            attachmentPart.setHeader("Content-Type","application/vnd.ms-excel; name=\"test.xlsx\""); // Rewrite Header
            attachmentPart.setHeader("Content-Disposition", "attachment; filename=\"test.xlsx\"");
            attachmentPart.setDataHandler(new DataHandler(dataSource));


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);
            msg.setContent(multipart);

            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



}
