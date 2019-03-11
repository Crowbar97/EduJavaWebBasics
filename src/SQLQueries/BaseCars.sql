create table BaseCars (
	id_ int not null auto_increment,
	
    modelId_ int not null unique,

    primary key (id_),
    foreign key (modelId_) references Models(id_)
);

insert BaseCars (modelId_)
	select id_ from Models;
    
select * from BaseCars;
# truncate BaseCars;
# drop table BaseCars;

DELIMITER //
create procedure getBaseCars()
begin
	select Makes.name_ as make, Models.name_ as model from Makes, Models, BaseCars
		where Makes.id_ = Models.makeId_ and BaseCars.modelId_ = Models.id_;
end //
DELIMITER ;

call getBaseCars();
# drop procedure getBaseCars;


