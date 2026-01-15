#!/usr/bin/env python3
"""
Bulk Data Generator for Portfolio Database
Author: Portfolio Team
Create date: 2025/01/15
Description: Script to generate and insert large volume of test data (1M+ records)
Use Tables: users, categories, products, orders, order_items, audit_log, query_history
Transaction: Yes (batch commits)
"""

import psycopg2
from psycopg2.extras import execute_batch, execute_values
from psycopg2.pool import SimpleConnectionPool
import uuid
import random
import string
from datetime import datetime, timedelta
from decimal import Decimal
import argparse
import sys
from typing import List, Tuple, Optional
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from threading import Lock

# Database configuration
DB_CONFIG = {
    'host': 'localhost',
    'port': 5433,
    'database': 'portfolio',
    'user': 'portfolio_user',
    'password': 'portfolio_pass'
}

# Data generation configuration
CONFIG = {
    'users': {
        'total': 100000,
        'admin_ratio': 0.1,      # 10% ADMIN
        'member_ratio': 0.3,      # 30% MEMBER
        'guest_ratio': 0.6        # 60% GUEST
    },
    'categories': {
        'total': 1000,
        'max_depth': 3,
        'children_per_parent': 5
    },
    'products': {
        'total': 200000,
        'min_price': 1000,
        'max_price': 5000000
    },
    'orders': {
        'total': 300000,
        'date_range_days': 365
    },
    'order_items': {
        'avg_per_order': 1.3,
        'min_quantity': 1,
        'max_quantity': 10
    },
    'audit_log': {
        'total': 50000
    },
    'query_history': {
        'total': 10000
    },
    'batch_size': 1000,
    'connection_pool_size': 5,
    'parallel_workers': 4  # Number of parallel threads for data generation
}


