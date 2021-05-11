package com.th.workbase.common.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileToolUtil {
    /**
     * @param @param  path
     * @param @return
     * @param @throws Exception    设定文件
     * @return List<List < String>>    返回类型
     * @throws 1.先用InputStream获取excel文件的io流 2.然后穿件一个内存中的excel文件HSSFWorkbook类型对象，这个对象表示了整个excel文件。
     *                                      3.对这个excel文件的每页做循环处理
     *                                      4.对每页中每行做循环处理
     *                                      5.对每行中的每个单元格做处理，获取这个单元格的值
     *                                      6.把这行的结果添加到一个List数组中
     *                                      7.把每行的结果添加到最后的总结果中
     *                                      8.解析完以后就获取了一个List<List<String>>类型的对象了
     * @Title: readXls
     * @Description: 处理xls文件
     */
    public static List<List<String>> readXls(InputStream is) throws Exception {
        // HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        int size = hssfWorkbook.getNumberOfSheets();
        // HSSFSheet 标识某一页
        HSSFSheet hssfSheet = hssfWorkbook.getSheet("Sheet1");
        if (hssfSheet != null) {
            // 处理当前页，循环读取每一行
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                // HSSFRow表示行
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int minColIx = hssfRow.getFirstCellNum();
                int maxColIx = hssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                // 遍历改行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    HSSFCell cell = hssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
                        try {
                            int i = (int) cell.getNumericCellValue();
                            rowList.add(i + "");
                        } catch (Exception e) {
                            rowList.add(cell.getNumericCellValue() + "");
                        }
                    } else if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
                        rowList.add(cell.getBooleanCellValue() + "");
                    } else {
                        rowList.add(cell.getStringCellValue());
                    }
                }
                result.add(rowList);
            }
        }
        return result;
    }

    /**
     * @param @param  path
     * @param @return
     * @param @throws Exception    设定文件
     * @return List<List < String>>    返回类型
     * @throws
     * @Title: readXlsx
     * @Description: 处理Xlsx文件
     */
    public static List<List<String>> readXlsx(InputStream is) throws Exception {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        Sheet xssfSheet = xssfWorkbook.getSheet("Sheet1");
        // 处理当前页，循环读取每一行
        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            Row xssfRow = xssfSheet.getRow(rowNum);
            try {
                int minColIx = xssfRow.getFirstCellNum();
                int maxColIx = xssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    Cell cell = xssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    rowList.add(cell.toString());
                }
                result.add(rowList);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(rowNum + "|" + xssfRow);
            }
        }
        return result;
    }

    /**
     * @param @param  path
     * @param @return
     * @param @throws Exception    设定文件
     * @return List<List < String>>    返回类型
     * @throws 1.先用InputStream获取excel文件的io流 2.然后穿件一个内存中的excel文件HSSFWorkbook类型对象，这个对象表示了整个excel文件。
     *                                      3.对这个excel文件的每页做循环处理
     *                                      4.对每页中每行做循环处理
     *                                      5.对每行中的每个单元格做处理，获取这个单元格的值
     *                                      6.把这行的结果添加到一个List数组中
     *                                      7.把每行的结果添加到最后的总结果中
     *                                      8.解析完以后就获取了一个List<List<String>>类型的对象了
     * @Title: readXls
     * @Description: 处理xls文件
     */
    public static List<List<String>> readXlsAll(InputStream is) throws Exception {
        // HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        int size = hssfWorkbook.getNumberOfSheets();
        // 循环每一页，并处理当前循环页
        for (int numSheet = 0; numSheet < size; numSheet++) {
            // HSSFSheet 标识某一页
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                // HSSFRow表示行
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int minColIx = hssfRow.getFirstCellNum();
                int maxColIx = hssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                // 遍历改行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    HSSFCell cell = hssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
                        rowList.add(cell.getNumericCellValue() + "");
                    } else if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
                        rowList.add(cell.getBooleanCellValue() + "");
                    } else {
                        rowList.add(cell.getStringCellValue());
                    }
                }
                result.add(rowList);
            }
        }
        return result;
    }

    /**
     * @param @param  path
     * @param @return
     * @param @throws Exception    设定文件
     * @return List<List < String>>    返回类型
     * @throws
     * @Title: readXlsx
     * @Description: 处理Xlsx文件
     */
    public static List<List<String>> readXlsxAll(InputStream is) throws Exception {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        // 循环每一页，并处理当前循环页
        for (Sheet xssfSheet : xssfWorkbook) {
            if (xssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                Row xssfRow = xssfSheet.getRow(rowNum);
                try {
                    int minColIx = xssfRow.getFirstCellNum();
                    int maxColIx = xssfRow.getLastCellNum();
                    List<String> rowList = new ArrayList<String>();
                    for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                        Cell cell = xssfRow.getCell(colIx);
                        if (cell == null) {
                            continue;
                        }
                        rowList.add(cell.toString());
                    }
                    result.add(rowList);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(rowNum + "|" + xssfRow);
                }
            }
        }
        return result;
    }
}
