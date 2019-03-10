create table BaseCars (
	id_ int not null auto_increment,
	
    modelId_ int not null,

    primary key (id_),
    foreign key (modelId_) references Models(id_)
);

# drop table BaseCars;

insert BaseCars (modelId_)
	select id_ from Models;

# delete from BaseCars where id_ > 0;

select * from BaseCars;

select Makes.name_ as make, Models.name_ as model from Makes, Models, BaseCars
	where Makes.id_ = Models.makeId_ and BaseCars.modelId_ = Models.id_;
