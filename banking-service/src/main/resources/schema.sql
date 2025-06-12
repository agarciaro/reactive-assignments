-- Eliminar tablas si existen
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;

-- Crear tabla de cuentas
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de transacciones
CREATE TABLE transactions (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    from_account_id UUID NOT NULL,
    to_account_id UUID NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    fraud_analysis VARCHAR(500),
    description VARCHAR(255),
    FOREIGN KEY (from_account_id) REFERENCES accounts(id),
    FOREIGN KEY (to_account_id) REFERENCES accounts(id),
    CHECK (amount > 0),
    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- Crear índices para mejorar rendimiento
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_transactions_from_account ON transactions(from_account_id);
CREATE INDEX idx_transactions_to_account ON transactions(to_account_id);
CREATE INDEX idx_transactions_timestamp ON transactions(timestamp);
CREATE INDEX idx_transactions_status ON transactions(status); 