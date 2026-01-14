/*
 * Author : Portfolio Team
 * Create date : 2024/01/14
 * Description : Get product list by category with pagination
 * Use Tables : products, categories
 * Transaction : No
 * Modified Histories:
 * 2024/01/14: Initial creation
 */

-- Drop function if exists
DROP FUNCTION IF EXISTS usp_portfolio_sel_product_list_by_category(UUID, INTEGER, INTEGER, TEXT, TEXT);

/**
 * Get products by category with pagination and sorting
 * @param p_category_id - category UUID
 * @param p_page_size - number of items per page
 * @param p_page_number - page number (1-based)
 * @param p_sort_by - column to sort by
 * @param p_sort_order - ASC or DESC
 * @returns TABLE with product details and pagination info
 */
CREATE OR REPLACE FUNCTION usp_portfolio_sel_product_list_by_category(
    p_category_id UUID,
    p_page_size INTEGER DEFAULT 20,
    p_page_number INTEGER DEFAULT 1,
    p_sort_by TEXT DEFAULT 'created_at',
    p_sort_order TEXT DEFAULT 'DESC'
)
RETURNS TABLE (
    product_id UUID,
    sku VARCHAR(50),
    product_name VARCHAR(255),
    description TEXT,
    price DECIMAL(12, 2),
    original_price DECIMAL(12, 2),
    stock_quantity INTEGER,
    is_featured BOOLEAN,
    image_url VARCHAR(500),
    category_name VARCHAR(100),
    total_count BIGINT,
    page_count INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_offset INTEGER;
    v_total_count BIGINT;
    v_page_count INTEGER;
    v_sort_column TEXT;
    v_sort_direction TEXT;
BEGIN
    -- Calculate offset
    v_offset := (p_page_number - 1) * p_page_size;
    
    -- Validate and set sort column
    v_sort_column := CASE p_sort_by
        WHEN 'name' THEN 'p.name'
        WHEN 'price' THEN 'p.price'
        WHEN 'stock' THEN 'p.stock_quantity'
        WHEN 'created_at' THEN 'p.created_at'
        ELSE 'p.created_at'
    END;
    
    -- Validate sort direction
    v_sort_direction := CASE UPPER(p_sort_order)
        WHEN 'ASC' THEN 'ASC'
        ELSE 'DESC'
    END;
    
    -- Get total count
    SELECT COUNT(*)
    INTO v_total_count
    FROM products p
    WHERE p.category_id = p_category_id
      AND p.is_active = true;
    
    -- Calculate page count
    v_page_count := CEIL(v_total_count::NUMERIC / p_page_size);
    
    -- Return products with pagination
    RETURN QUERY EXECUTE format(
        'SELECT 
            p.id,
            p.sku,
            p.name,
            p.description,
            p.price,
            p.original_price,
            p.stock_quantity,
            p.is_featured,
            p.image_url,
            c.name as category_name,
            %L::BIGINT as total_count,
            %L::INTEGER as page_count
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE p.category_id = %L
          AND p.is_active = true
        ORDER BY %s %s
        LIMIT %L OFFSET %L',
        v_total_count,
        v_page_count,
        p_category_id,
        v_sort_column,
        v_sort_direction,
        p_page_size,
        v_offset
    );
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION usp_portfolio_sel_product_list_by_category(UUID, INTEGER, INTEGER, TEXT, TEXT) TO PUBLIC;

-- Add comment
COMMENT ON FUNCTION usp_portfolio_sel_product_list_by_category(UUID, INTEGER, INTEGER, TEXT, TEXT) IS 
'Retrieves paginated product list for a specific category with sorting options';

/**
 * Search products by name with full-text search
 * @param p_search_term - search keyword
 * @param p_page_size - number of items per page
 * @param p_page_number - page number (1-based)
 * @returns TABLE with matching products
 */
CREATE OR REPLACE FUNCTION usp_portfolio_sel_product_search(
    p_search_term TEXT,
    p_page_size INTEGER DEFAULT 20,
    p_page_number INTEGER DEFAULT 1
)
RETURNS TABLE (
    product_id UUID,
    sku VARCHAR(50),
    product_name VARCHAR(255),
    description TEXT,
    price DECIMAL(12, 2),
    stock_quantity INTEGER,
    category_name VARCHAR(100),
    relevance REAL,
    total_count BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_offset INTEGER;
    v_total_count BIGINT;
    v_search_pattern TEXT;
BEGIN
    -- Calculate offset
    v_offset := (p_page_number - 1) * p_page_size;
    
    -- Prepare search pattern
    v_search_pattern := '%' || LOWER(p_search_term) || '%';
    
    -- Get total count
    SELECT COUNT(*)
    INTO v_total_count
    FROM products p
    WHERE p.is_active = true
      AND (LOWER(p.name) LIKE v_search_pattern 
           OR LOWER(p.description) LIKE v_search_pattern);
    
    -- Return search results
    RETURN QUERY
    SELECT 
        p.id,
        p.sku,
        p.name,
        p.description,
        p.price,
        p.stock_quantity,
        c.name,
        similarity(p.name, p_search_term) as relevance,
        v_total_count
    FROM products p
    LEFT JOIN categories c ON p.category_id = c.id
    WHERE p.is_active = true
      AND (LOWER(p.name) LIKE v_search_pattern 
           OR LOWER(p.description) LIKE v_search_pattern)
    ORDER BY similarity(p.name, p_search_term) DESC, p.name
    LIMIT p_page_size OFFSET v_offset;
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION usp_portfolio_sel_product_search(TEXT, INTEGER, INTEGER) TO PUBLIC;

-- Add comment
COMMENT ON FUNCTION usp_portfolio_sel_product_search(TEXT, INTEGER, INTEGER) IS 
'Searches products using full-text search with relevance scoring';
