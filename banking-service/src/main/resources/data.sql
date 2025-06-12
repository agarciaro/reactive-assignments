-- Insertar cuentas de prueba
INSERT INTO accounts (id, account_number, owner_name, balance, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'ACC001', 'Juan Pérez', 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001', 'ACC002', 'María García', 5000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440002', 'ACC003', 'Carlos López', 15000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440003', 'ACC004', 'Ana Martínez', 2500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440004', 'ACC005', 'Luis Rodríguez', 7500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar algunas transacciones de ejemplo
INSERT INTO transactions (id, from_account_id, to_account_id, amount, timestamp, status, description)
VALUES
    ('750e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', 100.00, CURRENT_TIMESTAMP, 'APPROVED', 'Transferencia inicial de prueba'),
    ('750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 200.00, CURRENT_TIMESTAMP, 'APPROVED', 'Pago de servicios'); 