ALTER TABLE `gitfactor`.`repository` 
ADD COLUMN `size` INT NULL DEFAULT NULL AFTER `total_stars`,
ADD COLUMN `default_branch` VARCHAR(255) NULL DEFAULT NULL AFTER `size`;