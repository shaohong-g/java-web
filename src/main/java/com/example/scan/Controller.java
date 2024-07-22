package com.example.scan;

import com.example.util.Config;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/")
public class Controller {
    @Autowired
    ApplicationContext applicationContext;

    @GetMapping("health")
    public String health(){
        return "Hello Health";
    }

    @GetMapping(value ="/beans", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> test() {
        System.out.println("Test");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("context", applicationContext.getBeanDefinitionNames());
        try {
            map.put("parent-context", applicationContext.getParent().getBeanDefinitionNames());
        } catch (Exception ex) {
            map.put("parent-context", "Fail to retrieve");
        }
        map.put("datetime", ZonedDateTime.now().toString());
        return map;
    }

    @GetMapping(value ="/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getConfig() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mailPassword", Config.mailPassword);
        map.put("mailUsername", Config.mailUsername);
        map.put("datetime", ZonedDateTime.now().toString());
        return map;
    }


    @GetMapping("download")
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void  downloadExcel(HttpServletResponse response) throws IOException {
        // https://stackoverflow.com/questions/27741283/return-file-from-spring-controller-having-outputstream

        response.setHeader("Content-disposition","attachment; filename=" + "test.xlsx");

        // Create file only if all operations work
        try (OutputStream os = response.getOutputStream(); Workbook wb = new Workbook(os, "DemoExcel", "1.0"); ) {
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
            System.out.println("Processing time :: " + watch.getTotalTimeSeconds() + "s");
        }

        response.flushBuffer();
    }

    @PostMapping(value = "upload" ,consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> upload(@RequestParam(name = "file") MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();

        String[] RequiredColumns = new String[]{"Column 1","Column 3", "Column 4", "Column 5"};
        ArrayList<Integer> RequiredColumnsIdx = new ArrayList<Integer>();
        Map<String, Integer> sheetsMapping = new HashMap<String, Integer>();

        AtomicBoolean isHeaderPresent = new AtomicBoolean(true);

        try {
            InputStream is = file.getInputStream();
            ReadableWorkbook wb = new ReadableWorkbook(is);

            StopWatch watch = new StopWatch();
            watch.start();

            // Check sheets
            wb.getSheets().forEach(sheet -> {
                sheetsMapping.put(sheet.getName(),sheet.getIndex());
            });

            // Check Columns and rows
            Sheet sheet1 = wb.getSheet(0).orElseThrow();

            try (Stream<Row> rows = sheet1.openStream()) {

                AtomicInteger count = new AtomicInteger(1);
                rows.forEach(r -> {
                    // Columns
                    if (isHeaderPresent.get()){
                        System.out.println("Header Row Count: " + r.getCellCount());
                        if (r.getCellCount() < RequiredColumns.length){
                            throw new RuntimeException("Not enough Columns. Expect " + RequiredColumns.length);
                        }
                        for (String header: RequiredColumns){
                            boolean isPresent = false;
                            for (int i = 0; i < r.getCellCount(); i++) {
                                if (Objects.equals(header, r.getCellAsString(i).orElse(null )) ){
                                    RequiredColumnsIdx.add(i);
                                    isPresent = true;
                                    break;
                                }
                            }
                            if (! isPresent) {
                                throw new RuntimeException("Cannot find Required Column: " + header);
                            }
                        }
                        result.put("HEADERS IDX", RequiredColumnsIdx);
                        isHeaderPresent.set(false);
                    } else {
                        // Rows
                        ArrayList<String> rowValArray = new ArrayList<String>();
                        for (int rowIdx: RequiredColumnsIdx) {
                            rowValArray.add(r.getCellAsString(rowIdx).orElse(null ));
                        }
                        result.put("ROW " + count, rowValArray);
                        count.getAndIncrement();
                        System.out.println(rowValArray + " Get Static: " + r.getCellAsString( RequiredColumnsIdx.get(3) ).orElse(null ));
                    }
                });

            }
            watch.stop();
            System.out.println("Processing time :: " + watch.getTotalTimeSeconds() + "s");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
        System.out.println(sheetsMapping.entrySet());
        return ResponseEntity.ok(result);
    }



}
