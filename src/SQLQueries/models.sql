create table `models` (
	`id` int not null auto_increment,
    `name` varchar(100) not null,
    `mark_id` int not null,
    
     primary key (`id`),
     foreign key (`mark_id`) references `marks`(`id`)
);

# drop table `models`;

insert `models` (`name`, `mark_id`) values
('w123', 1),
('w124', 1),
('w140', 1),
('e34', 2),
('e39', 2),
('e38', 2),
('b4', 3),
('c4', 3);

# truncate `models`;

select * from `models`;

select `marks`.`name` as `mark`, `models`.`name` as `model` from `marks`, `models`
	where `marks`.`id` = `models`.`mark_id`;

delimiter //
create procedure `get_models`()
begin
	select `marks`.`name` as `mark`, `models`.`name` as `model` from `marks`, `models`
		where `models`.`mark_id` = `marks`.`id`;
end //
delimiter ;

call `get_models`();
# drop procedure `get_avail_cars`;
