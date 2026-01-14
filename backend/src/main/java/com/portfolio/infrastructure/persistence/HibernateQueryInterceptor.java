package com.portfolio.infrastructure.persistence;

import com.portfolio.application.dto.SqlCaptureDto;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hibernate Statement Inspector for capturing generated SQL
 * Intercepts all SQL statements before execution
 */
@Component
public class HibernateQueryInterceptor implements StatementInspector {

    private static final int MAX_CAPTURED_QUERIES = 100;
    private final List<SqlCaptureDto> capturedQueries = new CopyOnWriteArrayList<>();
    private boolean isCapturing = false;

    /**
     * Inspect and optionally modify SQL statement before execution
     * @param sql - original SQL statement
     * @return String - SQL statement (potentially modified)
     */
    @Override
    public String inspect(String sql) {
        if (isCapturing && sql != null) {
            captureQuery(sql);
        }
        return sql;
    }

    /**
     * Capture SQL query with metadata
     * @param sql - SQL to capture
     */
    private void captureQuery(String sql) {
        SqlCaptureDto capture = new SqlCaptureDto.Builder()
            .sql(sql)
            .formattedSql(formatSql(sql))
            .operationType(detectOperationType(sql))
            .entityName(extractEntityName(sql))
            .build();

        capturedQueries.add(capture);

        // Maintain max size
        while (capturedQueries.size() > MAX_CAPTURED_QUERIES) {
            capturedQueries.remove(0);
        }
    }

    /**
     * Format SQL for better readability
     * @param sql - raw SQL
     * @return String - formatted SQL
     */
    private String formatSql(String sql) {
        if (sql == null) {
            return null;
        }

        // Add newlines before major keywords
        String formatted = sql
            .replaceAll("(?i)\\bSELECT\\b", "\nSELECT")
            .replaceAll("(?i)\\bFROM\\b", "\nFROM")
            .replaceAll("(?i)\\bWHERE\\b", "\nWHERE")
            .replaceAll("(?i)\\bAND\\b", "\n  AND")
            .replaceAll("(?i)\\bOR\\b", "\n  OR")
            .replaceAll("(?i)\\bINNER JOIN\\b", "\nINNER JOIN")
            .replaceAll("(?i)\\bLEFT JOIN\\b", "\nLEFT JOIN")
            .replaceAll("(?i)\\bRIGHT JOIN\\b", "\nRIGHT JOIN")
            .replaceAll("(?i)\\bON\\b", "\n  ON")
            .replaceAll("(?i)\\bGROUP BY\\b", "\nGROUP BY")
            .replaceAll("(?i)\\bORDER BY\\b", "\nORDER BY")
            .replaceAll("(?i)\\bHAVING\\b", "\nHAVING")
            .replaceAll("(?i)\\bLIMIT\\b", "\nLIMIT")
            .replaceAll("(?i)\\bOFFSET\\b", "\nOFFSET")
            .replaceAll("(?i)\\bINSERT INTO\\b", "\nINSERT INTO")
            .replaceAll("(?i)\\bVALUES\\b", "\nVALUES")
            .replaceAll("(?i)\\bUPDATE\\b", "\nUPDATE")
            .replaceAll("(?i)\\bSET\\b", "\nSET")
            .replaceAll("(?i)\\bDELETE FROM\\b", "\nDELETE FROM")
            .trim();

        return formatted;
    }

    /**
     * Detect SQL operation type
     * @param sql - SQL statement
     * @return String - operation type
     */
    private String detectOperationType(String sql) {
        if (sql == null) {
            return "UNKNOWN";
        }

        String upperSql = sql.trim().toUpperCase();

        if (upperSql.startsWith("SELECT")) {
            return "SELECT";
        } else if (upperSql.startsWith("INSERT")) {
            return "INSERT";
        } else if (upperSql.startsWith("UPDATE")) {
            return "UPDATE";
        } else if (upperSql.startsWith("DELETE")) {
            return "DELETE";
        } else if (upperSql.startsWith("CREATE")) {
            return "CREATE";
        } else if (upperSql.startsWith("ALTER")) {
            return "ALTER";
        } else if (upperSql.startsWith("DROP")) {
            return "DROP";
        }

        return "OTHER";
    }

    /**
     * Extract entity/table name from SQL
     * @param sql - SQL statement
     * @return String - entity name
     */
    private String extractEntityName(String sql) {
        if (sql == null) {
            return null;
        }

        // Pattern for FROM clause
        Pattern fromPattern = Pattern.compile("(?i)FROM\\s+(\\w+)");
        Matcher fromMatcher = fromPattern.matcher(sql);
        if (fromMatcher.find()) {
            return fromMatcher.group(1);
        }

        // Pattern for INSERT INTO
        Pattern insertPattern = Pattern.compile("(?i)INSERT\\s+INTO\\s+(\\w+)");
        Matcher insertMatcher = insertPattern.matcher(sql);
        if (insertMatcher.find()) {
            return insertMatcher.group(1);
        }

        // Pattern for UPDATE
        Pattern updatePattern = Pattern.compile("(?i)UPDATE\\s+(\\w+)");
        Matcher updateMatcher = updatePattern.matcher(sql);
        if (updateMatcher.find()) {
            return updateMatcher.group(1);
        }

        return null;
    }

    /**
     * Start capturing queries
     */
    public void startCapturing() {
        capturedQueries.clear();
        isCapturing = true;
    }

    /**
     * Stop capturing queries
     */
    public void stopCapturing() {
        isCapturing = false;
    }

    /**
     * Get captured queries
     * @return List - captured SQL queries
     */
    public List<SqlCaptureDto> getCapturedQueries() {
        return Collections.unmodifiableList(new ArrayList<>(capturedQueries));
    }

    /**
     * Clear captured queries
     */
    public void clearCapturedQueries() {
        capturedQueries.clear();
    }

    /**
     * Check if currently capturing
     * @return boolean - capturing status
     */
    public boolean isCapturing() {
        return isCapturing;
    }

    /**
     * Get the last captured query
     * @return SqlCaptureDto - last query or null
     */
    public SqlCaptureDto getLastCapturedQuery() {
        if (capturedQueries.isEmpty()) {
            return null;
        }
        return capturedQueries.get(capturedQueries.size() - 1);
    }

    /**
     * Get queries by operation type
     * @param operationType - operation type to filter
     * @return List - filtered queries
     */
    public List<SqlCaptureDto> getQueriesByType(String operationType) {
        return capturedQueries.stream()
            .filter(q -> operationType.equalsIgnoreCase(q.getOperationType()))
            .toList();
    }
}
