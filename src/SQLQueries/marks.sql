create table `marks` (
	`id` int not null auto_increment,
    `name` varchar(100) not null unique,
    
     primary key (`id`)
);

# drop table `marks`;

insert `marks` (`name`) values
('merc'),
('bmw'),
('audi');

# truncate `marks`;

select * from `marks`;
