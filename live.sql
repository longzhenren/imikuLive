create table room
(
	id int auto_increment
		primary key,
	name varchar(15) null,
	cover varchar(25) default 'auto' not null,
	intro varchar(140) null,
	pwd varchar(8) null,
	pri tinyint(1) default 0 not null,
	app varchar(8) null,
	sign varchar(44) null,
	open tinyint(1) default 0 not null,
	constraint room_app_uindex
		unique (app),
	constraint room_name_uindex
		unique (name)
);

create table user
(
	id int auto_increment
		primary key,
	email varchar(50) null,
	nickname varchar(50) not null,
	gender int default 3 not null,
	avatar varchar(25) default 'default.png' not null,
	intro varchar(140) null,
	inner_code int default 0 not null,
	password varchar(25) not null,
	ip varchar(40) default '未知' not null,
	room int default 0 not null,
	constraint user_email_uindex
		unique (email)
);

