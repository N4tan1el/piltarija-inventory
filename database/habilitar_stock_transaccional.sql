-- MyISAM no soporta transacciones. InnoDB permite revertir el descuento
-- de todos los productos si alguna línea de la compra no tiene stock.
ALTER TABLE productos ENGINE = InnoDB;
