package com.th.workbase.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class UrlFilesToZip {

    /**
     * 把文件集合打成zip压缩包
     *
     * @param srcFiles 压缩文件集合
     * @param zipFile  zip文件名
     * @throws RuntimeException 异常
     */
    public static void toZip(List<File> srcFiles, File zipFile) throws RuntimeException {
        long start = System.currentTimeMillis();
        if (zipFile == null) {
            log.error("压缩包文件名为空！");
            return;
        }
        if (!zipFile.getName().endsWith(".zip")) {
            log.error("压缩包文件名异常，zipFile={}", zipFile.getPath());
            return;
        }
        ZipOutputStream zos = null;
        try {
            FileOutputStream out = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[1024];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.setComment("我是注释");
                zos.closeEntry();
                in.close();
                out.close();
            }
            long end = System.currentTimeMillis();
            log.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            log.error("ZipUtil toZip exception, ", e);
            throw new RuntimeException("zipFile error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    log.error("ZipUtil toZip close exception, ", e);
                }
            }
        }
    }


    /**
     * 读取某个文件夹下的所有文件
     */
    public static ArrayList<File> getFiles(String filepath) throws FileNotFoundException, IOException {
        ArrayList<File> filesResult = new ArrayList<>();

        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                System.out.println("文件");
                System.out.println("path=" + file.getPath());
                System.out.println("absolutepath=" + file.getAbsolutePath());
                System.out.println("name=" + file.getName());

            } else if (file.isDirectory()) {
                System.out.println("文件夹");
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "/" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        System.out.println("path=" + readfile.getPath());
                        System.out.println("absolutepath="
                                + readfile.getAbsolutePath());
                        System.out.println("name=" + readfile.getName());
                        filesResult.add(readfile);
                    } else if (readfile.isDirectory()) {
                        getFiles(filepath + "/" + filelist[i]);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("readfile()   Exception:" + e.getMessage());
        }
        return filesResult;
    }

    //生成zip
    public static boolean createCardImgZip(String sourcePath, String zipName, String zipPath) {
        boolean result = false;
        File sourceFile = new File(sourcePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if (sourceFile.exists() == false) {
            System.out.println("File catalog:" + sourcePath + "not exist!");
        } else {
            try {
                if (!new File(zipPath).exists()) {
                    new File(zipPath).mkdirs();
                }
                File zipFile = new File(zipPath + "/" + zipName + ".zip");
//                if (zipFile.exists()) {
//                    System.out.println(zipPath + "Catalog File: " + zipName + ".zip" + "pack file.");
//                } else {
                File[] sourceFiles = sourceFile.listFiles();
                if (null == sourceFiles || sourceFiles.length < 1) {
                    System.out.println("File Catalog:" + sourcePath + "nothing in there,don't hava to compress!");
                } else {
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(new BufferedOutputStream(fos));
                    byte[] bufs = new byte[1024 * 10];
                    for (int i = 0; i < sourceFiles.length; i++) {
                        // create .zip and put pictures in
                        ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                        zos.putNextEntry(zipEntry);
                        // read documents and put them in the zip
                        fis = new FileInputStream(sourceFiles[i]);
                        bis = new BufferedInputStream(fis, 1024 * 10);
                        int read = 0;
                        while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                            zos.write(bufs, 0, read);
                        }
                    }
                    result = true;
                }
//                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                try {
                    if (null != bis)
                        bis.close();
                    if (null != zos)
                        zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    /**
     * 压缩图片，并等比缩小。
     *
     * @param data    输入图片数据的byte[]。
     * @param width   最大输出宽度，但是最后会根据图片本身比例调整。推荐值800。
     * @param height  最大输出高度，但是最后会根据图片本身比例调整。推荐值600。
     * @param type    指定最后存储的图片类型，支持字符串jpg,png,gif,bmp,jpeg。如果为null，则默认输出jpg格式图片。
     * @param maxSize 指定最大输出图片的容量大小。可以为null表示不指定压缩容量大小。不要小于10000，推荐100000。
     * @return 输出图片数据的byte[]。
     * @throws Exception
     * @author aren
     */
    public static byte[] zipImageToScaledSize(byte[] data, int width, int height, String type, Integer maxSize)
            throws Exception {
        if (data == null) {
            return null;
        }
        if (width <= 0 || height <= 0) {
            width = 800;
            height = 600;
        }
        // 设定输出格式
        String[] supportType = new String[]{"jpg", "png", "bmp", "jpeg", "gif"};
        if (type == null || !ArrayUtils.contains(supportType, type)) {
            type = "jpg";
        }
        int pointedHeight;
        int pointedWidth;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        inputStream.close();
        int originalHeight = bufferedImage.getHeight();
        int originalWidth = bufferedImage.getWidth();
        // 设定等比例压缩。
        if ((originalHeight / (double) height) > (originalWidth / (double) width)) {
            pointedHeight = NumberUtils.min(height, originalHeight);
            pointedWidth = -1;
        } else {
            pointedHeight = -1;
            pointedWidth = NumberUtils.min(width, originalWidth);
        }
        // 压缩图片，此处附上颜色类型BufferedImage.TYPE_INT_RGB。Color.WHITE，可以有效避免png转jpg时图片发红的问题。
        Image newImage = bufferedImage.getScaledInstance(pointedWidth, pointedHeight, Image.SCALE_SMOOTH);
        BufferedImage newBufferedImage = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        newBufferedImage.getGraphics().drawImage(newImage, 0, 0, Color.WHITE, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(newBufferedImage, type, byteArrayOutputStream);
        byteArrayOutputStream.close();
        data = byteArrayOutputStream.toByteArray();
        if (maxSize != null && data.length > maxSize) {
            // 设定递归的保险，以免图片质量太差
            if (maxSize < 5000 && (data.length > 10 * maxSize)) {
                maxSize = 5000;
            }
            // 递归压缩
            double scale = Math.max(Math.pow(maxSize / (double) data.length, 0.5), 0.9);
            return zipImageToScaledSize(data, (int) (width * scale), (int) (height * scale), type, maxSize);
        } else {
            return data;
        }
    }

    public static void ZipImage(String FilePath, String saveFileName) throws Exception {
        File fileFolder = new File(FilePath);
        File[] fileList = fileFolder.listFiles();
        if (fileList == null || fileList.length == 0) {
            return;
        }
        for (File file : fileList) {
            String fileName = file.getName();
            if (!fileName.endsWith(".doc")) {
                InputStream input = new FileInputStream(file);
                byte[] data = new byte[(input.available())];
                input.read(data);
                input.close();
                String type = "jpg";
                data = UrlFilesToZip.zipImageToScaledSize(data, 600, 800, type, 100000);
                OutputStream output = new FileOutputStream(saveFileName + "/" + file.getName());
                output.write(data);
                output.close();
            }

        }
    }

}