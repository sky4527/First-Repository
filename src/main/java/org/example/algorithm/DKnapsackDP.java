package org.example.algorithm;

import org.example.model.ItemSet;
import java.util.*;

/**
 * D{0-1}背包问题动态规划求解器
 * 每个项集有三种选择：不选、选物品1、选物品2、选物品3
 *
 * @author [你的名字]
 */
public class DKnapsackDP {

    /**
     * 求解结果内部类
     */
    public static class Result {
        private int maxValue;                    // 最大价值
        private List<Integer> selectedIndexes;   // 选择的项集索引
        private List<Integer> selectedTypes;     // 选择的物品类型（1/2/3）
        private double timeMs;                   // 求解时间（毫秒）

        public Result(int maxValue, List<Integer> selectedIndexes, List<Integer> selectedTypes, double timeMs) {
            this.maxValue = maxValue;
            this.selectedIndexes = selectedIndexes;
            this.selectedTypes = selectedTypes;
            this.timeMs = timeMs;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public List<Integer> getSelectedIndexes() {
            return selectedIndexes;
        }

        public List<Integer> getSelectedTypes() {
            return selectedTypes;
        }

        public double getTimeMs() {
            return timeMs;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("最大价值: ").append(maxValue).append("\n");
            sb.append("求解时间: ").append(String.format("%.3f", timeMs)).append(" ms\n");
            sb.append("选择的项集: ");
            for (int i = 0; i < selectedIndexes.size(); i++) {
                sb.append("(项集").append(selectedIndexes.get(i))
                        .append(", 选物品").append(selectedTypes.get(i)).append(") ");
            }
            return sb.toString();
        }
    }

    /**
     * 动态规划求解
     *
     * @param itemSets 项集列表
     * @param capacity 背包容量
     * @return 求解结果
     */
    public static Result solve(List<ItemSet> itemSets, int capacity) {
        int n = itemSets.size();

        // dp[i][j] 表示前i个项集，容量j时的最大价值
        int[][] dp = new int[n + 1][capacity + 1];

        // 记录选择路径：0-不选，1-选物品1，2-选物品2，3-选物品3
        int[][] choice = new int[n + 1][capacity + 1];

        long startTime = System.nanoTime();

        for (int i = 1; i <= n; i++) {
            ItemSet set = itemSets.get(i - 1);
            int w1 = set.getWeight1();
            int v1 = set.getValue1();
            int w2 = set.getWeight2();
            int v2 = set.getValue2();
            int w3 = set.getWeight3();
            int v3 = set.getValue3();

            for (int j = 0; j <= capacity; j++) {
                // 默认不选当前项集
                dp[i][j] = dp[i - 1][j];
                choice[i][j] = 0;

                // 尝试选择物品1
                if (j >= w1) {
                    int val = dp[i - 1][j - w1] + v1;
                    if (val > dp[i][j]) {
                        dp[i][j] = val;
                        choice[i][j] = 1;
                    }
                }

                // 尝试选择物品2
                if (j >= w2) {
                    int val = dp[i - 1][j - w2] + v2;
                    if (val > dp[i][j]) {
                        dp[i][j] = val;
                        choice[i][j] = 2;
                    }
                }

                // 尝试选择物品3（折扣组合）
                if (j >= w3) {
                    int val = dp[i - 1][j - w3] + v3;
                    if (val > dp[i][j]) {
                        dp[i][j] = val;
                        choice[i][j] = 3;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        // 回溯找出选择的项
        List<Integer> selectedIndexes = new ArrayList<>();
        List<Integer> selectedTypes = new ArrayList<>();
        int remainingCap = capacity;

        for (int i = n; i > 0; i--) {
            int chosen = choice[i][remainingCap];
            if (chosen != 0) {
                selectedIndexes.add(i - 1);
                selectedTypes.add(chosen);

                ItemSet set = itemSets.get(i - 1);
                switch (chosen) {
                    case 1:
                        remainingCap -= set.getWeight1();
                        break;
                    case 2:
                        remainingCap -= set.getWeight2();
                        break;
                    case 3:
                        remainingCap -= set.getWeight3();
                        break;
                }
            }
        }

        // 反转顺序（回溯得到的是逆序）
        Collections.reverse(selectedIndexes);
        Collections.reverse(selectedTypes);

        return new Result(dp[n][capacity], selectedIndexes, selectedTypes, timeMs);
    }

    /**
     * 按价值重量比对项集进行非递增排序
     *
     * @param itemSets 项集列表
     */
    public static void sortByRatio(List<ItemSet> itemSets) {
        itemSets.sort((a, b) -> Double.compare(b.getRatio(), a.getRatio()));
    }
}