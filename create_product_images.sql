create table product_images ( product character varying(4) NOT NULL, image bytea NOT NULL);
alter table only product_images add constraint product_image_pkey PRIMARY KEY (product);
alter table only product_images add constraint "$1" FOREIGN KEY (product) references product(product_code) ON UPDATE CASCADE ON DELETE CASCADE;
