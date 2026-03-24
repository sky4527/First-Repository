package org.example;

import org.example.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

/**
 * 程序入口类
 *
 * @author [曹微婕]
 */
public class Main {

    public static void main(String[] args) {
        // 设置界面外观
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("界面主题加载失败，使用默认主题");
        }

        // 在事件调度线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}