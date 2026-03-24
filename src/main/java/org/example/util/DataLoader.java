package org.example.util;

import org.example.model.ItemSet;
import java.io.*;
import java.util.*;

/**
 * 数据加载工具类
 * 负责从文件中读取D{0-1}KP实例数据
 *
 * @author [曹微婕]
 */
public class DataLoader {

    /**
     * 从文件加载数据
     *
     * @param filePath 文件路径
     * @return 项集列表
     */
    public static List<ItemSet> loadData(String filePath) {
        List<ItemSet> itemSets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int setId = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // 跳过空行和注释行
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                // 解析数据行（格式：w1 v1 w2 v2 w3 v3）
                String[] parts = line.split("\\s+");
                if (parts.length >= 6) {
                    ItemSet set = new ItemSet();
                    set.setId(setId++);
                    set.setWeight1(Integer.parseInt(parts[0]));
                    set.setValue1(Integer.parseInt(parts[1]));
                    set.setWeight2(Integer.parseInt(parts[2]));
                    set.setValue2(Integer.parseInt(parts[3]));
                    set.setWeight3(Integer.parseInt(parts[4]));
                    set.setValue3(Integer.parseInt(parts[5]));

                    // 验证数据有效性
                    if (set.getValue3() != set.getValue1() + set.getValue2()) {
                        System.err.println("警告：第" + setId + "项集价值不符合定义（v3 != v1+v2）");
                    }

                    if (set.getWeight3() >= set.getWeight1() + set.getWeight2()) {
                        System.err.println("警告：第" + setId + "项集重量不符合折扣条件（w3 >= w1+w2）");
                    }

                    set.calculateRatio();
                    itemSets.add(set);
                }
            }

            System.out.println("成功加载 " + itemSets.size() + " 个项集");

        } catch (IOException e) {
            System.err.println("文件读取失败：" + e.getMessage());
            e.printStackTrace();
        }

        return itemSets;
    }

    /**
     * 获取背包容量（从文件末尾读取，若没有则返回默认值）
     *
     * @param filePath 文件路径
     * @param defaultCapacity 默认容量
     * @return 背包容量
     */
    public static int getCapacity(String filePath, int defaultCapacity) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String lastLine = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) {
                    lastLine = line;
                }
            }

            if (lastLine != null) {
                String[] parts = lastLine.split("\\s+");
                if (parts.length >= 7) {
                    return Integer.parseInt(parts[6]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return defaultCapacity;
    }
}