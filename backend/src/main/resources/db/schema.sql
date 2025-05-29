CREATE TABLE IF NOT EXISTS rds_db_instance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    db_instance_id VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    region_id VARCHAR(50),
    status VARCHAR(50),
    engine VARCHAR(50),
    engine_version VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME
); 