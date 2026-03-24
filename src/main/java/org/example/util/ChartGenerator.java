package org.example.util;

import org.example.model.ItemSet;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.title.TextTitle;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 图表生成工具类
 * 负责生成物品价值-重量散点图
 *
 * @author [曹微婕]
 */
public class ChartGenerator {

    // 定义三种物品的颜色和形状
    private static final Color COLOR_ITEM1 = new Color(255, 99, 132);   // 红色
    private static final Color COLOR_ITEM2 = new Color(54, 162, 235);   // 蓝色
    private static final Color COLOR_ITEM3 = new Color(75, 192, 192);   // 绿色

    private static final Shape SHAPE_ITEM1 = new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6);   // 圆形
    private static final Shape SHAPE_ITEM2 = new java.awt.geom.Rectangle2D.Double(-3, -3, 6, 6); // 方形
    private static final Shape SHAPE_ITEM3 = new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6);   // 圆形，但可用填充区分

    /**
     * 创建散点图（增强版）
     *
     * @param itemSets 项集列表
     * @param title 图表标题
     * @return JPanel 包含图表面板
     */
    public static JPanel createScatterPlot(List<ItemSet> itemSets, String title) {
        // 创建三个数据集
        XYSeries seriesItem1 = new XYSeries("物品1 (单独购买)");
        XYSeries seriesItem2 = new XYSeries("物品2 (单独购买)");
        XYSeries seriesItem3 = new XYSeries("物品3 (折扣组合)");

        for (ItemSet set : itemSets) {
            seriesItem1.add(set.getWeight1(), set.getValue1());
            seriesItem2.add(set.getWeight2(), set.getValue2());
            seriesItem3.add(set.getWeight3(), set.getValue3());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesItem1);
        dataset.addSeries(seriesItem2);
        dataset.addSeries(seriesItem3);

        // 创建图表
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,                    // 图表标题
                "重量 (Weight)",          // X轴标签
                "价值 (Value)",           // Y轴标签
                dataset,                  // 数据集
                PlotOrientation.VERTICAL,
                true,                     // 显示图例
                true,                     // 显示提示
                false                     // 显示URL
        );

        // 设置图表整体样式
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        // 设置标题字体
        chart.getTitle().setFont(new Font("微软雅黑", Font.BOLD, 16));
        chart.getTitle().setPaint(new Color(33, 33, 33));

        // 获取绘图区域
        XYPlot plot = chart.getXYPlot();

        // 设置绘图区域背景色
        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setDomainGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        // 设置X轴（重量轴）
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
        domainAxis.setLabelPaint(new Color(66, 66, 66));
        domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 10));
        domainAxis.setLowerMargin(0.05);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setAutoRangeIncludesZero(true);
        domainAxis.setLabel("重量 (Weight)");

        // 设置Y轴（价值轴）
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
        rangeAxis.setLabelPaint(new Color(66, 66, 66));
        rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 10));
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setLabel("价值 (Value)");

        // 自定义渲染器（设置点的形状和颜色）
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // 物品1：红色圆形
        renderer.setSeriesShape(0, SHAPE_ITEM1);
        renderer.setSeriesPaint(0, COLOR_ITEM1);
        renderer.setSeriesLinesVisible(0, false);  // 不画线
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);

        // 物品2：蓝色方形
        renderer.setSeriesShape(1, SHAPE_ITEM2);
        renderer.setSeriesPaint(1, COLOR_ITEM2);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShapesFilled(1, true);

        // 物品3：绿色圆形（带边框加粗效果）
        renderer.setSeriesShape(2, SHAPE_ITEM3);
        renderer.setSeriesPaint(2, COLOR_ITEM3);
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesShapesFilled(2, true);

        // 设置提示框（显示具体数值）
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator(
                "{0}: ({1}, {2})",
                new DecimalFormat("#.##"),
                new DecimalFormat("#.##")
        ));

        plot.setRenderer(renderer);

        // 设置图例样式
        chart.getLegend().setItemFont(new Font("微软雅黑", Font.PLAIN, 11));
        chart.getLegend().setBackgroundPaint(new Color(255, 255, 255, 200));
        chart.getLegend().setBorder(0, 0, 0, 0);

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 450);
            }
        };
        chartPanel.setMouseWheelEnabled(true);  // 支持鼠标滚轮缩放
        chartPanel.setMouseZoomable(true);      // 支持鼠标缩放

        return chartPanel;
    }

    /**
     * 创建散点图（简化版，带统计信息）
     *
     * @param itemSets 项集列表
     * @param title 图表标题
     * @param capacity 背包容量（用于显示在副标题）
     * @return JPanel 包含图表面板
     */
    public static JPanel createScatterPlotWithStats(List<ItemSet> itemSets, String title, int capacity) {
        JPanel panel = createScatterPlot(itemSets, title);

        // 可以在图表上添加副标题显示统计信息
        // 由于JFreeChart的限制，这里简单返回原面板
        return panel;
    }
}