CREATE TABLE IF NOT EXISTS `tag` (
  `id_tag` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id_tag`))
ENGINE = InnoDB;

ALTER TABLE commit ADD COLUMN id_tag INT NULL;

ALTER TABLE commit ADD CONSTRAINT `fk_commit_tag`
    FOREIGN KEY (`id_tag`)
    REFERENCES tag (`id_tag`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;
    
ALTER TABLE `commit` 
CHANGE COLUMN `status` `status` VARCHAR(20) NULL DEFAULT NULL;

ALTER TABLE `repository` 
CHANGE COLUMN `status` `status` VARCHAR(20) NULL DEFAULT NULL ;

ALTER TABLE `tag` 
ADD COLUMN `id_repository` INT UNSIGNED NULL AFTER `name`,
ADD INDEX `fk_repository_idx` (`id_repository` ASC);

ALTER TABLE `tag` 
ADD CONSTRAINT `fk_repository`
  FOREIGN KEY (`id_repository`)
  REFERENCES `repository` (`id_repository`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
ALTER TABLE `tag` 
ADD COLUMN `date` TIMESTAMP NULL AFTER `id_repository`,
ADD COLUMN `author_name` VARCHAR(100) NULL AFTER `date`;