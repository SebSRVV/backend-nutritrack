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

/**
 * Implementación del repositorio MealRepository utilizando Supabase como backend de datos.
 * Esta clase se encarga de realizar las operaciones CRUD y de consulta sobre las comidas (Meal)
 * comunicándose directamente con Supabase mediante SupabaseDataClient.
 */
@Repository
public class SupabaseMealRepository implements MealRepository {

    private final SupabaseDataClient data; // Cliente para interactuar con Supabase

    public SupabaseMealRepository(SupabaseDataClient data) {
        this.data = data;
    }

    // ---------- INSERT ----------
    /**
     * Inserta una nueva comida en la base de datos junto con sus categorías.
     */
    @Override
    public Meal insert(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        // Convierte el objeto Meal a un mapa compatible con la estructura de Supabase
        Map<String, Object> row = toMealRow(meal);

        // Inserta en la tabla 'meal_logs' y obtiene la fila resultante
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> inserted =
                (List<Map<String, Object>>) data.insertWithAuth("meal_logs", row, bearer).block();

        if (inserted == null || inserted.isEmpty()) {
            throw new IllegalStateException("No se insertó meal_logs");
        }

        // Obtiene el ID generado para la nueva comida
        UUID mealId = UUID.fromString(String.valueOf(inserted.get(0).get("id")));

        // Une las categorías por ID y por nombre (las crea si no existen)
        List<Integer> allCatIds = new ArrayList<>();
        if (categoryIds != null) allCatIds.addAll(categoryIds);
        if (categoryNames != null && !categoryNames.isEmpty()) {
            allCatIds.addAll(upsertCategoriesByName(categoryNames, bearer));
        }

        // Inserta las relaciones entre la comida y sus categorías
        if (!allCatIds.isEmpty()) {
            for (Integer catId : allCatIds.stream().distinct().collect(Collectors.toList())) {
                Map<String, Object> link = new HashMap<>();
                link.put("meal_log_id", mealId);
                link.put("category_id", catId);
                data.insertWithAuth("meal_log_categories", link, bearer).block();
            }
        }

        // Retorna la comida recién insertada desde la base de datos
        return findById(mealId, bearer).orElseThrow();
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza los datos de una comida existente y sus categorías relacionadas.
     */
    @Override
    public Meal update(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        Map<String, Object> row = toMealRow(meal);
        row.remove("id"); // El ID no se debe modificar en la actualización

        // Actualiza la fila en la tabla principal
        data.patchWithAuth("meal_logs", "id=eq." + meal.getId(), row, bearer).block();

        // Elimina las categorías anteriores para volver a insertarlas
        data.deleteWithAuth("meal_log_categories", "meal_log_id=eq." + meal.getId(), bearer).block();

        // Prepara las nuevas categorías a asociar
        List<Integer> allCatIds = new ArrayList<>();
        if (categoryIds != null) allCatIds.addAll(categoryIds);
        if (categoryNames != null && !categoryNames.isEmpty()) {
            allCatIds.addAll(upsertCategoriesByName(categoryNames, bearer));
        }

        // Inserta nuevamente las asociaciones entre comida y categorías
        if (!allCatIds.isEmpty()) {
            for (Integer catId : allCatIds.stream().distinct().collect(Collectors.toList())) {
                Map<String, Object> link = new HashMap<>();
                link.put("meal_log_id", meal.getId());
                link.put("category_id", catId);
                data.insertWithAuth("meal_log_categories", link, bearer).block();
            }
        }

        // Retorna la versión actualizada del registro
        return findById(meal.getId(), bearer).orElseThrow();
    }

    // ---------- SELECT by ID ----------
    /**
     * Busca una comida específica por su ID, incluyendo sus categorías asociadas.
     */
    @Override
    public Optional<Meal> findById(UUID mealId, String bearer) {
        String qp = String.join("&",
                "id=eq." + mealId,
                "select=*,meal_log_categories(*,food_categories(*))", // Incluye relaciones anidadas
                "limit=1"
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.selectWithAuth("meal_logs", qp, bearer).block();

        if (rows == null || rows.isEmpty()) return Optional.empty();
        return Optional.of(mapMeal(rows.get(0))); // Mapea los datos al objeto Meal
    }

    // ---------- DELETE ----------
    /**
     * Elimina una comida y todas sus relaciones con categorías.
     */
    @Override
    public void delete(UUID mealId, String bearer) {
        // Primero elimina las relaciones
        data.deleteWithAuth("meal_log_categories", "meal_log_id=eq." + mealId, bearer).block();
        // Luego elimina el registro principal
        data.deleteWithAuth("meal_logs", "id=eq." + mealId, bearer).block();
    }

    // ---------- SELECT by user + date ----------
    /**
     * Devuelve todas las comidas registradas por un usuario en una fecha específica.
     */
    @Override
    public List<Meal> findByUserAndDate(UUID userId, LocalDate date, String bearer) {
        if (date == null) {
            throw new IllegalArgumentException("date no puede ser null. Usa findByUserAndDateRange.");
        }

        // Define rango horario para la fecha (de 00:00 a 23:59)
        String from = date.atStartOfDay().atOffset(ZoneOffset.UTC).toString();
        String to   = date.atTime(23,59,59).atOffset(ZoneOffset.UTC).toString();

        String qp = String.join("&",
                "user_id=eq." + userId,
                "logged_at=gte." + from,
                "logged_at=lte." + to,
                "select=*,meal_log_categories(*,food_categories(*))",
                "order=logged_at.asc"
        );

        // Ejecuta la consulta en Supabase
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.selectWithAuth("meal_logs", qp, bearer).block();

        if (rows == null) return List.of();
        List<Meal> result = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            result.add(mapMeal(r)); // Mapea cada fila a un objeto Meal
        }
        return result;
    }

    /**
     * Obtiene comidas de un usuario dentro de un rango de fechas.
     */
    @Override
    public List<Meal> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to, String bearer) {
        if (userId == null) throw new IllegalArgumentException("userId no puede ser null");
        if (from == null || to == null) throw new IllegalArgumentException("from/to no pueden ser null");

        String fromStr = from.atStartOfDay().atOffset(ZoneOffset.UTC).toString();
        String toStr   = to.atTime(23, 59, 59).atOffset(ZoneOffset.UTC).toString();

        String qp = String.join("&",
                "user_id=eq." + userId,
                "logged_at=gte." + fromStr,
                "logged_at=lte." + toStr,
                "select=*,meal_log_categories(*,food_categories(*))",
                "order=logged_at.asc"
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.selectWithAuth("meal_logs", qp, bearer).block();

        if (rows == null) return List.of();
        List<Meal> result = new ArrayList<>();
        for (Map<String, Object> r : rows) result.add(mapMeal(r));
        return result;
    }

    // ---------- ALL categories ----------
    /**
     * Obtiene todas las categorías de comida registradas.
     */
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
    /**
     * Convierte un objeto Meal en un mapa de campos compatible con Supabase.
     */
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
        row.put("logged_at", m.getLoggedAt());
        return row;
    }

    /**
     * Convierte un registro de Supabase en un objeto Meal.
     */
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

        // Mapea las categorías asociadas (relación muchos a muchos)
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

    // ---------- Conversores básicos ----------
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
     * Inserta o devuelve los IDs de categorías según sus nombres.
     * Si una categoría no existe, la crea automáticamente.
     */
    private List<Integer> upsertCategoriesByName(List<String> names, String bearer) {
        List<Integer> ids = new ArrayList<>();
        for (String name : names) {
            // Busca si ya existe la categoría
            String qp = "name=eq." + encodeEquals(name);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows =
                    (List<Map<String, Object>>) data.selectWithAuth("food_categories", qp, bearer).block();

            if (rows != null && !rows.isEmpty()) {
                ids.add(asInt(rows.get(0).get("id")));
                continue;
            }

            // Si no existe, la crea
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
     * Codifica correctamente los nombres para ser usados en filtros de consulta Supabase.
     */
    private String encodeEquals(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\\\"");
        if (escaped.matches("^[A-Za-z0-9_]+$")) return escaped;
        return "\"" + escaped + "\"";
    }
}
