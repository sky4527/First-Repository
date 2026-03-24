package org.example.ui;

import org.example.model.ItemSet;
import org.example.algorithm.DKnapsackDP;
import org.example.util.DataLoader;
import org.example.util.ChartGenerator;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * 主界面类
 * 提供用户交互界面，包含文件选择、数据展示、求解、导出等功能
 *
 * @author [曹微婕]
 */
public class MainFrame extends JFrame {

    private List<ItemSet> itemSets;           // 当前项集数据
    private int capacity = 100;               // 背包容量
    private DKnapsackDP.Result currentResult; // 当前求解结果

    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    private JTextArea resultArea;
    private JTextField txtCapacity;
    private JLabel lblFileStatus;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("D{0-1}背包问题求解器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        // 主布局
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部工具栏
        JToolBar toolbar = createToolbar();

        // 状态栏
        lblFileStatus = new JLabel("未加载数据文件");
        lblFileStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 中央区域：左边表格，右边图表
        JSplitPane splitPane = createSplitPane();

        // 底部结果区域
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(lblFileStatus, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton btnOpen = new JButton("📂 打开数据文件");
        JButton btnSort = new JButton("📊 按价值重量比排序");
        JButton btnSolve = new JButton("⚡ 动态规划求解");
        JButton btnExport = new JButton("💾 导出结果");
        JButton btnExportExcel = new JButton("📎 导出Excel");

        txtCapacity = new JTextField("100", 8);

        btnOpen.addActionListener(e -> openFile());
        btnSort.addActionListener(e -> sortByRatio());
        btnSolve.addActionListener(e -> solve());
        btnExport.addActionListener(e -> exportResult());
        btnExportExcel.addActionListener(e -> exportExcel());

        toolbar.add(btnOpen);
        toolbar.addSeparator();
        toolbar.add(new JLabel("背包容量:"));
        toolbar.add(txtCapacity);
        toolbar.addSeparator();
        toolbar.add(btnSort);
        toolbar.add(btnSolve);
        toolbar.addSeparator();
        toolbar.add(btnExport);
        toolbar.add(btnExportExcel);

        return toolbar;
    }

    private JSplitPane createSplitPane() {
        // 左边表格
        String[] columns = {"项集ID", "重量1", "价值1", "重量2", "价值2", "重量3", "价值3", "价值/重量比"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(500, 0));
        tableScroll.setBorder(BorderFactory.createTitledBorder("项集数据"));

        // 右边图表区域
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("价值-重量散点图"));
        chartPanel.add(new JLabel("请打开数据文件生成图表", SwingConstants.CENTER), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, chartPanel);
        splitPane.setDividerLocation(550);

        return splitPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("求解结果"));

