CREATE TABLE franquicia (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    CONSTRAINT uq_franquicia_nombre UNIQUE (nombre)
);

CREATE TABLE sucursal (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    franquicia_id BIGINT      NOT NULL,
    CONSTRAINT fk_sucursal_franquicia FOREIGN KEY (franquicia_id) REFERENCES franquicia (id) ON DELETE CASCADE,
    CONSTRAINT uq_sucursal_nombre_franquicia UNIQUE (franquicia_id, nombre)
);

CREATE TABLE producto (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    stock       INT          NOT NULL,
    sucursal_id BIGINT       NOT NULL,
    CONSTRAINT fk_producto_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal (id) ON DELETE CASCADE,
    CONSTRAINT uq_producto_nombre_sucursal UNIQUE (sucursal_id, nombre),
    CONSTRAINT ck_producto_stock CHECK (stock >= 0)
);

CREATE INDEX idx_sucursal_franquicia ON sucursal (franquicia_id);
CREATE INDEX idx_producto_sucursal_stock ON producto (sucursal_id, stock);