-- Usuários
CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100)        NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categorias de investimentos
CREATE TABLE investment_categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    risk_level  VARCHAR(20) CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Dados iniciais de categorias
INSERT INTO investment_categories (name, description, risk_level)
VALUES ('ACOES', 'Ações na bolsa de valores', 'HIGH'),
       ('FUNDOS_IMOBILIARIOS', 'Fundos de investimento imobiliário', 'MEDIUM'),
       ('RENDA_FIXA', 'Tesouro Direto, CDBs, LCIs', 'LOW'),
       ('CRIPTOMOEDAS', 'Bitcoin, Ethereum, etc', 'HIGH'),
       ('FUNDOS_INVESTIMENTO', 'Fundos de ações, multimercado', 'MEDIUM');


-- Ativos (ações, FIIs, cripto, etc)
CREATE TABLE assets (
                        id BIGSERIAL PRIMARY KEY,
                        ticker VARCHAR(20) NOT NULL UNIQUE,
                        name VARCHAR(200) NOT NULL,
                        category_id BIGINT NOT NULL REFERENCES investment_categories(id),
                        current_price DECIMAL(15, 2),
                        last_update TIMESTAMP,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_assets_ticker ON assets(ticker);
CREATE INDEX idx_assets_category ON assets(category_id);

-- Carteiras do usuário
CREATE TABLE portfolios
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (user_id, name)
);

-- Investimentos (posições do usuário)
CREATE TABLE investments
(
    id            BIGSERIAL PRIMARY KEY,
    portfolio_id  BIGINT         NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    asset_id      BIGINT         NOT NULL REFERENCES assets (id) ON DELETE CASCADE,
    quantity      DECIMAL(20, 8) NOT NULL CHECK (quantity > 0),
    average_price DECIMAL(15, 2) NOT NULL,
    purchase_date DATE           NOT NULL,
    notes         TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (portfolio_id, asset_id)
);

CREATE INDEX idx_investments_portfolio ON investments (portfolio_id);
CREATE INDEX idx_investments_asset ON investments (asset_id);

-- Transações (histórico de compra/venda)
CREATE TABLE transactions
(
    id               BIGSERIAL PRIMARY KEY,
    portfolio_id     BIGINT         NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    asset_id         BIGINT         NOT NULL REFERENCES assets (id) ON DELETE CASCADE,
    type             VARCHAR(10)    NOT NULL CHECK (type IN ('BUY', 'SELL')),
    quantity         DECIMAL(20, 8) NOT NULL CHECK (quantity > 0),
    price            DECIMAL(15, 2) NOT NULL CHECK (price > 0),
    total_amount     DECIMAL(15, 2) NOT NULL CHECK (total_amount > 0),
    fees             DECIMAL(10, 2) DEFAULT 0 CHECK (fees >= 0),
    transaction_date DATE           NOT NULL,
    notes            TEXT,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_portfolio ON transactions (portfolio_id);
CREATE INDEX idx_transactions_date ON transactions (transaction_date DESC);

-- Histórico de preços (para gráficos de evolução)
CREATE TABLE price_history
(
    id       BIGSERIAL PRIMARY KEY,
    asset_id BIGINT         NOT NULL REFERENCES assets (id) ON DELETE CASCADE,
    price    DECIMAL(15, 2) NOT NULL CHECK (price > 0),
    date     DATE           NOT NULL,

    UNIQUE (asset_id, date)
);

CREATE INDEX idx_price_history_asset_date ON price_history (asset_id, date DESC);

-- Alertas de preço
CREATE TABLE price_alerts
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    asset_id     BIGINT         NOT NULL REFERENCES assets (id) ON DELETE CASCADE,
    target_price DECIMAL(15, 2) NOT NULL CHECK (target_price > 0),
    condition    VARCHAR(10) CHECK (condition IN ('ABOVE', 'BELOW')),
    is_active    BOOLEAN   DEFAULT TRUE,
    triggered_at TIMESTAMP,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alerts_user_active ON price_alerts (user_id, is_active);

-- Snapshot de patrimônio (para gráfico de evolução)
CREATE TABLE portfolio_snapshots
(
    id                  BIGSERIAL PRIMARY KEY,
    portfolio_id        BIGINT         NOT NULL REFERENCES portfolios (id) ON DELETE CASCADE,
    total_invested      DECIMAL(15, 2) NOT NULL CHECK (total_invested >= 0),
    current_value       DECIMAL(15, 2) NOT NULL CHECK (current_value >= 0),
    profit_loss         DECIMAL(15, 2) NOT NULL,
    profit_loss_percent DECIMAL(10, 4) NOT NULL,
    snapshot_date       DATE           NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (portfolio_id, snapshot_date)
);

CREATE INDEX idx_snapshots_portfolio_date ON portfolio_snapshots (portfolio_id, snapshot_date DESC);