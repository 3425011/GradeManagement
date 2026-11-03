package com.score.util;

import com.score.entity.GradeVO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 工具类（基于 Apache POI）
 * 职责：成绩列表导出为 .xlsx、读取上传的 .xlsx、生成导入模板。
 * 读取时用 DataFormatter 把每个单元格统一转成字符串，避免“学号被当成数字、
 * 2021001 变成 2021001.0”这类问题。
 */
public class ExcelUtil {

    /** 导出成绩列表到输出流（列：学号/姓名/班级/科目/学期/分数/录入时间） */
    public static void writeGradeList(List<GradeVO> list, OutputStream out) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩");

            String[] titles = {"学号", "姓名", "班级", "科目", "学期", "分数", "录入时间"};
            writeRow(sheet, 0, titles, workbook);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            int r = 1;
            for (GradeVO vo : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(vo.getStudentNo() == null ? "" : vo.getStudentNo());
                row.createCell(1).setCellValue(vo.getStudentName() == null ? "" : vo.getStudentName());
                row.createCell(2).setCellValue(vo.getClassName() == null ? "" : vo.getClassName());
                row.createCell(3).setCellValue(vo.getSubjectName() == null ? "" : vo.getSubjectName());
                row.createCell(4).setCellValue(vo.getSemester() == null ? "" : vo.getSemester());
                if (vo.getScore() != null) {
                    row.createCell(5).setCellValue(vo.getScore());
                }
                row.createCell(6).setCellValue(vo.getCreateTime() == null ? "" : sdf.format(vo.getCreateTime()));
            }

            // 列宽自适应（按内容长度粗略设定）
            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) * 2, 12 * 256));
            }
            workbook.write(out);
        }
    }

    /** 生成导入模板（含表头与一行示例数据） */
    public static void writeImportTemplate(OutputStream out) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩导入模板");
            String[] titles = {"学号", "科目名称", "学期", "分数"};
            writeRow(sheet, 0, titles, workbook);

            // 示例行（便于用户参照格式）
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("2021001");
            example.createCell(1).setCellValue("Java高级程序设计");
            example.createCell(2).setCellValue("2025-2026-1");
            example.createCell(3).setCellValue(88);

            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) * 2, 12 * 256));
            }
            workbook.write(out);
        }
    }

    /** 读取工作簿第一个 Sheet，返回每行（含表头）的字符串单元格列表 */
    public static List<List<String>> readFirstSheet(InputStream in) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = WorkbookFactory.create(in)) {
            if (workbook.getNumberOfSheets() == 0) {
                return rows;
            }
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> cells = new ArrayList<>();
                for (Cell cell : row) {
                    cells.add(formatter.formatCellValue(cell).trim());
                }
                rows.add(cells);
            }
        }
        return rows;
    }

    /** 写入表头行（加粗） */
    private static void writeRow(Sheet sheet, int rowIndex, String[] values, XSSFWorkbook workbook) {
        Row row = sheet.createRow(rowIndex);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            cell.setCellStyle(headerStyle);
        }
    }
}
