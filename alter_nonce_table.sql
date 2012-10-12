drop table nonces;
create table nonces (username character varying(20), nonce character varying(20), timestamp timestamp with time zone);
alter table nonces add constraint nonces_pkey primary key (username,nonce,timestamp);
alter table nonces add constraint "$1" FOREIGN KEY (username) references users(username) on update cascade on delete cascade;
