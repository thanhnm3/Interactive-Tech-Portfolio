-- Flyway migration V2: Seed data
-- Initial test data for development

-- Sample categories
INSERT INTO categories (id, name, description, slug, display_order) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Electronics', 'Electronic devices and accessories', 'electronics', 1),
    ('550e8400-e29b-41d4-a716-446655440002', 'Computers', 'Laptops, desktops, and accessories', 'computers', 2),
    ('550e8400-e29b-41d4-a716-446655440003', 'Books', 'Physical and digital books', 'books', 3),
    ('550e8400-e29b-41d4-a716-446655440004', 'Home & Garden', 'Home improvement and garden supplies', 'home-garden', 4)
ON CONFLICT (slug) DO NOTHING;

-- Sample subcategories
INSERT INTO categories (id, name, description, slug, parent_id, display_order) VALUES
    ('550e8400-e29b-41d4-a716-446655440011', 'Laptops', 'Portable computers', 'laptops', '550e8400-e29b-41d4-a716-446655440002', 1),
    ('550e8400-e29b-41d4-a716-446655440012', 'Smartphones', 'Mobile phones', 'smartphones', '550e8400-e29b-41d4-a716-446655440001', 1),
    ('550e8400-e29b-41d4-a716-446655440013', 'Programming', 'Programming books', 'programming', '550e8400-e29b-41d4-a716-446655440003', 1)
ON CONFLICT (slug) DO NOTHING;

-- Sample products
INSERT INTO products (id, sku, name, description, price, category_id, stock_quantity, is_featured) VALUES
    ('660e8400-e29b-41d4-a716-446655440001', 'LAPTOP-001', 'MacBook Pro 14"', 'Apple MacBook Pro with M3 chip', 298000, '550e8400-e29b-41d4-a716-446655440011', 50, true),
    ('660e8400-e29b-41d4-a716-446655440002', 'PHONE-001', 'iPhone 15 Pro', 'Latest iPhone with titanium design', 159800, '550e8400-e29b-41d4-a716-446655440012', 100, true),
    ('660e8400-e29b-41d4-a716-446655440003', 'BOOK-001', 'Clean Code', 'A Handbook of Agile Software Craftsmanship', 4500, '550e8400-e29b-41d4-a716-446655440013', 200, false),
    ('660e8400-e29b-41d4-a716-446655440004', 'BOOK-002', 'Design Patterns', 'Elements of Reusable Object-Oriented Software', 5800, '550e8400-e29b-41d4-a716-446655440013', 150, true)
ON CONFLICT (sku) DO NOTHING;

-- Sample admin user
INSERT INTO users (id, user_type, email, username, password_hash, department, admin_level) VALUES
    ('770e8400-e29b-41d4-a716-446655440001', 'ADMIN', 'admin@portfolio.example.com', 'admin', '$2a$10$example_hash_admin', 'Engineering', 5)
ON CONFLICT (email) DO NOTHING;

-- Sample member user
INSERT INTO users (id, user_type, email, username, password_hash, first_name, last_name, membership_tier, loyalty_points) VALUES
    ('770e8400-e29b-41d4-a716-446655440002', 'MEMBER', 'taro@example.com', 'taro_yamada', '$2a$10$example_hash_member', 'Taro', 'Yamada', 'GOLD', 5500)
ON CONFLICT (email) DO NOTHING;
