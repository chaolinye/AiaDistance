package org.freedom.aia;

import org.freedom.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaolin on 2017/6/20.
 */
public class AiaDistance extends JFrame{
    public AiaDistance(String title){
        super(title);
        init();
    }
    public void init(){
        Box mainBox= Box.createVerticalBox();

        Box fileBox=Box.createVerticalBox();
        Box srcBox=Box.createHorizontalBox();
        Box desBox=Box.createHorizontalBox();
        fileBox.add(srcBox);
        fileBox.add(Box.createVerticalStrut(10));
        fileBox.add(desBox);

        JButton srcBtn=new JButton("选择需要求距的Aia的父路径或者Zip包");
        JTextField srcTf=new JTextField();
        srcTf.setEditable(false);
        srcTf.setPreferredSize(new Dimension(500,20));
        srcBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jf.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    @Override
                    public boolean accept(File f) {
                        // TODO Auto-generated method stub
                        if(f.isDirectory()) return true;
                        if(f.getName().toLowerCase().endsWith(".zip")) return true;
                        return false;
                    }
                });
                int result = jf.showOpenDialog(AiaDistance.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    srcTf.setText(jf.getSelectedFile().getAbsolutePath());
                }
            }
        });

        srcBox.add(Box.createHorizontalGlue());
        srcBox.add(srcBtn);
        srcBox.add(srcTf);
        srcBox.add(Box.createHorizontalGlue());

        JButton desBtn=new JButton("选择求距结果文件路径");
        JTextField desTf=new JTextField();
        desTf.setEditable(false);
        desBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jf.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    @Override
                    public boolean accept(File f) {
                        // TODO Auto-generated method stub
                        if(f.isDirectory()) return true;
                        return false;
                    }
                });
                int result = jf.showOpenDialog(AiaDistance.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    desTf.setText(jf.getSelectedFile().getAbsolutePath());
                }
            }
        });

        desBox.add(Box.createHorizontalGlue());
        desBox.add(desBtn);
        desBox.add(desTf);
        desBox.add(Box.createHorizontalGlue());

        Box calculateBox=Box.createHorizontalBox();
        JButton calculateBtn=new JButton("求距");
        calculateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(srcTf.getText().isEmpty()||desTf.getText().isEmpty()){
                    JOptionPane.showMessageDialog(AiaDistance.this,"路径不能为空");
                    return;
                }
                calculateBtn.setEnabled(false);
                String srcPath=srcTf.getText();
                File dir=new File(srcPath);
                List<AiaProject> aiaProjects=new ArrayList<>();
                if (dir.isDirectory()) {
                    List<File> files = FileUtil.findAiaFile(dir);
                    System.out.println("start parse");
                    long start= System.currentTimeMillis();
                    for (int i = 0; i < files.size(); i++) {
                        AiaProject aia=new AiaProject(files.get(i));
                        aiaProjects.add(aia);
                    }
                }else if (srcPath.toLowerCase().endsWith(".zip")) {
                    try {
                        aiaProjects = FileUtil.UnzipToAiaProjects(new FileInputStream(dir));
                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                CalculateDistance calculator=new CalculateDistance(aiaProjects);
                double[][] dis=calculator.calculateAllDistance();
                String desPath=desTf.getText()+"\\result"+System.currentTimeMillis()+".xls";

                FileUtil.writeDisMatrixToExcel(aiaProjects,dis,desPath);
                calculateBtn.setEnabled(true);
                int re=JOptionPane.showConfirmDialog(AiaDistance.this,"求距结果文件："+desPath+"\n是否打开结果文件","result",JOptionPane.YES_NO_OPTION);
                if(re==JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(new File(desPath));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        calculateBox.add(Box.createHorizontalGlue());
        calculateBox.add(calculateBtn);
        calculateBox.add(Box.createHorizontalGlue());

        mainBox.add(Box.createVerticalGlue());
        mainBox.add(fileBox);
        mainBox.add(Box.createVerticalStrut(20));
        mainBox.add(calculateBox);
        mainBox.add(Box.createVerticalGlue());
        this.add(mainBox);
    }

    public static void main(String[] args) {
        AiaDistance ad=new AiaDistance("Aia距离求解工具");
        ad.pack();
        ad.setVisible(true);
        ad.setLocationRelativeTo(null);
        ad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
