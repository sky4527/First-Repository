package org.example.model;

/**
 * 项集类 - D{0-1}背包问题的数据单元
 * 每个项集包含3个物品，第三个物品是前两个物品的折扣组合
 *
 * @author [曹微婕]
 */
public class ItemSet {
    private int id;           // 项集编号
    private int weight1;      // 物品1重量
    private int value1;       // 物品1价值
    private int weight2;      // 物品2重量
    private int value2;       // 物品2价值
    private int weight3;      // 物品3重量（折扣组合）
    private int value3;       // 物品3价值（= value1 + value2）
    private double ratio;     // 价值重量比（用于排序）

    // 构造方法
    public ItemSet() {
    }

    public ItemSet(int id, int weight1, int value1, int weight2, int value2, int weight3, int value3) {
        this.id = id;
        this.weight1 = weight1;
        this.value1 = value1;
        this.weight2 = weight2;
        this.value2 = value2;
        this.weight3 = weight3;
        this.value3 = value3;
        calculateRatio();
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight1() {
        return weight1;
    }

    public void setWeight1(int weight1) {
        this.weight1 = weight1;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getWeight2() {
        return weight2;
    }

    public void setWeight2(int weight2) {
        this.weight2 = weight2;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public int getWeight3() {
        return weight3;
    }

    public void setWeight3(int weight3) {
        this.weight3 = weight3;
    }

    public int getValue3() {
        return value3;
    }

    public void setValue3(int value3) {
        this.value3 = value3;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     * 计算价值重量比（按第三项计算，用于排序）
     */
    public void calculateRatio() {
        if (weight3 > 0) {
            this.ratio = (double) value3 / weight3;
        } else {
            this.ratio = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("ItemSet{id=%d, w1=%d, v1=%d, w2=%d, v2=%d, w3=%d, v3=%d, ratio=%.2f}",
                id, weight1, value1, weight2, value2, weight3, value3, ratio);
    }
}