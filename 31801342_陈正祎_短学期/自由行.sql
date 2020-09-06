/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2020/9/6 13:53:00                            */
/*==============================================================*/


drop table if exists Order_info;

drop table if exists area_info;

drop table if exists hotel_info;

drop table if exists line_hotel;

drop table if exists line_info;

drop table if exists line_restaurant;

drop table if exists line_spot;

drop table if exists manager_info;

drop table if exists order_user_info;

drop table if exists product_info;

drop table if exists restaurant_info;

drop table if exists spot_info;

drop table if exists user_info;

/*==============================================================*/
/* Table: Order_info                                            */
/*==============================================================*/
create table Order_info
(
   order_id             int not null,
   product_id           int,
   user_id              int,
   total_peo            int,
   begin_city           varchar(20),
   price                date,
   order_time           date,
   order_state          varchar(20),
   hotel_goal           decimal(2),
   line_goal            decimal(2),
   product_goal         decimal(2),
   total_goal           decimal(2),
   comment              varchar(140),
   primary key (order_id)
);

/*==============================================================*/
/* Table: area_info                                             */
/*==============================================================*/
create table area_info
(
   area_info            int not null,
   area_name            varchar(20) not null,
   area_num             int not null,
   primary key (area_info)
);

/*==============================================================*/
/* Table: hotel_info                                            */
/*==============================================================*/
create table hotel_info
(
   hotel_id             int not null,
   area_info            int,
   hotel_name           varchar(20) not null,
   area_num             int not null,
   hotel_des            varchar(140),
   hotel_add            varchar(50) not null,
   hotel_star           decimal(1) not null,
   hotel_opentime       varchar(20) not null,
   primary key (hotel_id)
);

/*==============================================================*/
/* Table: line_hotel                                            */
/*==============================================================*/
create table line_hotel
(
   hotel_id             int not null,
   line_id              varchar(20) not null,
   data                 date,
   primary key (hotel_id, line_id)
);

/*==============================================================*/
/* Table: line_info                                             */
/*==============================================================*/
create table line_info
(
   line_id              varchar(20) not null,
   line_name            varchar(20) not null,
   line_des             varchar(140),
   line_dest            varchar(20) not null,
   line_day             int not null,
   line_price           decimal(2) not null,
   line_spe             varchar(140),
   lin_trafictip        varchar(50),
   primary key (line_id)
);

/*==============================================================*/
/* Table: line_restaurant                                       */
/*==============================================================*/
create table line_restaurant
(
   rest_info            int not null,
   line_id              varchar(20) not null,
   price                decimal(2),
   primary key (rest_info, line_id)
);

/*==============================================================*/
/* Table: line_spot                                             */
/*==============================================================*/
create table line_spot
(
   spot_id              int not null,
   line_id              varchar(20) not null,
   rec_data             date not null,
   primary key (spot_id, line_id)
);

/*==============================================================*/
/* Table: manager_info                                          */
/*==============================================================*/
create table manager_info
(
   emp_id               int not null,
   emp_name             varchar(20) not null,
   emp_pwd              varchar(50) not null,
   emp_pow              int not null,
   primary key (emp_id)
);

/*==============================================================*/
/* Table: order_user_info                                       */
/*==============================================================*/
create table order_user_info
(
   user_num             int not null,
   order_id             int not null,
   primary key (user_num, order_id)
);

/*==============================================================*/
/* Table: product_info                                          */
/*==============================================================*/
create table product_info
(
   product_id           int not null,
   line_id              varchar(20) not null,
   begin_time           date,
   primary key (product_id)
);

/*==============================================================*/
/* Table: restaurant_info                                       */
/*==============================================================*/
create table restaurant_info
(
   rest_info            int not null,
   area_info            int,
   rest_name            varchar(20) not null,
   area_num             int not null,
   rest_des             text,
   rest_spec            varchar(50),
   rest_price           decimal(2),
   primary key (rest_info)
);

/*==============================================================*/
/* Table: spot_info                                             */
/*==============================================================*/
create table spot_info
(
   spot_id              int not null,
   area_info            int,
   spot_name            varchar(20) not null,
   area_num             int not null,
   spot_price           int not null,
   spot_des             text,
   spot_time            int,
   spot_rec_id          int,
   primary key (spot_id)
);

/*==============================================================*/
/* Table: user_info                                             */
/*==============================================================*/
create table user_info
(
   user_num             int not null,
   user_name            varchar(20) not null,
   user_sex             varchar(4) not null,
   user_pwd             varchar(50),
   user_pho             varchar(20),
   user_email           varchar(20),
   user_city            varchar(20),
   register_time        datetime,
   IsVIP                bool,
   VIPdue               datetime,
   ID_card              varchar(20) not null,
   primary key (user_num)
);

alter table Order_info add constraint FK_Relationship_15 foreign key (product_id)
      references product_info (product_id) on delete restrict on update restrict;

alter table hotel_info add constraint FK_Relationship_6 foreign key (area_info)
      references area_info (area_info) on delete restrict on update restrict;

alter table line_hotel add constraint FK_line_hotel foreign key (hotel_id)
      references hotel_info (hotel_id) on delete restrict on update restrict;

alter table line_hotel add constraint FK_line_hotel2 foreign key (line_id)
      references line_info (line_id) on delete restrict on update restrict;

alter table line_restaurant add constraint FK_line_restaurant foreign key (rest_info)
      references restaurant_info (rest_info) on delete restrict on update restrict;

alter table line_restaurant add constraint FK_line_restaurant2 foreign key (line_id)
      references line_info (line_id) on delete restrict on update restrict;

alter table line_spot add constraint FK_line_spot foreign key (spot_id)
      references spot_info (spot_id) on delete restrict on update restrict;

alter table line_spot add constraint FK_line_spot2 foreign key (line_id)
      references line_info (line_id) on delete restrict on update restrict;

alter table order_user_info add constraint FK_Relationship_1 foreign key (user_num)
      references user_info (user_num) on delete restrict on update restrict;

alter table order_user_info add constraint FK_Relationship_2 foreign key (order_id)
      references Order_info (order_id) on delete restrict on update restrict;

alter table product_info add constraint FK_Relationship_11 foreign key (line_id)
      references line_info (line_id) on delete restrict on update restrict;

alter table restaurant_info add constraint FK_Relationship_5 foreign key (area_info)
      references area_info (area_info) on delete restrict on update restrict;

alter table spot_info add constraint FK_Relationship_4 foreign key (area_info)
      references area_info (area_info) on delete restrict on update restrict;

