package com.app.jiaxiaotong.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.app.jiaxiaotong.im.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2015/8/25.
 */
public class FileUtils {

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param imgPath
     *            图片格式
     * @return
     */
    public static String imageToString(String imgPath) {
        // 如果传进来的路径为空，则返回null
        if (StringUtil.isEmpty(imgPath)) {
            return null;
        }

        // 获取bitmap
        Bitmap bitmap = null;

        try {
//			bitmap = readBitmap(imgPath);
            bitmap = ImageUtils.getCompredBitmap(imgPath);
        } catch (Exception e) {
            // TODO: handle exception
            bitmap.recycle();
            System.gc();
        }

        // 如果获取的BitMap为空，则返回NULL
        if (bitmap == null) {
            return null;
        }

        // 定义文件类型
        String fileType = "jpg";
        if (imgPath.indexOf(".") != -1) {
            fileType = imgPath.substring(imgPath.lastIndexOf(".") + 1, imgPath.length());
        }

        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
            if (bitmap != null && !bitmap.isRecycled()) {
                // 回收并且置为null
                bitmap.recycle();
                bitmap = null;
            }
            byte[] imgBytes = out.toByteArray();
            return android.util.Base64.encodeToString(imgBytes,android.util.Base64.DEFAULT) + "****" + fileType;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                }

                System.gc();

                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                if (bitmap != null && !bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                }

                System.gc();

                e.printStackTrace();
            }
        }
    }

    public static String bitmapToString(Bitmap bitmap){

        // 如果获取的BitMap为空，则返回NULL
        if (bitmap == null) {
            return null;
        }

        // 定义文件类型
        String fileType = "jpg";
//				if (imgPath.indexOf(".") != -1) {
//					fileType = imgPath.substring(imgPath.lastIndexOf(".") + 1, imgPath.length());
//				}

        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
            if (bitmap != null && !bitmap.isRecycled()) {
                // 回收并且置为null
                bitmap.recycle();
                bitmap = null;
            }
            byte[] imgBytes = out.toByteArray();
            return android.util.Base64.encodeToString(imgBytes, android.util.Base64.DEFAULT) + "****" + fileType;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                }

                System.gc();

                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                if (bitmap != null && !bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                }

                System.gc();

                e.printStackTrace();
            }
        }
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {

            return null;
        }

    }

    // /**
    // * 将图片转化成二进制
    // *
    // * @param fileName
    // * @return
    // */
    // public static String imgToString(String filePath) {
    // // 如果传进来的文件路径为空，则返回null
    // if (StringUtil.isEmpty(filePath)) {
    // return null;
    // }
    //
    // // 将传进来的路径转化成file，如果file不存在，则返回null
    // File f = new File(filePath);
    // if (!f.exists()) {
    // return null;
    // }
    //
    // String fileName = f.getName();
    // String fileType = "jpg";
    // if (!StringUtil.isEmpty(fileName)) {
    // fileType = fileName.substring(fileName.lastIndexOf(".") + 1,
    // fileName.length());
    // }
    // BASE64Encoder encoder = new BASE64Encoder();
    // BufferedImage bi;
    // try {
    // bi = ImageIO.read(f);
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // ImageIO.write(bi, fileType, baos);
    // byte[] bytes = baos.toByteArray();
    //
    // return encoder.encodeBuffer(bytes).trim() + "****" + fileType;
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    // public static void main(String[] args) {
    // // String fileStr = imgToString("D:/工作文档/IMG_1298.JPG");
    // // System.out.println(imageToString(fileStr));
    // }

}
