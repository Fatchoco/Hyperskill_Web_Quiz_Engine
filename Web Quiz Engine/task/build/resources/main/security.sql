--drop table users;
--drop table authorities;

create table users(
	username varchar_ignorecase(50) not null primary key,
	password varchar_ignorecase(200) not null,
	enabled boolean not null
);

create table authorities (
	username varchar_ignorecase(50) not null,
	authority varchar_ignorecase(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);

insert into users (username, password, enabled) values ('bob', '$2a$04$AoXvNB.Cp48dpOrhWTp4VuKquz/3ap7gQTM2s32pa0PfHPSO7Ahrm', true);
insert into authorities (username, authority) values ('bob', 'ROLE_USER');

insert into users (username, password, enabled) values ('sara', '$2a$04$AoXvNB.Cp48dpOrhWTp4VuKquz/3ap7gQTM2s32pa0PfHPSO7Ahrm', true);
insert into authorities (username, authority) values ('sara', 'ROLE_ADMIN');