        resultArea = new JTextArea(8, 0);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);

        bottomPanel.add(resultScroll, BorderLayout.CENTER);

        return bottomPanel;
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser("./data");
        fileChooser.setCurrentDirectory(new File("."));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            itemSets = DataLoader.loadData(file.getAbsolutePath());
            capacity = DataLoader.getCapacity(file.getAbsolutePath(), 100);
            txtCapacity.setText(String.valueOf(capacity));

            if (itemSets != null && !itemSets.isEmpty()) {
                updateTable();
                updateChart();
                lblFileStatus.setText("已加载: " + file.getName() + " | 项集数: " + itemSets.size());
                resultArea.setText("数据加载成功！\n");
            } else {
                JOptionPane.showMessageDialog(this, "数据文件为空或格式错误");
            }
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        if (itemSets == null) {
            return;
        }

        for (ItemSet set : itemSets) {
            tableModel.addRow(new Object[]{
                    set.getId(),
                    set.getWeight1(), set.getValue1(),
                    set.getWeight2(), set.getValue2(),
                    set.getWeight3(), set.getValue3(),
                    String.format("%.2f", set.getRatio())
            });
        }
    }

    private void updateChart() {
        if (itemSets == null || itemSets.isEmpty()) {
            return;
        }

        chartPanel.removeAll();
        JPanel newChart = ChartGenerator.createScatterPlot(itemSets, "物品价值-重量分布图");
        chartPanel.add(newChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void sortByRatio() {
        if (itemSets == null || itemSets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先加载数据文件");
            return;
        }

        DKnapsackDP.sortByRatio(itemSets);
        updateTable();
        updateChart();
        resultArea.append("✓ 已按价值重量比非递增排序\n");
    }

    private void solve() {
        if (itemSets == null || itemSets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先加载数据文件");
            return;
        }

        try {
            capacity = Integer.parseInt(txtCapacity.getText().trim());
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "背包容量必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的背包容量（正整数）");
            return;
        }

        currentResult = DKnapsackDP.solve(itemSets, capacity);

        StringBuilder sb = new StringBuilder();
        sb.append("========== 求解结果 ==========\n");
        sb.append("背包容量: ").append(capacity).append("\n");
        sb.append("最大总价值: ").append(currentResult.getMaxValue()).append("\n");
        sb.append("求解耗时: ").append(String.format("%.3f", currentResult.getTimeMs())).append(" ms\n");
        sb.append("\n选择的方案:\n");

        int totalWeight = 0;
        for (int i = 0; i < currentResult.getSelectedIndexes().size(); i++) {
            int idx = currentResult.getSelectedIndexes().get(i);
            int type = currentResult.getSelectedTypes().get(i);
            ItemSet set = itemSets.get(idx);

            int weight = 0;
            int value = 0;
            switch (type) {
                case 1:
                    weight = set.getWeight1();
                    value = set.getValue1();
                    break;
                case 2:
                    weight = set.getWeight2();
                    value = set.getValue2();
                    break;
                case 3:
                    weight = set.getWeight3();
                    value = set.getValue3();
                    break;
            }
            totalWeight += weight;
            sb.append(String.format("  项集 %d → 选物品%d (重量:%d, 价值:%d)\n", idx, type, weight, value));
        }
        sb.append("\n总重量: ").append(totalWeight).append(" / ").append(capacity).append("\n");
        sb.append("================================\n");

        resultArea.setText(sb.toString());
    }

    private void exportResult() {
        if (currentResult == null) {
            JOptionPane.showMessageDialog(this, "请先求解后再导出");
            return;
        }

        JFileChooser fileChooser = new JFileChooser("./result");
        fileChooser.setCurrentDirectory(new File("result"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".txt")) {
                path += ".txt";
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                writer.write(resultArea.getText());
                JOptionPane.showMessageDialog(this, "导出成功: " + path);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage());
            }
        }
    }

    private void exportExcel() {
        if (currentResult == null) {
            JOptionPane.showMessageDialog(this, "请先求解后再导出");
            return;
        }

        JFileChooser fileChooser = new JFileChooser("./result");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".xlsx")) {
                path += ".xlsx";
            }

            try {
                // 简单的Excel导出（使用CSV格式代替，避免POI复杂性）
                try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
                    writer.println("背包容量," + capacity);
                    writer.println("最大总价值," + currentResult.getMaxValue());
                    writer.println("求解耗时(ms)," + currentResult.getTimeMs());
                    writer.println();
                    writer.println("项集索引,选择的物品类型,重量,价值");

                    for (int i = 0; i < currentResult.getSelectedIndexes().size(); i++) {
                        int idx = currentResult.getSelectedIndexes().get(i);
                        int type = currentResult.getSelectedTypes().get(i);
                        ItemSet set = itemSets.get(idx);

                        int weight = 0;
                        int value = 0;
                        switch (type) {
                            case 1:
                                weight = set.getWeight1();
                                value = set.getValue1();
                                break;
                            case 2:
                                weight = set.getWeight2();
                                value = set.getValue2();
                                break;
                            case 3:
                                weight = set.getWeight3();
                                value = set.getValue3();
                                break;
                        }
                        writer.println(idx + "," + type + "," + weight + "," + value);
                    }
                }
                JOptionPane.showMessageDialog(this, "导出成功: " + path);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage());
            }
        }
    }
}