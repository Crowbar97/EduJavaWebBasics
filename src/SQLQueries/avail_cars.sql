# "avail" means "available"
create table `avail_cars` (
	`id` int not null auto_increment,
	
    `model_id` int not null unique,

    primary key (`id`),
    foreign key (`model_id`) references `models`(`id`)
);

insert `avail_cars` (`model_id`)
	select `id` from `models`;
    
select * from `avail_cars`;
# truncate `avail_cars`;
# drop table `avail_cars`;

DELIMITER //
create procedure `get_avail_cars`()
begin
	select `marks`.`name` as `mark`, `models`.`name` as `model` from `marks`, `models`, `avail_cars`
		where `avail_cars`.`model_id` = `models`.`id` and `marks`.`id` = `models`.`mark_id`;
end //
DELIMITER ;

call `get_avail_cars`();
# drop procedure `get_avail_cars`;


