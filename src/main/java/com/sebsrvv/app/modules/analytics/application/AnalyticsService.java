// src/main/java/com/sebsrvv/app/modules/analytics/application/AnalyticsService.java
package com.sebsrvv.app.modules.analytics.application;

import com.sebsrvv.app.modules.analytics.web.dto.TrendsResponse;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final SupabaseDataClient data;

    public AnalyticsService(SupabaseDataClient data) {
        this.data = data;
    }

    /**
     * Obtiene series por bucket (week/month) desde la RPC y calcula una media móvil opcional.
     */
    public TrendsResponse getTrends(
            String userId,
            String bearer,
            String period,           // weekly | monthly
            String metric,           // calories | protein_g | carbs_g | fat_g | goalAdherence
            LocalDate from,          // opcional
            LocalDate to,            // opcional
            Integer movingAvg        // tamaño ventana en buckets (opcional)
    ) {
        // Normaliza fechas (si son nulas, usa el mes actual)
        LocalDate effFrom = from;
        LocalDate effTo = to;
        if (effFrom == null && effTo == null) {
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            effFrom = today.withDayOfMonth(1);
            effTo = today;
        } else if (effFrom != null && effTo == null) {
            effTo = effFrom;
        } else if (effFrom == null) {
            effFrom = effTo;
        }

        Map<String, Object> args = new HashMap<>();
        args.put("p_user_id", UUID.fromString(userId));
        args.put("p_metric", metric);
        args.put("p_period", period);
        args.put("p_from", effFrom.toString()); // date
        args.put("p_to", effTo.toString());     // date

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) data.rpcWithAuth("analytics_trends", args, bearer).block();

        List<TrendsResponse.Point> series = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> r : rows) {
                String bucket = String.valueOf(r.get("bucket"));
                Double value = r.get("value") == null ? 0d : ((Number) r.get("value")).doubleValue();
                series.add(new TrendsResponse.Point(bucket, value));
            }
        }

        // Ordenar por fecha asc (bucket es YYYY-MM-DD)
        series = series.stream()
                .sorted(Comparator.comparing(TrendsResponse.Point::getBucket))
                .collect(Collectors.toList());

        List<TrendsResponse.Point> ma = Collections.emptyList();
        if (movingAvg != null && movingAvg > 0 && series.size() >= movingAvg) {
            ma = movingAverage(series, movingAvg);
        }

        return new TrendsResponse(period, metric, series, ma);
    }

    private List<TrendsResponse.Point> movingAverage(List<TrendsResponse.Point> series, int window) {
        List<TrendsResponse.Point> out = new ArrayList<>();
        double sum = 0;
        Deque<Double> q = new ArrayDeque<>();
        for (int i = 0; i < series.size(); i++) {
            double v = series.get(i).getValue() == null ? 0d : series.get(i).getValue();
            sum += v;
            q.addLast(v);
            if (q.size() > window) sum -= q.removeFirst();
            if (q.size() == window) {
                double avg = sum / window;
                out.add(new TrendsResponse.Point(series.get(i).getBucket(), avg));
            }
        }
        return out;
    }
}
