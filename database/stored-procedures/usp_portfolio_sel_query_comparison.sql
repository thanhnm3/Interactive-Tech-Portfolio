/*
 * Author : Portfolio Team
 * Create date : 2024/01/14
 * Description : Compare query performance between optimized and unoptimized versions
 * Use Tables : query_history
 * Transaction : No
 * Modified Histories:
 * 2024/01/14: Initial creation
 */

-- Drop function if exists
DROP FUNCTION IF EXISTS usp_portfolio_sel_query_comparison(TEXT, TEXT);

/**
 * Compare performance of two query patterns
 * @param p_unoptimized_hash - hash of unoptimized query
 * @param p_optimized_hash - hash of optimized query
 * @returns TABLE with comparison metrics
 */
CREATE OR REPLACE FUNCTION usp_portfolio_sel_query_comparison(
    p_unoptimized_hash TEXT,
    p_optimized_hash TEXT
)
RETURNS TABLE (
    metric_name TEXT,
    unoptimized_value NUMERIC,
    optimized_value NUMERIC,
    improvement_percentage NUMERIC,
    recommendation TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_unopt_avg_time NUMERIC;
    v_opt_avg_time NUMERIC;
    v_unopt_avg_rows_examined NUMERIC;
    v_opt_avg_rows_examined NUMERIC;
    v_unopt_index_usage NUMERIC;
    v_opt_index_usage NUMERIC;
    v_time_improvement NUMERIC;
    v_rows_improvement NUMERIC;
BEGIN
    -- Calculate average metrics for unoptimized query
    SELECT 
        COALESCE(AVG(execution_time_ms), 0),
        COALESCE(AVG(rows_examined), 0),
        COALESCE(AVG(CASE WHEN used_index THEN 1 ELSE 0 END) * 100, 0)
    INTO v_unopt_avg_time, v_unopt_avg_rows_examined, v_unopt_index_usage
    FROM query_history
    WHERE query_hash = p_unoptimized_hash;
    
    -- Calculate average metrics for optimized query
    SELECT 
        COALESCE(AVG(execution_time_ms), 0),
        COALESCE(AVG(rows_examined), 0),
        COALESCE(AVG(CASE WHEN used_index THEN 1 ELSE 0 END) * 100, 0)
    INTO v_opt_avg_time, v_opt_avg_rows_examined, v_opt_index_usage
    FROM query_history
    WHERE query_hash = p_optimized_hash;
    
    -- Calculate improvements
    IF v_unopt_avg_time > 0 THEN
        v_time_improvement := ((v_unopt_avg_time - v_opt_avg_time) / v_unopt_avg_time) * 100;
    ELSE
        v_time_improvement := 0;
    END IF;
    
    IF v_unopt_avg_rows_examined > 0 THEN
        v_rows_improvement := ((v_unopt_avg_rows_examined - v_opt_avg_rows_examined) / v_unopt_avg_rows_examined) * 100;
    ELSE
        v_rows_improvement := 0;
    END IF;
    
    -- Return execution time comparison
    RETURN QUERY SELECT 
        'Average Execution Time (ms)'::TEXT,
        v_unopt_avg_time,
        v_opt_avg_time,
        v_time_improvement,
        CASE 
            WHEN v_time_improvement > 50 THEN 'Excellent improvement - optimization is highly effective'
            WHEN v_time_improvement > 20 THEN 'Good improvement - consider additional optimizations'
            WHEN v_time_improvement > 0 THEN 'Moderate improvement - may need further tuning'
            ELSE 'No improvement - review optimization strategy'
        END;
    
    -- Return rows examined comparison
    RETURN QUERY SELECT 
        'Average Rows Examined'::TEXT,
        v_unopt_avg_rows_examined,
        v_opt_avg_rows_examined,
        v_rows_improvement,
        CASE 
            WHEN v_rows_improvement > 80 THEN 'Index is being used effectively'
            WHEN v_rows_improvement > 50 THEN 'Good reduction in rows examined'
            WHEN v_rows_improvement > 0 THEN 'Some improvement, consider composite indexes'
            ELSE 'No reduction - verify index is covering query conditions'
        END;
    
    -- Return index usage comparison
    RETURN QUERY SELECT 
        'Index Usage Rate (%)'::TEXT,
        v_unopt_index_usage,
        v_opt_index_usage,
        v_opt_index_usage - v_unopt_index_usage,
        CASE 
            WHEN v_opt_index_usage >= 100 THEN 'Optimal - index is always used'
            WHEN v_opt_index_usage >= 80 THEN 'Good - index is mostly used'
            WHEN v_opt_index_usage >= 50 THEN 'Fair - review query patterns'
            ELSE 'Poor - index may not match query predicates'
        END;
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION usp_portfolio_sel_query_comparison(TEXT, TEXT) TO PUBLIC;

-- Add comment
COMMENT ON FUNCTION usp_portfolio_sel_query_comparison(TEXT, TEXT) IS 
'Compares performance metrics between optimized and unoptimized query versions';

-- =====================================================
-- Additional utility functions for query analysis
-- =====================================================

/**
 * Get slow queries from history
 * @param p_threshold_ms - minimum execution time threshold
 * @param p_limit - maximum number of results
 * @returns TABLE with slow query details
 */
CREATE OR REPLACE FUNCTION usp_portfolio_sel_slow_queries(
    p_threshold_ms BIGINT DEFAULT 1000,
    p_limit INTEGER DEFAULT 10
)
RETURNS TABLE (
    query_hash VARCHAR(64),
    avg_execution_time_ms NUMERIC,
    execution_count BIGINT,
    avg_rows_examined NUMERIC,
    index_usage_rate NUMERIC,
    sample_query TEXT,
    last_executed TIMESTAMP
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        qh.query_hash,
        AVG(qh.execution_time_ms)::NUMERIC,
        COUNT(*)::BIGINT,
        AVG(qh.rows_examined)::NUMERIC,
        (AVG(CASE WHEN qh.used_index THEN 1 ELSE 0 END) * 100)::NUMERIC,
        MAX(qh.query_text),
        MAX(qh.created_at)
    FROM query_history qh
    GROUP BY qh.query_hash
    HAVING AVG(qh.execution_time_ms) >= p_threshold_ms
    ORDER BY AVG(qh.execution_time_ms) DESC
    LIMIT p_limit;
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION usp_portfolio_sel_slow_queries(BIGINT, INTEGER) TO PUBLIC;

-- Add comment
COMMENT ON FUNCTION usp_portfolio_sel_slow_queries(BIGINT, INTEGER) IS 
'Retrieves slow queries that exceed the specified execution time threshold';

/**
 * Log query execution for performance tracking
 * @param p_query_text - the SQL query text
 * @param p_execution_time_ms - execution time in milliseconds
 * @param p_rows_examined - number of rows examined
 * @param p_rows_returned - number of rows returned
 * @param p_used_index - whether an index was used
 * @param p_index_name - name of index used (if any)
 * @param p_execution_plan - JSONB execution plan
 */
CREATE OR REPLACE FUNCTION usp_portfolio_ins_query_history(
    p_query_text TEXT,
    p_execution_time_ms BIGINT,
    p_rows_examined BIGINT DEFAULT NULL,
    p_rows_returned BIGINT DEFAULT NULL,
    p_used_index BOOLEAN DEFAULT false,
    p_index_name VARCHAR(100) DEFAULT NULL,
    p_execution_plan JSONB DEFAULT NULL
)
RETURNS UUID
LANGUAGE plpgsql
AS $$
DECLARE
    v_query_hash VARCHAR(64);
    v_new_id UUID;
BEGIN
    -- Generate hash of query for grouping
    v_query_hash := encode(sha256(p_query_text::bytea), 'hex');
    
    -- Insert into query history
    INSERT INTO query_history (
        query_text,
        query_hash,
        execution_time_ms,
        rows_examined,
        rows_returned,
        used_index,
        index_name,
        execution_plan,
        created_at
    ) VALUES (
        p_query_text,
        v_query_hash,
        p_execution_time_ms,
        p_rows_examined,
        p_rows_returned,
        p_used_index,
        p_index_name,
        p_execution_plan,
        CURRENT_TIMESTAMP
    )
    RETURNING id INTO v_new_id;
    
    RETURN v_new_id;
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION usp_portfolio_ins_query_history(TEXT, BIGINT, BIGINT, BIGINT, BOOLEAN, VARCHAR, JSONB) TO PUBLIC;

-- Add comment
COMMENT ON FUNCTION usp_portfolio_ins_query_history(TEXT, BIGINT, BIGINT, BIGINT, BOOLEAN, VARCHAR, JSONB) IS 
'Logs query execution details for performance analysis';
