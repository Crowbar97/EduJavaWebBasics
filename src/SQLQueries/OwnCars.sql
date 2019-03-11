create table OwnCars (
	id_ int not null auto_increment,
	
    modelId_ int not null unique,
    count_ int not null,

    primary key (id_),
    foreign key (modelId_) references Models(id_)
);

select * from OwnCars;
# drop table OwnCars;

DELIMITER //
create procedure getCars()
begin
	select Makes.name_ as make, Models.name_ as model, OwnCars.count_ as count from Makes, Models, OwnCars
		where Makes.id_ = Models.makeId_ and OwnCars.modelId_ = Models.id_;
end //
DELIMITER ;

call getCars();
# drop procedure getCars;

DELIMITER //
create procedure saveCars(in cars text)
begin
	truncate OwnCars;
    
    create temporary table if not exists CarBatch
		select 'XXXXXXXXXX' as makeName_, 'XXXXXXXXXX' as modelName_, 0 as count_;
    
	set @q = concat('insert CarBatch values ', cars);
	prepare stmt from @q;
	execute stmt;
	deallocate prepare stmt;
    
    insert OwnCars (modelId_, count_)
		select Models.id_, CarBatch.count_ from Models, Makes, CarBatch
			where Models.name_ = CarBatch.modelName_ and Makes.name_ = CarBatch.makeName_ and Models.makeId_ = Makes.id_;
            
	drop temporary table CarBatch;
end //
DELIMITER ;

call saveCars('(\'merc\', \'w140\', 5),(\'bmw\', \'e39\', 6)');
# drop procedure saveCars;
