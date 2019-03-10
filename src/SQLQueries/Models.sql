create table Models (
	id_ int not null auto_increment,
    name_ varchar(100) not null,
    makeId_ int not null,
    
     primary key (id_),
     foreign key (makeId_) references Makes(id_)
);

# drop table Models;

insert Models (name_, makeId_) values
('w123', 1),
('w124', 1),
('w140', 1),
('e34', 2),
('e39', 2),
('e38', 2),
('b4', 3),
('c4', 3);

# delete from Models where id_ > 0;

select * from Models;

select Makes.name_ as make, Models.name_ as model from Makes, Models
	where Makes.id_ = Models.makeId_;