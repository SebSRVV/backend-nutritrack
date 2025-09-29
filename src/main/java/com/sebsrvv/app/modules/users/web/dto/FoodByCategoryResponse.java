package com.sebsrvv.app.modules.users.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class FoodByCategoryResponse {

    public static class Range {
        public String from;
        public String to;
        public String groupBy;
        public Range(String from, String to, String groupBy) {
            this.from = from; this.to = to; this.groupBy = groupBy;
        }
    }

    public static class Totals {
        public int calories;
        public BigDecimal protein_g;
        public BigDecimal carbs_g;
        public BigDecimal fat_g;
    }

    public static class Point {
        public String bucket;
        public int calories;
        public int count;
    }

    public static class CategorySeries {
        public int categoryId;
        public String name;
        public int calories;
        public int count;
        public List<Point> series;
    }

    public Range range;
    public Totals totals;
    public List<CategorySeries> categories;
}