class BulkDataGenerator:
    """Generate and insert bulk data into database"""
    
    def __init__(self, config: dict, db_config: dict):
        self.config = config
        self.db_config = db_config
        self.pool: Optional[SimpleConnectionPool] = None
        self.user_ids: List[str] = []
        self.category_ids: List[str] = []
        self.product_ids: List[str] = []
        self.product_info: dict = {}  # Store product info: {product_id: (name, sku, price)}
        self.order_ids: List[str] = []
        self.lock = Lock()  # Lock for thread-safe operations
        
    def connect(self):
        """Create connection pool"""
        try:
            self.pool = SimpleConnectionPool(
                1,
                self.config['connection_pool_size'],
                **self.db_config
            )
            print("✓ Database connection pool created")
        except Exception as e:
            print(f"✗ Failed to create connection pool: {e}")
            sys.exit(1)
    
    def get_connection(self):
        """Get connection from pool"""
        return self.pool.getconn()
    
    def return_connection(self, conn):
        """Return connection to pool"""
        self.pool.putconn(conn)
    
    def close(self):
        """Close connection pool"""
        if self.pool:
            self.pool.closeall()
            print("✓ Connection pool closed")
    
    def generate_random_string(self, length: int = 10) -> str:
        """Generate random string"""
        return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))
    
    def generate_email(self, username: str) -> str:
        """Generate email from username"""
        domains = ['example.com', 'test.com', 'demo.org', 'sample.net', 'mock.io']
        return f"{username}@{random.choice(domains)}"
    
    def generate_password_hash(self) -> str:
        """Generate mock password hash"""
        return f"$2a$10${self.generate_random_string(53)}"
    
    def generate_users(self) -> int:
        """Generate and insert users"""
        print("\n[1/7] Generating users...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        total = self.config['users']['total']
        admin_count = int(total * self.config['users']['admin_ratio'])
        member_count = int(total * self.config['users']['member_ratio'])
        guest_count = total - admin_count - member_count
        
        batch_size = self.config['batch_size']
        inserted = 0
        
        try:
            # Generate ADMIN users
            print(f"  → Generating {admin_count} ADMIN users...")
            admin_data = []
            for i in range(admin_count):
                user_id = str(uuid.uuid4())
                username = f"admin_{self.generate_random_string(8)}"
                admin_data.append((
                    user_id,
                    'ADMIN',
                    self.generate_email(username),
                    username,
                    self.generate_password_hash(),
                    True,
                    datetime.now() - timedelta(days=random.randint(1, 365)),
                    random.choice(['Engineering', 'Sales', 'Marketing', 'Operations', 'Finance']),
                    random.randint(1, 5)
                ))
                self.user_ids.append(user_id)
                
                if len(admin_data) >= batch_size:
                    execute_batch(
                        cursor,
                        """INSERT INTO users (id, user_type, email, username, password_hash, is_active, created_at, department, admin_level)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                        admin_data
                    )
                    conn.commit()
                    inserted += len(admin_data)
                    print(f"    Inserted {inserted}/{admin_count} ADMIN users", end='\r')
                    admin_data = []
            
            if admin_data:
                execute_batch(cursor, """INSERT INTO users (id, user_type, email, username, password_hash, is_active, created_at, department, admin_level)
                                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""", admin_data)
                conn.commit()
                inserted += len(admin_data)
            
            # Generate MEMBER users
            print(f"\n  → Generating {member_count} MEMBER users...")
            member_data = []
            for i in range(member_count):
                user_id = str(uuid.uuid4())
                username = f"member_{self.generate_random_string(8)}"
                first_name = random.choice(['Taro', 'Hanako', 'Ichiro', 'Yuki', 'Sakura', 'Kenji', 'Aiko', 'Hiroshi'])
                last_name = random.choice(['Yamada', 'Tanaka', 'Suzuki', 'Watanabe', 'Ito', 'Nakamura', 'Kobayashi', 'Kato'])
                member_data.append((
                    user_id,
                    'MEMBER',
                    self.generate_email(username),
                    username,
                    self.generate_password_hash(),
                    True,
                    datetime.now() - timedelta(days=random.randint(1, 365)),
                    first_name,
                    last_name,
                    f"090{random.randint(10000000, 99999999)}",
                    random.choice(['BRONZE', 'SILVER', 'GOLD', 'PLATINUM']),
                    random.randint(0, 50000),
                    datetime.now() - timedelta(days=random.randint(3650, 10950))  # 10-30 years ago
                ))
                self.user_ids.append(user_id)
                
                if len(member_data) >= batch_size:
                    execute_batch(
                        cursor,
                        """INSERT INTO users (id, user_type, email, username, password_hash, is_active, created_at, first_name, last_name, phone_number, membership_tier, loyalty_points, date_of_birth)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                        member_data
                    )
                    conn.commit()
                    inserted += len(member_data)
                    print(f"    Inserted {inserted - admin_count}/{member_count} MEMBER users", end='\r')
                    member_data = []
            
            if member_data:
                execute_batch(cursor, """INSERT INTO users (id, user_type, email, username, password_hash, is_active, created_at, first_name, last_name, phone_number, membership_tier, loyalty_points, date_of_birth)
                                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""", member_data)
                conn.commit()
                inserted += len(member_data)
            
            # Generate GUEST users
            print(f"\n  → Generating {guest_count} GUEST users...")
            guest_data = []
            for i in range(guest_count):
                user_id = str(uuid.uuid4())
                username = f"guest_{self.generate_random_string(8)}"
                guest_data.append((
                    user_id,
                    'GUEST',
                    self.generate_email(username),
                    username,
                    self.generate_password_hash(),
                    True,
                    datetime.now() - timedelta(days=random.randint(1, 90)),
                    f"session_{self.generate_random_string(16)}",
                    f"{random.randint(1, 255)}.{random.randint(1, 255)}.{random.randint(1, 255)}.{random.randint(1, 255)}",
                    f"Mozilla/5.0 {self.generate_random_string(20)}",
                    datetime.now() + timedelta(hours=random.randint(1, 24)),
                    random.choice([True, False])
                ))
                self.user_ids.append(user_id)
                
                if len(guest_data) >= batch_size:
                    execute_batch(
                        cursor,
                        """INSERT INTO users (id, user_type, email, username, password_hash, is_active, created_at, session_id, ip_address, user_agent, session_expires_at, is_converted)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                        guest_data
                    )
                    conn.commit()
                    inserted += len(guest_data)
                    print(f"    Inserted {inserted - admin_count - member_count}/{guest_count} GUEST users", end='\r')
                    guest_data = []
            
            if guest_data:
                execute_batch(cursor, """INSERT INTO users (id, user_type, email, username, password_hash, is_active, created_at, session_id, ip_address, user_agent, session_expires_at, is_converted)
                                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""", guest_data)
                conn.commit()
                inserted += len(guest_data)
            
            cursor.close()
            print(f"\n✓ Inserted {inserted} users total")
            return inserted
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error generating users: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def generate_categories(self) -> int:
        """Generate and insert categories with hierarchical structure"""
        print("\n[2/7] Generating categories...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        total = self.config['categories']['total']
        batch_size = self.config['batch_size']
        inserted = 0
        
        category_names = [
            'Electronics', 'Computers', 'Books', 'Home & Garden', 'Clothing', 'Sports',
            'Toys', 'Automotive', 'Health', 'Beauty', 'Food', 'Beverages',
            'Furniture', 'Appliances', 'Tools', 'Office', 'Music', 'Movies',
            'Games', 'Software', 'Hardware', 'Accessories', 'Parts', 'Services'
        ]
        
        try:
            # Generate root categories
            root_data = []
            root_count = min(50, total // 10)
            for i in range(root_count):
                cat_id = str(uuid.uuid4())
                name = random.choice(category_names) + f" {i+1}"
                root_data.append((
                    cat_id,
                    name,
                    f"Description for {name}",
                    f"slug-{name.lower().replace(' ', '-')}-{i+1}",
                    None,  # parent_id
                    i,
                    True,
                    datetime.now() - timedelta(days=random.randint(1, 365)),
                    None
                ))
                self.category_ids.append(cat_id)
            
            if root_data:
                execute_batch(
                    cursor,
                    """INSERT INTO categories (id, name, description, slug, parent_id, display_order, is_active, created_at, updated_at)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    root_data
                )
                conn.commit()
                inserted += len(root_data)
            
            # Generate child categories
            remaining = total - inserted
            parent_categories = self.category_ids[:root_count]
            child_data = []
            child_level = 1
            
            while inserted < total and child_level <= self.config['categories']['max_depth']:
                new_parents = []
                for parent_id in parent_categories:
                    children_count = min(
                        self.config['categories']['children_per_parent'],
                        (total - inserted) // len(parent_categories) if parent_categories else 0
                    )
                    
                    for j in range(children_count):
                        if inserted >= total:
                            break
                        
                        cat_id = str(uuid.uuid4())
                        name = f"Subcategory {child_level}-{j+1}"
                        child_data.append((
                            cat_id,
                            name,
                            f"Description for {name}",
                            f"slug-{self.generate_random_string(10)}-{inserted}",
                            parent_id,
                            j,
                            True,
                            datetime.now() - timedelta(days=random.randint(1, 365)),
                            None
                        ))
                        self.category_ids.append(cat_id)
                        new_parents.append(cat_id)
                        inserted += 1
                        
                        if len(child_data) >= batch_size:
                            execute_batch(
                                cursor,
                                """INSERT INTO categories (id, name, description, slug, parent_id, display_order, is_active, created_at, updated_at)
                                   VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                                child_data
                            )
                            conn.commit()
                            print(f"  Inserted {inserted}/{total} categories", end='\r')
                            child_data = []
                
                parent_categories = new_parents
                child_level += 1
            
            if child_data:
                execute_batch(
                    cursor,
                    """INSERT INTO categories (id, name, description, slug, parent_id, display_order, is_active, created_at, updated_at)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    child_data
                )
                conn.commit()
                inserted += len(child_data)
            
            cursor.close()
            print(f"\n✓ Inserted {inserted} categories")
            return inserted
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error generating categories: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def _generate_products_batch(self, start_idx: int, end_idx: int, category_ids: List[str]) -> Tuple[List, dict]:
        """Generate a batch of products (for parallel processing)"""
        product_names = [
            'Laptop', 'Smartphone', 'Tablet', 'Monitor', 'Keyboard', 'Mouse',
            'Headphones', 'Speaker', 'Camera', 'Watch', 'Charger', 'Cable',
            'Book', 'Notebook', 'Pen', 'Desk', 'Chair', 'Lamp',
            'Bag', 'Wallet', 'Shoes', 'Shirt', 'Jacket', 'Hat'
        ]
        
        product_data = []
        product_info = {}
        
        for i in range(start_idx, end_idx):
            product_id = str(uuid.uuid4())
            base_name = random.choice(product_names)
            name = f"{base_name} {self.generate_random_string(6).upper()}-{i+1}"
            sku = f"{base_name[:3].upper()}-{i+1:06d}"
            price = Decimal(random.randint(
                self.config['products']['min_price'],
                self.config['products']['max_price']
            ))
            original_price = price * Decimal(random.uniform(1.1, 1.5))
            category_id = random.choice(category_ids) if category_ids else None
            
            product_data.append((
                product_id,
                sku,
                name,
                f"Description for {name}",
                price,
                original_price,
                category_id,
                random.randint(0, 1000),
                random.randint(5, 50),
                f"https://example.com/images/{sku}.jpg",
                random.choice([True, False]),
                random.choice([True, False]),
                Decimal(random.uniform(0.1, 50.0)),
                datetime.now() - timedelta(days=random.randint(1, 365)),
                None
            ))
            product_info[product_id] = (name, sku, price)
        
        return product_data, product_info
    
    def _insert_products_batch(self, product_data: List) -> int:
        """Insert a batch of products (thread-safe)"""
        conn = self.get_connection()
        cursor = conn.cursor()
        try:
            execute_batch(
                cursor,
                """INSERT INTO products (id, sku, name, description, price, original_price, category_id, stock_quantity, min_stock_level, image_url, is_active, is_featured, weight, created_at, updated_at)
                   VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                product_data
            )
            conn.commit()
            return len(product_data)
        except Exception as e:
            conn.rollback()
            raise e
        finally:
            cursor.close()
            self.return_connection(conn)
    
    def generate_products(self) -> int:
        """Generate and insert products with parallel processing"""
        print("\n[3/7] Generating products (with parallel processing)...")
        
        total = self.config['products']['total']
        batch_size = self.config['batch_size']
        workers = self.config.get('parallel_workers', 4)
        inserted = 0
        
        try:
            # Generate data in parallel
            chunks = []
            chunk_size = max(batch_size * 10, total // workers)  # Larger chunks for parallel generation
            
            for i in range(0, total, chunk_size):
                end_idx = min(i + chunk_size, total)
                chunks.append((i, end_idx))
            
            all_product_data = []
            all_product_info = {}
            
            with ThreadPoolExecutor(max_workers=workers) as executor:
                futures = []
                for start_idx, end_idx in chunks:
                    future = executor.submit(self._generate_products_batch, start_idx, end_idx, self.category_ids)
                    futures.append(future)
                
                for future in as_completed(futures):
                    product_data, product_info = future.result()
                    all_product_data.extend(product_data)
                    all_product_info.update(product_info)
            
            # Update shared state (thread-safe)
            with self.lock:
                for item in all_product_data:
                    self.product_ids.append(item[0])
                self.product_info.update(all_product_info)
            
            # Insert in batches (can also be parallelized, but DB connection is bottleneck)
            for i in range(0, len(all_product_data), batch_size):
                batch = all_product_data[i:i + batch_size]
                self._insert_products_batch(batch)
                inserted += len(batch)
                print(f"  Inserted {inserted}/{total} products", end='\r')
            
            print(f"\n✓ Inserted {inserted} products")
            return inserted
            
        except Exception as e:
            print(f"\n✗ Error generating products: {e}")
            raise
    
    def generate_orders(self) -> int:
        """Generate and insert orders"""
        print("\n[4/7] Generating orders...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        total = self.config['orders']['total']
        batch_size = self.config['batch_size']
        inserted = 0
        
        start_date = datetime.now() - timedelta(days=self.config['orders']['date_range_days'])
        statuses = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED']
        status_weights = [0.1, 0.2, 0.3, 0.35, 0.05]  # Most orders are delivered
        
        try:
            order_data = []
            member_user_ids = [uid for i, uid in enumerate(self.user_ids) if i < len(self.user_ids) * 0.4]  # First 40% are members/admins
            
            for i in range(total):
                order_id = str(uuid.uuid4())
                order_number = f"ORD-{datetime.now().strftime('%Y%m%d')}-{i+1:06d}"
                user_id = random.choice(member_user_ids) if member_user_ids else None
                
                # Generate order amounts
                subtotal = Decimal(random.uniform(1000, 500000))
                tax_amount = subtotal * Decimal(0.1)  # 10% tax
                shipping_amount = Decimal(random.choice([0, 500, 1000, 2000]))
                discount_amount = subtotal * Decimal(random.uniform(0, 0.2))  # 0-20% discount
                total_amount = subtotal + tax_amount + shipping_amount - discount_amount
                
                status = random.choices(statuses, weights=status_weights)[0]
                created_at = start_date + timedelta(
                    days=random.randint(0, self.config['orders']['date_range_days']),
                    hours=random.randint(0, 23),
                    minutes=random.randint(0, 59)
                )
                
                completed_at = None
                if status in ['DELIVERED', 'CANCELLED']:
                    completed_at = created_at + timedelta(days=random.randint(1, 7))
                
                order_data.append((
                    order_id,
                    order_number,
                    user_id,
                    subtotal,
                    tax_amount,
                    shipping_amount,
                    discount_amount,
                    total_amount,
                    status,
                    f"Shipping address {i+1}",
                    f"Billing address {i+1}",
                    f"Order notes {i+1}" if random.random() < 0.3 else None,
                    created_at,
                    created_at,  # updated_at
                    completed_at
                ))
                self.order_ids.append(order_id)
                
                if len(order_data) >= batch_size:
                    execute_batch(
                        cursor,
                        """INSERT INTO orders (id, order_number, user_id, subtotal, tax_amount, shipping_amount, discount_amount, total_amount, status, shipping_address, billing_address, notes, created_at, updated_at, completed_at)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                        order_data
                    )
                    conn.commit()
                    inserted += len(order_data)
                    print(f"  Inserted {inserted}/{total} orders", end='\r')
                    order_data = []
            
            if order_data:
                execute_batch(
                    cursor,
                    """INSERT INTO orders (id, order_number, user_id, subtotal, tax_amount, shipping_amount, discount_amount, total_amount, status, shipping_address, billing_address, notes, created_at, updated_at, completed_at)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    order_data
                )
                conn.commit()
                inserted += len(order_data)
            
            cursor.close()
            print(f"\n✓ Inserted {inserted} orders")
            return inserted
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error generating orders: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def generate_order_items(self) -> int:
        """Generate and insert order items"""
        print("\n[5/7] Generating order items...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        # Calculate total order items based on average per order
        avg_per_order = self.config['order_items']['avg_per_order']
        total = int(len(self.order_ids) * avg_per_order)
        batch_size = self.config['batch_size']
        inserted = 0
        
        try:
            if not self.product_info:
                print("  ⚠ No products found, skipping order items")
                return 0
            
            order_item_data = []
            product_list = list(self.product_info.keys())
            
            for order_id in self.order_ids:
                # Number of items per order (1-5 items, weighted towards 1-2)
                num_items = random.choices([1, 2, 3, 4, 5], weights=[0.4, 0.3, 0.15, 0.1, 0.05])[0]
                
                for _ in range(num_items):
                    product_id = random.choice(product_list)
                    product_name, product_sku, unit_price = self.product_info[product_id]
                    
                    quantity = random.randint(
                        self.config['order_items']['min_quantity'],
                        self.config['order_items']['max_quantity']
                    )
                    discount_amount = unit_price * Decimal(random.uniform(0, 0.15)) * quantity
                    line_total = (unit_price * quantity) - discount_amount
                    
                    order_item_data.append((
                        str(uuid.uuid4()),
                        order_id,
                        product_id,
                        product_name,
                        product_sku,
                        quantity,
                        unit_price,
                        discount_amount,
                        line_total
                    ))
                    inserted += 1
                    
                    if len(order_item_data) >= batch_size:
                        execute_batch(
                            cursor,
                            """INSERT INTO order_items (id, order_id, product_id, product_name, product_sku, quantity, unit_price, discount_amount, line_total)
                               VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                            order_item_data
                        )
                        conn.commit()
                        print(f"  Inserted {inserted}/{total} order items", end='\r')
                        order_item_data = []
            
            if order_item_data:
                execute_batch(
                    cursor,
                    """INSERT INTO order_items (id, order_id, product_id, product_name, product_sku, quantity, unit_price, discount_amount, line_total)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    order_item_data
                )
                conn.commit()
                inserted += len(order_item_data)
            
            cursor.close()
            print(f"\n✓ Inserted {inserted} order items")
            return inserted
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error generating order items: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def generate_audit_log(self) -> int:
        """Generate and insert audit log entries"""
        print("\n[6/7] Generating audit log entries...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        total = self.config['audit_log']['total']
        batch_size = self.config['batch_size']
        inserted = 0
        
        actions = ['CREATE', 'UPDATE', 'DELETE', 'VIEW', 'LOGIN', 'LOGOUT']
        entity_types = ['USER', 'PRODUCT', 'ORDER', 'CATEGORY']
        
        try:
            audit_data = []
            for i in range(total):
                audit_id = str(uuid.uuid4())
                entity_type = random.choice(entity_types)
                entity_id = random.choice(self.user_ids + self.product_ids + self.order_ids)
                action = random.choice(actions)
                user_id = random.choice(self.user_ids) if self.user_ids else None
                username = f"user_{random.randint(1, 1000)}"
                
                old_value = None
                new_value = None
                if action in ['CREATE', 'UPDATE']:
                    new_value = f'{{"field": "value_{i}"}}'
                if action in ['UPDATE', 'DELETE']:
                    old_value = f'{{"field": "old_value_{i}"}}'
                
                created_at = datetime.now() - timedelta(days=random.randint(0, 90))
                
                audit_data.append((
                    audit_id,
                    entity_type,
                    entity_id,
                    action,
                    user_id,
                    username,
                    old_value,
                    new_value,
                    f"{random.randint(1, 255)}.{random.randint(1, 255)}.{random.randint(1, 255)}.{random.randint(1, 255)}",
                    f"Mozilla/5.0 {self.generate_random_string(20)}",
                    created_at
                ))
                
                if len(audit_data) >= batch_size:
                    execute_batch(
                        cursor,
                        """INSERT INTO audit_log (id, entity_type, entity_id, action, user_id, username, old_value, new_value, ip_address, user_agent, created_at)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                        audit_data
                    )
                    conn.commit()
                    inserted += len(audit_data)
                    print(f"  Inserted {inserted}/{total} audit log entries", end='\r')
                    audit_data = []
            
            if audit_data:
                execute_batch(
                    cursor,
                    """INSERT INTO audit_log (id, entity_type, entity_id, action, user_id, username, old_value, new_value, ip_address, user_agent, created_at)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    audit_data
                )
                conn.commit()
                inserted += len(audit_data)
            
            cursor.close()
            print(f"\n✓ Inserted {inserted} audit log entries")
            return inserted
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error generating audit log: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def generate_query_history(self) -> int:
        """Generate and insert query history entries"""
        print("\n[7/7] Generating query history entries...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        total = self.config['query_history']['total']
        batch_size = self.config['batch_size']
        inserted = 0
        
        query_templates = [
            ("SELECT * FROM products WHERE category_id = '%s'", 1),
            ("SELECT * FROM orders WHERE user_id = '%s'", 1),
            ("SELECT * FROM users WHERE email = '%s'", 1),
            ("SELECT COUNT(*) FROM orders WHERE status = '%s'", 1),
            ("SELECT * FROM products WHERE price BETWEEN %s AND %s", 2)
        ]
        
        try:
            query_data = []
            for i in range(total):
                query_id = str(uuid.uuid4())
                template, num_args = random.choice(query_templates)
                if num_args == 1:
                    query_text = template % (self.generate_random_string(8),)
                else:
                    query_text = template % (random.randint(1000, 5000), random.randint(5000, 10000))
                query_hash = self.generate_random_string(64)
                execution_time_ms = random.randint(10, 5000)
                rows_examined = random.randint(0, 100000)
                rows_returned = random.randint(0, min(rows_examined, 1000))
                used_index = random.choice([True, False])
                index_name = f"idx_{self.generate_random_string(10)}" if used_index else None
                execution_plan = f'{{"plan": "plan_{i}"}}'
                created_at = datetime.now() - timedelta(days=random.randint(0, 30))
                
                query_data.append((
                    query_id,
                    query_text,
                    query_hash,
                    execution_time_ms,
                    rows_examined,
                    rows_returned,
                    used_index,
                    index_name,
                    execution_plan,
                    created_at
                ))
                
                if len(query_data) >= batch_size:
                    execute_batch(
                        cursor,
                        """INSERT INTO query_history (id, query_text, query_hash, execution_time_ms, rows_examined, rows_returned, used_index, index_name, execution_plan, created_at)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                        query_data
                    )
                    conn.commit()
                    inserted += len(query_data)
                    print(f"  Inserted {inserted}/{total} query history entries", end='\r')
                    query_data = []
            
            if query_data:
                execute_batch(
                    cursor,
                    """INSERT INTO query_history (id, query_text, query_hash, execution_time_ms, rows_examined, rows_returned, used_index, index_name, execution_plan, created_at)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    query_data
                )
                conn.commit()
                inserted += len(query_data)
            
            cursor.close()
            print(f"\n✓ Inserted {inserted} query history entries")
            return inserted
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error generating query history: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def cleanup_data(self):
        """Clean up existing data before inserting new data"""
        print("\n[0/7] Cleaning up existing data...")
        conn = self.get_connection()
        cursor = conn.cursor()
        
        try:
            # Delete in reverse order of foreign key dependencies
            tables_to_clean = [
                'query_history',
                'audit_log',
                'order_items',
                'orders',
                'daily_summary',
                'products',
                'categories',
                'users'
            ]
            
            for table in tables_to_clean:
                try:
                    cursor.execute(f"TRUNCATE TABLE {table} CASCADE")
                    conn.commit()
                    print(f"  ✓ Cleaned {table}")
                except Exception as e:
                    # Table might not exist, continue
                    print(f"  ⚠ Could not clean {table}: {e}")
                    conn.rollback()
                    continue
            
            # Reset sequences if any
            try:
                cursor.execute("SELECT setval(pg_get_serial_sequence('users', 'id'), 1, false)")
                conn.commit()
            except:
                pass  # UUID doesn't use sequences
            
            cursor.close()
            print("✓ Data cleanup completed")
            
        except Exception as e:
            conn.rollback()
            print(f"\n✗ Error during cleanup: {e}")
            raise
        finally:
            self.return_connection(conn)
    
    def run(self, cleanup: bool = True):
        """Run the complete data generation process"""
        start_time = time.time()
        print("=" * 60)
        print("Bulk Data Generator for Portfolio Database")
        print("=" * 60)
        print(f"Configuration:")
        print(f"  Users: {self.config['users']['total']:,}")
        print(f"  Categories: {self.config['categories']['total']:,}")
        print(f"  Products: {self.config['products']['total']:,}")
        print(f"  Orders: {self.config['orders']['total']:,}")
        print(f"  Batch size: {self.config['batch_size']:,}")
        print(f"  Parallel workers: {self.config.get('parallel_workers', 4)}")
        print("=" * 60)
        
        try:
            self.connect()
            
            # Clean up existing data if requested
            if cleanup:
                self.cleanup_data()
            
            # Generate data in order (respecting foreign key constraints)
            self.generate_users()
            self.generate_categories()
            self.generate_products()
            self.generate_orders()
            self.generate_order_items()
            self.generate_audit_log()
            self.generate_query_history()
            
            elapsed_time = time.time() - start_time
            print("\n" + "=" * 60)
            print("✓ Data generation completed successfully!")
            print(f"  Total time: {elapsed_time:.2f} seconds ({elapsed_time/60:.2f} minutes)")
            print("=" * 60)
            
        except Exception as e:
            print(f"\n✗ Data generation failed: {e}")
            raise
        finally:
            self.close()


def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(description='Generate bulk test data for Portfolio database')
    parser.add_argument('--host', default=DB_CONFIG['host'], help='Database host')
    parser.add_argument('--port', type=int, default=DB_CONFIG['port'], help='Database port')
    parser.add_argument('--database', default=DB_CONFIG['database'], help='Database name')
    parser.add_argument('--user', default=DB_CONFIG['user'], help='Database user')
    parser.add_argument('--password', default=DB_CONFIG['password'], help='Database password')
    parser.add_argument('--batch-size', type=int, default=CONFIG['batch_size'], help='Batch size for inserts')
    parser.add_argument('--scale', type=float, default=1.0, help='Scale factor (1.0 = default, 0.5 = half, 2.0 = double)')
    parser.add_argument('--no-cleanup', action='store_true', help='Skip cleanup of existing data before insertion')
    parser.add_argument('--workers', type=int, default=CONFIG['parallel_workers'], help='Number of parallel workers (default: 4)')
    
    args = parser.parse_args()
    
    # Update config with arguments
    db_config = {
        'host': args.host,
        'port': args.port,
        'database': args.database,
        'user': args.user,
        'password': args.password
    }
    
    config = CONFIG.copy()
    config['batch_size'] = args.batch_size
    config['parallel_workers'] = args.workers
    config['users']['total'] = int(config['users']['total'] * args.scale)
    config['categories']['total'] = int(config['categories']['total'] * args.scale)
    config['products']['total'] = int(config['products']['total'] * args.scale)
    config['orders']['total'] = int(config['orders']['total'] * args.scale)
    config['audit_log']['total'] = int(config['audit_log']['total'] * args.scale)
    config['query_history']['total'] = int(config['query_history']['total'] * args.scale)
    
    generator = BulkDataGenerator(config, db_config)
    generator.run(cleanup=not args.no_cleanup)


if __name__ == '__main__':
    main()
