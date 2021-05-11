package com.th.workbase.common.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cc
 * @date 2021-01-19-上午10:07
 */
public class POIUtil {
    /**
     * @Description: 读取 word
     */
    public static List<String> readWord(String filePath) throws Exception {

        List<String> linList = new ArrayList<String>();
        String buffer = "";
        try {
            if (filePath.endsWith(".doc")) {
                InputStream is = new FileInputStream(new File(filePath));
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText();
                ex.close();
                if (buffer.length() > 0) {
                    //使用回车换行符分割字符串
                    String[] arry = buffer.split("\\r\\n");
                    for (String string : arry) {
                        linList.add(string.trim());
                    }
                }
            } else if (filePath.endsWith(".docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                extractor.close();
                if (buffer.length() > 0) {
                    //使用换行符分割字符串
                    String[] arry = buffer.split("\\n");
                    for (String string : arry) {
                        linList.add(string.trim());
                    }
                }
            } else {
                return null;
            }
            return linList;
        } catch (Exception e) {
            System.out.print("error---->" + filePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Description: 读取 pdf
     */
    public static List<String> readPdf(String filePath) throws Exception {
        if (filePath.endsWith(".pdf")) {
            List<String> linList = new ArrayList<String>();
            try (PDDocument document = PDDocument.load(new File(filePath))) {
                document.getClass();
                if (!document.isEncrypted()) {
                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);
                    PDFTextStripper tStripper = new PDFTextStripper();
                    String pdfFileInText = tStripper.getText(document);
                    String[] lines = pdfFileInText.split("\\r?\\n");
                    for (String line : lines) {
                        linList.add(line.trim());
                    }
                    return linList;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

