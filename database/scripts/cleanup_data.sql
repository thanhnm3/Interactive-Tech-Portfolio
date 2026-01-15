/*
 * Author : Portfolio Team
 * Create date : 2025/01/15
 * Description : Script to clean up all test data from Portfolio database
 * Use Tables : users, categories, products, orders, order_items, audit_log, query_history, daily_summary
 * Transaction : Yes
 * 
 * Usage:
 *   docker-compose exec -T postgres psql -U portfolio_user -d portfolio -f /tmp/cleanup_data.sql
 *   Or copy file to container first:
 *   docker cp database/scripts/cleanup_data.sql portfolio-postgres:/tmp/cleanup_data.sql
 */

-- Clean up data in reverse order of foreign key dependencies
-- Using TRUNCATE CASCADE for faster deletion and to handle foreign keys automatically

BEGIN;

-- Delete dependent tables first
TRUNCATE TABLE IF EXISTS query_history CASCADE;
TRUNCATE TABLE IF EXISTS audit_log CASCADE;
TRUNCATE TABLE IF EXISTS order_items CASCADE;
TRUNCATE TABLE IF EXISTS orders CASCADE;
TRUNCATE TABLE IF EXISTS daily_summary CASCADE;

-- Delete main tables
TRUNCATE TABLE IF EXISTS products CASCADE;
TRUNCATE TABLE IF EXISTS categories CASCADE;
TRUNCATE TABLE IF EXISTS users CASCADE;

COMMIT;

-- Verify cleanup
SELECT 
    'users' as table_name, COUNT(*) as remaining_rows FROM users
UNION ALL
SELECT 'categories', COUNT(*) FROM categories
UNION ALL
SELECT 'products', COUNT(*) FROM products
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL
SELECT 'audit_log', COUNT(*) FROM audit_log
UNION ALL
SELECT 'query_history', COUNT(*) FROM query_history;
