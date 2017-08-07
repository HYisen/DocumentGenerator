package net.alexhyisen.dg.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.alexhyisen.dg.Utility;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Exporter {
    private ObservableList<Item> data;

    public void setData(ObservableList<Item> data) {
        this.data = data;
    }

    public void export(Path target) throws IOException, InvalidFormatException {
        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFFont[] font = {wb.createFont(), wb.createFont()};
        font[1].setBold(true);

        XSSFCellStyle label = wb.createCellStyle();
        label.setAlignment(HorizontalAlignment.CENTER);
        label.setVerticalAlignment(VerticalAlignment.CENTER);
        label.setFont(font[1]);
        XSSFCellStyle title = wb.createCellStyle();
        title.setAlignment(HorizontalAlignment.CENTER);
        title.setFont(font[1]);
        title.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFCellStyle value = wb.createCellStyle();
        value.setAlignment(HorizontalAlignment.CENTER);
        value.setFont(font[0]);

        XSSFSheet ws = wb.createSheet("formal");
        int width = 4480;
        ws.setColumnWidth(0, width);
        ws.setColumnWidth(1, width);
        ws.setColumnWidth(2, width);
        ws.setColumnWidth(3, width);
        int linage = 0;
        for (Item item : data) {
            int length = 4;
            XSSFRow row;
            XSSFCell cell;

            row = ws.createRow(linage);
            cell = row.createCell(1);
            cell.setCellStyle(title);
            cell.setCellValue("控件类型");
            cell = row.createCell(2);
            cell.setCellStyle(title);
            cell.setCellValue("校验");
            cell = row.createCell(3);
            cell.setCellStyle(title);
            cell.setCellValue("状态");
            row = ws.createRow(linage + 1);
            cell = row.createCell(1);
            cell.setCellStyle(value);
            cell.setCellValue(item.getCls());
            cell = row.createCell(2);
            cell.setCellStyle(value);
            cell.setCellValue(item.getRequired().equals("true") ? "必须" : "");
            cell = row.createCell(3);
            cell.setCellStyle(value);
            cell.setCellValue(item.getId().startsWith("btn") ? "启用" :
                    item.getReadonly().equals("true") ? "只读" : "可编辑");

            row = ws.createRow(linage + 2);
            cell = row.createCell(1);
            cell.setCellStyle(title);
            cell.setCellValue("绑定值");
            ws.addMergedRegion(new CellRangeAddress(
                    linage + 2, linage + 2, 2, 3));
            cell = row.createCell(2);
            cell.setCellStyle(title);
            cell.setCellValue("功能点");
            row = ws.createRow(linage + 3);
            cell = row.createCell(1);
            cell.setCellStyle(value);
            cell.setCellValue(String.format("(%s)", item.getName()));
            ws.addMergedRegion(new CellRangeAddress(
                    linage + 3, linage + 3, 2, 3));
            cell = row.createCell(2);
            cell.setCellStyle(value);
            cell.setCellValue("");


            if (item.getId().startsWith("cmb")) {
                length = 6;

                row = ws.createRow(linage + 4);
                cell = row.createCell(1);
                cell.setCellStyle(title);
                cell.setCellValue("数据源");
                cell = row.createCell(2);
                cell.setCellStyle(title);
                cell.setCellValue("值字段");
                cell = row.createCell(3);
                cell.setCellStyle(title);
                cell.setCellValue("显示字段");
                row = ws.createRow(linage + 5);
                cell = row.createCell(1);
                cell.setCellStyle(value);
                cell.setCellValue("");
                cell = row.createCell(2);
                cell.setCellStyle(value);
                cell.setCellValue("value");
                cell = row.createCell(3);
                cell.setCellStyle(value);
                cell.setCellValue("text");
            }

            row = ws.getRow(linage);
            ws.addMergedRegion(new CellRangeAddress(
                    linage, linage + length - 1, 0, 0));
            cell = row.createCell(0);
            cell.setCellStyle(label);
            cell.setCellValue(item.getLabel());

            linage += length;
        }

        ws = wb.createSheet("basic");
        for (int i = 0; i < data.size(); i++) {
            Item item = data.get(i);
            XSSFRow row = ws.createRow(i);
            row.createCell(0).setCellValue(item.getLabel());
            row.createCell(1).setCellValue(item.getName());
            row.createCell(2).setCellValue(item.getCls());
            row.createCell(3).setCellValue(item.getId());
            row.createCell(4).setCellValue(item.getReadonly());
            row.createCell(5).setCellValue(item.getRequired());
        }


        if (target.toFile().exists()) {
            Files.delete(target);
        }
        Files.createFile(target);
        FileOutputStream os = new FileOutputStream(target.toFile());
        wb.write(os);
        os.close();
    }

    public static void main(String[] args) throws Exception {
        Path pJ = Paths.get(".", "sample.js");
        Path pH = Paths.get(".", "sample.jsp");
        Map<String, Map<String, String>> dH = new HttpExtractor().extract(pH);
        Map<String, Map<String, String>> dJ = new JsExtractor().extract(pJ);
        Map<String, Map<String, String>> d = Utility.merge(dH, dJ);
//        Utility.printData(data);
//        System.out.println();
//        Utility.printData(dH);
//        System.out.println();
//        Utility.printData(dJ);

        Exporter e = new Exporter();
        ObservableList<Item> data = FXCollections.observableArrayList();
        Utility.transform(d, data);
        data.forEach(v -> System.out.println("item id = " + v.getId()));
        e.setData(data);
        e.export(Paths.get(".", "out.xlsx"));
    }
}
