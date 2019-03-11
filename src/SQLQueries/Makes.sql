create table Makes (
	id_ int not null auto_increment,
    name_ varchar(100) not null unique,
    
     primary key (id_)
);

# drop table Makes;

insert Makes (name_) values
('merc'),
('bmw'),
('audi');

# truncate Makes;

select * from Makes;