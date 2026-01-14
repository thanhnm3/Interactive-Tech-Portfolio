/*
 * Author : Portfolio Team
 * Create date : 2024/01/14
 * Description : Initial database schema for Portfolio application
 * Use Tables : users, categories, products, orders, order_items, audit_log, query_history, daily_summary
 * Transaction : No
 */

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- Users table with Single Table Inheritance
-- =====================================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_type VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    -- Admin specific fields
    department VARCHAR(100),
    admin_level INTEGER CHECK (admin_level >= 1 AND admin_level <= 5),
    
    -- Member specific fields
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    membership_tier VARCHAR(20) DEFAULT 'BRONZE',
    loyalty_points INTEGER DEFAULT 0,
    date_of_birth DATE,
    
    -- Guest specific fields
    session_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    session_expires_at TIMESTAMP,
    is_converted BOOLEAN DEFAULT false
);

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- =====================================================
-- Categories table with hierarchical structure
-- =====================================================
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    slug VARCHAR(100) NOT NULL UNIQUE,
    parent_id UUID REFERENCES categories(id),
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Categories indexes
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_is_active ON categories(is_active);

-- =====================================================
-- Products table
-- =====================================================
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    original_price DECIMAL(12, 2),
    category_id UUID REFERENCES categories(id),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    min_stock_level INTEGER DEFAULT 10,
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_featured BOOLEAN DEFAULT false,
    weight DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Products indexes
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_is_active ON products(is_active);
CREATE INDEX idx_products_is_featured ON products(is_featured);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_created_at ON products(created_at);

-- Composite index for common queries
CREATE INDEX idx_products_category_active ON products(category_id, is_active, created_at DESC);

-- Full-text search index on product name
CREATE INDEX idx_products_name_trgm ON products USING gin (name gin_trgm_ops);

-- =====================================================
-- Orders table with monthly partitioning
-- =====================================================
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id UUID REFERENCES users(id),
    subtotal DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    shipping_amount DECIMAL(12, 2) DEFAULT 0,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    total_amount DECIMAL(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipping_address TEXT,
    billing_address TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP
) PARTITION BY RANGE (created_at);

-- Create partitions for orders (monthly)
CREATE TABLE orders_2024_01 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE orders_2024_03 PARTITION OF orders
    FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

CREATE TABLE orders_2024_04 PARTITION OF orders
    FOR VALUES FROM ('2024-04-01') TO ('2024-05-01');

CREATE TABLE orders_2024_05 PARTITION OF orders
    FOR VALUES FROM ('2024-05-01') TO ('2024-06-01');

CREATE TABLE orders_2024_06 PARTITION OF orders
    FOR VALUES FROM ('2024-06-01') TO ('2024-07-01');

CREATE TABLE orders_2024_07 PARTITION OF orders
    FOR VALUES FROM ('2024-07-01') TO ('2024-08-01');

CREATE TABLE orders_2024_08 PARTITION OF orders
    FOR VALUES FROM ('2024-08-01') TO ('2024-09-01');

CREATE TABLE orders_2024_09 PARTITION OF orders
    FOR VALUES FROM ('2024-09-01') TO ('2024-10-01');

CREATE TABLE orders_2024_10 PARTITION OF orders
    FOR VALUES FROM ('2024-10-01') TO ('2024-11-01');

CREATE TABLE orders_2024_11 PARTITION OF orders
    FOR VALUES FROM ('2024-11-01') TO ('2024-12-01');

CREATE TABLE orders_2024_12 PARTITION OF orders
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- Orders indexes
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- =====================================================
-- Order items table
-- =====================================================
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    product_id UUID REFERENCES products(id),
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    line_total DECIMAL(12, 2) NOT NULL
);

-- Order items indexes
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- =====================================================
-- Audit log table for activity tracking
-- =====================================================
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36),
    action VARCHAR(50) NOT NULL,
    user_id UUID REFERENCES users(id),
    username VARCHAR(100),
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Audit log indexes
CREATE INDEX idx_audit_log_entity_type ON audit_log(entity_type);
CREATE INDEX idx_audit_log_entity_id ON audit_log(entity_id);
CREATE INDEX idx_audit_log_action ON audit_log(action);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);

-- =====================================================
-- Query history table for performance tracking
-- =====================================================
CREATE TABLE query_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    query_text TEXT NOT NULL,
    query_hash VARCHAR(64) NOT NULL,
    execution_time_ms BIGINT NOT NULL,
    rows_examined BIGINT,
    rows_returned BIGINT,
    used_index BOOLEAN DEFAULT false,
    index_name VARCHAR(100),
    execution_plan JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Query history indexes
