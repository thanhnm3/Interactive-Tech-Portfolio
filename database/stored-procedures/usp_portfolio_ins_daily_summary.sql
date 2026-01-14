/*
 * Author : Portfolio Team
 * Create date : 2024/01/14
 * Description : Generate daily summary report from orders and users
 * Use Tables : orders, order_items, users, products, categories, daily_summary
 * Transaction : Yes
 * Modified Histories:
 * 2024/01/14: Initial creation
 */

-- Drop function if exists
DROP FUNCTION IF EXISTS usp_portfolio_ins_daily_summary(DATE);

/**
 * Generate daily summary for specified date
 * Calculates revenue, order counts, and identifies top performers
 * @param p_summary_date - date to generate summary for
 * @returns void
 */
CREATE OR REPLACE FUNCTION usp_portfolio_ins_daily_summary(
    p_summary_date DATE DEFAULT CURRENT_DATE - INTERVAL '1 day'
)
RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_orders INTEGER;
    v_total_revenue DECIMAL(15, 2);
    v_total_items_sold INTEGER;
    v_new_users INTEGER;
    v_active_users INTEGER;
    v_avg_order_value DECIMAL(12, 2);
    v_top_category_id UUID;
    v_top_product_id UUID;
    v_start_timestamp TIMESTAMP;
    v_end_timestamp TIMESTAMP;
BEGIN
    -- Calculate timestamp range for the summary date
    v_start_timestamp := p_summary_date::TIMESTAMP;
    v_end_timestamp := (p_summary_date + INTERVAL '1 day')::TIMESTAMP;
    
    -- Calculate total orders and revenue
    SELECT 
        COALESCE(COUNT(*), 0),
        COALESCE(SUM(total_amount), 0)
    INTO v_total_orders, v_total_revenue
    FROM orders
    WHERE created_at >= v_start_timestamp 
      AND created_at < v_end_timestamp
      AND status NOT IN ('CANCELLED', 'REFUNDED');
    
    -- Calculate total items sold
    SELECT COALESCE(SUM(oi.quantity), 0)
    INTO v_total_items_sold
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE o.created_at >= v_start_timestamp 
      AND o.created_at < v_end_timestamp
      AND o.status NOT IN ('CANCELLED', 'REFUNDED');
    
    -- Count new users registered on this date
    SELECT COUNT(*)
    INTO v_new_users
    FROM users
    WHERE created_at >= v_start_timestamp 
      AND created_at < v_end_timestamp;
    
    -- Count active users (users who placed orders)
    SELECT COUNT(DISTINCT user_id)
    INTO v_active_users
    FROM orders
    WHERE created_at >= v_start_timestamp 
      AND created_at < v_end_timestamp
      AND user_id IS NOT NULL;
    
    -- Calculate average order value
    IF v_total_orders > 0 THEN
        v_avg_order_value := v_total_revenue / v_total_orders;
    ELSE
        v_avg_order_value := 0;
    END IF;
    
    -- Find top category by revenue
    SELECT p.category_id
    INTO v_top_category_id
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    JOIN products p ON oi.product_id = p.id
    WHERE o.created_at >= v_start_timestamp 
      AND o.created_at < v_end_timestamp
      AND o.status NOT IN ('CANCELLED', 'REFUNDED')
    GROUP BY p.category_id
    ORDER BY SUM(oi.line_total) DESC
    LIMIT 1;
    
    -- Find top product by quantity sold
    SELECT oi.product_id
    INTO v_top_product_id
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE o.created_at >= v_start_timestamp 
      AND o.created_at < v_end_timestamp
      AND o.status NOT IN ('CANCELLED', 'REFUNDED')
    GROUP BY oi.product_id
    ORDER BY SUM(oi.quantity) DESC
    LIMIT 1;
    
    -- Insert or update daily summary
    INSERT INTO daily_summary (
        summary_date,
        total_orders,
        total_revenue,
        total_items_sold,
        new_users,
        active_users,
        avg_order_value,
        top_category_id,
        top_product_id,
        created_at,
        updated_at
    ) VALUES (
        p_summary_date,
        v_total_orders,
        v_total_revenue,
        v_total_items_sold,
        v_new_users,
        v_active_users,
        v_avg_order_value,
        v_top_category_id,
        v_top_product_id,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT (summary_date) 
    DO UPDATE SET
        total_orders = EXCLUDED.total_orders,
        total_revenue = EXCLUDED.total_revenue,
        total_items_sold = EXCLUDED.total_items_sold,
        new_users = EXCLUDED.new_users,
        active_users = EXCLUDED.active_users,
        avg_order_value = EXCLUDED.avg_order_value,
        top_category_id = EXCLUDED.top_category_id,
        top_product_id = EXCLUDED.top_product_id,
        updated_at = CURRENT_TIMESTAMP;
    
    -- Log the summary generation in audit_log
    INSERT INTO audit_log (
        entity_type,
        entity_id,
        action,
        new_value,
        created_at
    ) VALUES (
        'DAILY_SUMMARY',
        p_summary_date::TEXT,
        'GENERATED',
        jsonb_build_object(
            'total_orders', v_total_orders,
            'total_revenue', v_total_revenue,
            'total_items_sold', v_total_items_sold
        ),
        CURRENT_TIMESTAMP
    );
    
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION usp_portfolio_ins_daily_summary(DATE) TO PUBLIC;

-- Add comment
COMMENT ON FUNCTION usp_portfolio_ins_daily_summary(DATE) IS 
'Generates daily summary report including revenue, orders, and top performers';
