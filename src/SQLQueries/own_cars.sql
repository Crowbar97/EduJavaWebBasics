create table `own_cars` (
	`id` int not null auto_increment,
	
    `model_id` int not null unique,
    `count` int not null,

    primary key (`id`),
    foreign key (`model_id`) references `models`(`id`)
);

select * from `own_cars`;
# drop table `own_cars`;

delimiter //
create procedure `get_cars`()
begin
	select `marks`.`name` as `mark`, `models`.`name` as `model`, `own_cars`.`count` as `count` from `marks`, `models`, `own_cars`
		where `marks`.`id` = `models`.`mark_id` and `own_cars`.`model_id` = `models`.`id`;
end //
delimiter ;

call `get_cars`();
# drop procedure `get_cars`;

delimiter //
create procedure `save_cars`(in `cars` text)
begin
	truncate `own_cars`;
    
    create temporary table if not exists `car_batch`
		select 'XXXXXXXXXX' as `mark_name`, 'XXXXXXXXXX' as `model_name`, 0 as `count`;
    
	set @q = concat('insert car_batch values ', `cars`);
	prepare `stmt` from @q;
	execute `stmt`;
	deallocate prepare `stmt`;
    
    insert `own_cars` (`model_id`, `count`)
		select `models`.`id`, `car_batch`.`count` from `models`, `marks`, `car_batch`
			where `models`.`name` = `car_batch`.`model_name` and `marks`.`name` = `car_batch`.`mark_name` and `models`.`mark_id` = `marks`.`id`;
            
	drop temporary table `car_batch`;
end //
delimiter ;

call `save_cars`('(\'merc\', \'w140\', 5),(\'bmw\', \'e39\', 6)');
# drop procedure `save_cars`;
