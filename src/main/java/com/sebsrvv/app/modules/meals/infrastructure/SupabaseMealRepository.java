// com/sebsrvv/app/modules/meals/infrastructure/SupabaseMealRepository.java
package com.sebsrvv.app.modules.meals.infrastructure;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealCategory;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SupabaseMealRepository implements MealRepository {

    private final SupabaseDataClient data;

    public SupabaseMealRepository(SupabaseDataClient data) {
        this.data = data;
    }

    // ---------- INSERT ----------
    @Override
    public Meal insert(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        Map<String, Object> row = toMealRow(meal);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> inserted =
                (List<Map<String, Object>>) data.insertWithAuth("meal_logs", row, bearer).block();

        if (inserted == null || inserted.isEmpty()) {
            throw new IllegalStateException("No se insertó meal_logs");
        }

        Map<String, Object> mealRow = inserted.get(0);
        UUID mealId = UUID.fromString(String.valueOf(mealRow.get("id")));

        // Resolver categorías: ids existentes + crear por nombre si vienen
        List<Integer> allCatIds = new ArrayList<>();
        if (categoryIds != null) allCatIds.addAll(categoryIds);
        if (categoryNames != null && !categoryNames.isEmpty()) {
            allCatIds.addAll(upsertCategoriesByName(categoryNames, bearer));
        }

        // Vincular en meal_log_categories
        if (!allCatIds.isEmpty()) {
            for (Integer catId : allCatIds.stream().distinct().collect(Collectors.toList())) {
                Map<String, Object> link = new HashMap<>();
                link.put("meal_log_id", mealId);
                link.put("category_id", catId);
                data.insertWithAuth("meal_log_categories", link, bearer).block();
            }
        }

        return findById(mealId, bearer).orElseThrow();
    }

    // ---------- UPDATE ----------
    @Override
    public Meal update(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        Map<String, Object> row = toMealRow(meal);
        // Para PATCH no enviamos el id en el body
        row.remove("id");

        // PATCH con RLS
        data.patchWithAuth("meal_logs", "id=eq." + meal.getId(), row, bearer).block();

        // Reemplazar categorías: borrar links y volver a insertar
        data.deleteWithAuth("meal_log_categories", "meal_log_id=eq." + meal.getId(), bearer).block();

        List<Integer> allCatIds = new ArrayList<>();
        if (categoryIds != null) allCatIds.addAll(categoryIds);
        if (categoryNames != null && !categoryNames.isEmpty()) {
            allCatIds.addAll(upsertCategoriesByName(categoryNames, bearer));
        }

        if (!allCatIds.isEmpty()) {
            for (Integer catId : allCatIds.stream().distinct().collect(Collectors.toList())) {
                Map<String, Object> link = new HashMap<>();
                link.put("meal_log_id", meal.getId());
                link.put("category_id", catId);
                data.insertWithAuth("meal_log_categories", link, bearer).block();
            }
        }

        return findById(meal.getId(), bearer).orElseThrow();
    }

    // ---------- SELECT by ID ----------
    @Override
    public Optional<Meal> findById(UUID mealId, String bearer) {
        String qp = String.join("&",
                "id=eq." + mealId,
                "select=*,meal_log_categories(*,food_categories(*))",
                "limit=1"
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.selectWithAuth("meal_logs", qp, bearer).block();

        if (rows == null || rows.isEmpty()) return Optional.empty();
        return Optional.of(mapMeal(rows.get(0)));
    }

    // ---------- DELETE ----------
    @Override
    public void delete(UUID mealId, String bearer) {
        data.deleteWithAuth("meal_log_categories", "meal_log_id=eq." + mealId, bearer).block();
        data.deleteWithAuth("meal_logs", "id=eq." + mealId, bearer).block();
    }

    // ---------- SELECT by user + date ----------
    @Override
    public List<Meal> findByUserAndDate(UUID userId, LocalDate date, String bearer) {
        String from = date.atStartOfDay().atOffset(ZoneOffset.UTC).toString();
        String to   = date.atTime(23,59,59).atOffset(ZoneOffset.UTC).toString();

        String qp = String.join("&",
                "user_id=eq." + userId,
                "logged_at=gte." + from,
                "logged_at=lte." + to,
                "select=*,meal_log_categories(*,food_categories(*))",
                "order=logged_at.asc"
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.selectWithAuth("meal_logs", qp, bearer).block();

        if (rows == null) return List.of();
        List<Meal> result = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            result.add(mapMeal(r));
        }
        return result;
    }

    // ---------- ALL categories ----------
    @Override
    public List<MealCategory> findAllCategories(String bearer) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.selectWithAuth("food_categories", null, bearer).block();

        if (rows == null) return List.of();
        List<MealCategory> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            MealCategory c = new MealCategory();
            c.setId(asInt(r.get("id")));
            c.setName(String.valueOf(r.get("name")));
            c.setDescription((String) r.get("description"));
            out.add(c);
        }
        return out;
    }

    // ---------- Helpers ----------

    private Map<String, Object> toMealRow(Meal m) {
        Map<String, Object> row = new HashMap<>();
        if (m.getId() != null) row.put("id", m.getId());
        row.put("user_id", m.getUserId());
        row.put("meal_type", m.getMealType().toDbValue());
        row.put("description", m.getDescription());
        row.put("calories", m.getCalories());
        row.put("protein_g", m.getProteinG());
        row.put("carbs_g", m.getCarbsG());
        row.put("fat_g", m.getFatG());
        row.put("logged_at", m.getLoggedAt());  // Instant -> timestamptz
        return row;
    }

    private Meal mapMeal(Map<String, Object> r) {
        Meal m = new Meal();
        m.setId(UUID.fromString(String.valueOf(r.get("id"))));
        m.setUserId(UUID.fromString(String.valueOf(r.get("user_id"))));
        m.setMealType(MealType.fromDbValue(String.valueOf(r.get("meal_type"))));
        m.setDescription((String) r.get("description"));
        m.setCalories(asInt(r.get("calories")));
        m.setProteinG(asDouble(r.get("protein_g")));
        m.setCarbsG(asDouble(r.get("carbs_g")));
        m.setFatG(asDouble(r.get("fat_g")));
        m.setLoggedAt(r.get("logged_at") != null ? Instant.parse(String.valueOf(r.get("logged_at"))) : null);
        m.setCreatedAt(r.get("created_at") != null ? Instant.parse(String.valueOf(r.get("created_at"))) : null);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> links = (List<Map<String, Object>>) r.get("meal_log_categories");
        if (links != null) {
            List<MealCategory> cats = new ArrayList<>();
            for (Map<String, Object> link : links) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fc = (Map<String, Object>) link.get("food_categories");
                if (fc != null) {
                    MealCategory c = new MealCategory();
                    c.setId(asInt(fc.get("id")));
                    c.setName(String.valueOf(fc.get("name")));
                    c.setDescription((String) fc.get("description"));
                    cats.add(c);
                }
            }
            m.setCategories(cats);
        }
        return m;
    }

    private double asDouble(Object o) {
        if (o == null) return 0d;
        if (o instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(o));
    }

    private int asInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(String.valueOf(o));
    }

    /**
     * Crea (o encuentra) categorías por nombre respetando RLS.
     * Estrategia: SELECT por nombre; si no existe -> INSERT con JWT del usuario.
     */
    private List<Integer> upsertCategoriesByName(List<String> names, String bearer) {
        List<Integer> ids = new ArrayList<>();
        for (String name : names) {
            // 1) buscar si existe
            String qp = "name=eq." + encodeEquals(name);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows =
                    (List<Map<String, Object>>) data.selectWithAuth("food_categories", qp, bearer).block();
            if (rows != null && !rows.isEmpty()) {
                ids.add(asInt(rows.get(0).get("id")));
                continue;
            }

            // 2) insertar si no existe
            Map<String, Object> row = new HashMap<>();
            row.put("name", name);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> inserted =
                    (List<Map<String, Object>>) data.insertWithAuth("food_categories", row, bearer).block();
            if (inserted != null && !inserted.isEmpty()) {
                ids.add(asInt(inserted.get(0).get("id")));
            }
        }
        return ids;
    }

    /**
     * Encapsula valores con comillas si tienen espacios o caracteres especiales en filtros eq.
     * Ej: Protein Shake -> "Protein Shake"
     */
    private String encodeEquals(String value) {
        if (value == null) return "";
        // Comillas dobles en PostgREST para literales con espacios o signos
        String escaped = value.replace("\"", "\\\"");
        if (escaped.matches("^[A-Za-z0-9_]+$")) return escaped;
        return "\"" + escaped + "\"";
    }
}
