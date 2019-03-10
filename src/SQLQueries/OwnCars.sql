create table OwnCars (
	id_ int not null auto_increment,
	
    modelId_ int not null,

    primary key (id_),
    foreign key (modelId_) references Models(id_)
);

# drop table OwnCars;

insert OwnCars (modelId_) values
(1), (3), (5), (7);

# delete from OwnCars where id_ > 0;

select * from OwnCars;

select Makes.name_ as make, Models.name_ as model from Makes, Models, OwnCars
	where Makes.id_ = Models.makeId_ and OwnCars.modelId_ = Models.id_;
