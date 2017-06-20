package org.freedom.util;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.freedom.aia.AiaProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by chaolin on 2017/4/20.
 */
public class FileUtil {
    /**
     * 查找路径下的Aia文件
     * @param path
     * @return
     */
    public static List<File> findAiaFile(File path){
        List<File> fileList=new ArrayList<>();
        if(path.isDirectory()){
            File[] files=path.listFiles();
            for(File file:files){
                fileList.addAll(findAiaFile(file));
            }
        }else{
            if(path.getName().toLowerCase().endsWith(".aia")){
                fileList.add(path);
            }
        }
        return fileList;
    }
    /**
     * 获取ZIP包中Aia项目集合
     * @param file
     * @return
     */
    public static List<AiaProject> UnzipToAiaProjects(InputStream file){
        List<AiaProject> aiaProjects=new ArrayList<>();
        InputStream in = new BufferedInputStream(file);
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        try {
            while ((ze = zin.getNextEntry()) != null) {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int count = -1;
                byte[] data = new byte[1024];
                while ((count = zin.read(data)) != -1) {
                    bo.write(data, 0, count);
                }
                if (ze.isDirectory()) {
                } else if (ze.getName().toLowerCase().endsWith(".aia")) {
                    aiaProjects.add(new AiaProject(ze.getName(),new ByteArrayInputStream(bo.toByteArray())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zin.closeEntry();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();;
            }
        }
        return aiaProjects;
    }

    public static void writeDisMatrixToExcel(List<AiaProject> aias,double[][] disMatrix,String path){
        File file=new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            HSSFWorkbook book=new HSSFWorkbook();
            HSSFSheet sheet=book.createSheet("sheet1");
            HSSFRow row0=sheet.createRow(0);
            row0.createCell(0).setCellValue("aia文件名");
            for(int i=0;i<aias.size();i++){
                row0.createCell(i+1).setCellValue(aias.get(i).getName());
            }
            for(int i=0;i<aias.size();i++){
                HSSFRow row=sheet.createRow(i+1);
                row.createCell(0).setCellValue(aias.get(i).getName());
                for(int j=0;j<aias.size();j++){
                    row.createCell(j+1).setCellValue(disMatrix[i][j]);
                }
            }
            book.write(file);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