CREATE INDEX idx_query_history_hash ON query_history(query_hash);
CREATE INDEX idx_query_history_execution_time ON query_history(execution_time_ms);
CREATE INDEX idx_query_history_created_at ON query_history(created_at);

-- =====================================================
-- Daily summary table for reports
-- =====================================================
CREATE TABLE daily_summary (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    summary_date DATE NOT NULL UNIQUE,
    total_orders INTEGER DEFAULT 0,
    total_revenue DECIMAL(15, 2) DEFAULT 0,
    total_items_sold INTEGER DEFAULT 0,
    new_users INTEGER DEFAULT 0,
    active_users INTEGER DEFAULT 0,
    avg_order_value DECIMAL(12, 2) DEFAULT 0,
    top_category_id UUID REFERENCES categories(id),
    top_product_id UUID REFERENCES products(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Daily summary indexes
CREATE INDEX idx_daily_summary_date ON daily_summary(summary_date);

-- =====================================================
-- Enable trigram extension for full-text search
-- =====================================================
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- =====================================================
-- Insert sample data
-- =====================================================

-- Sample categories
INSERT INTO categories (id, name, description, slug, display_order) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Electronics', 'Electronic devices and accessories', 'electronics', 1),
    ('550e8400-e29b-41d4-a716-446655440002', 'Computers', 'Laptops, desktops, and accessories', 'computers', 2),
    ('550e8400-e29b-41d4-a716-446655440003', 'Books', 'Physical and digital books', 'books', 3),
    ('550e8400-e29b-41d4-a716-446655440004', 'Home & Garden', 'Home improvement and garden supplies', 'home-garden', 4);

-- Sample subcategories
INSERT INTO categories (id, name, description, slug, parent_id, display_order) VALUES
    ('550e8400-e29b-41d4-a716-446655440011', 'Laptops', 'Portable computers', 'laptops', '550e8400-e29b-41d4-a716-446655440002', 1),
    ('550e8400-e29b-41d4-a716-446655440012', 'Smartphones', 'Mobile phones', 'smartphones', '550e8400-e29b-41d4-a716-446655440001', 1),
    ('550e8400-e29b-41d4-a716-446655440013', 'Programming', 'Programming books', 'programming', '550e8400-e29b-41d4-a716-446655440003', 1);

-- Sample products
INSERT INTO products (id, sku, name, description, price, category_id, stock_quantity, is_featured) VALUES
    ('660e8400-e29b-41d4-a716-446655440001', 'LAPTOP-001', 'MacBook Pro 14"', 'Apple MacBook Pro with M3 chip', 298000, '550e8400-e29b-41d4-a716-446655440011', 50, true),
    ('660e8400-e29b-41d4-a716-446655440002', 'PHONE-001', 'iPhone 15 Pro', 'Latest iPhone with titanium design', 159800, '550e8400-e29b-41d4-a716-446655440012', 100, true),
    ('660e8400-e29b-41d4-a716-446655440003', 'BOOK-001', 'Clean Code', 'A Handbook of Agile Software Craftsmanship', 4500, '550e8400-e29b-41d4-a716-446655440013', 200, false),
    ('660e8400-e29b-41d4-a716-446655440004', 'BOOK-002', 'Design Patterns', 'Elements of Reusable Object-Oriented Software', 5800, '550e8400-e29b-41d4-a716-446655440013', 150, true);

-- Sample admin user (password: 'admin123' - should be properly hashed in production)
INSERT INTO users (id, user_type, email, username, password_hash, department, admin_level) VALUES
    ('770e8400-e29b-41d4-a716-446655440001', 'ADMIN', 'admin@portfolio.example.com', 'admin', '$2a$10$example_hash_admin', 'Engineering', 5);

-- Sample member user
INSERT INTO users (id, user_type, email, username, password_hash, first_name, last_name, membership_tier, loyalty_points) VALUES
    ('770e8400-e29b-41d4-a716-446655440002', 'MEMBER', 'taro@example.com', 'taro_yamada', '$2a$10$example_hash_member', 'Taro', 'Yamada', 'GOLD', 5500);

COMMENT ON TABLE users IS 'User accounts with Single Table Inheritance for Admin, Member, and Guest types';
COMMENT ON TABLE categories IS 'Product categories with hierarchical structure';
COMMENT ON TABLE products IS 'Product catalog with pricing and inventory';
COMMENT ON TABLE orders IS 'Customer orders with monthly partitioning';
COMMENT ON TABLE order_items IS 'Line items for each order';
COMMENT ON TABLE audit_log IS 'Activity audit trail for compliance';
COMMENT ON TABLE query_history IS 'Query performance tracking for optimization';
COMMENT ON TABLE daily_summary IS 'Pre-computed daily business metrics';
