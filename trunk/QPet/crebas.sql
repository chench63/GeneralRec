/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2011/6/22 12:32:10                           */
/*==============================================================*/


drop table if exists ItemLib;

drop table if exists ItemMatrix;

drop table if exists Matrix;

drop table if exists PetLib;

drop table if exists ServiceItem;

drop table if exists ServicePet;

drop table if exists User;

drop table if exists usrFrontInfo;

/*==============================================================*/
/* Table: ItemLib                                               */
/*==============================================================*/
create table ItemLib
(
   imgUrl               varchar(255),
   context              varchar(50),
   webUrl               varchar(255),
   Ext                  varchar(255),
   itemId               int not null,
   primary key (itemId)
);

/*==============================================================*/
/* Table: ItemMatrix                                            */
/*==============================================================*/
create table ItemMatrix
(
   imgUrl               varchar(255),
   context              varchar(50),
   webUrl               varchar(255),
   itemId               int not null,
   primary key (itemId)
);

/*==============================================================*/
/* Table: Matrix                                                */
/*==============================================================*/
create table Matrix
(
   itemId               int,
   usrId                int,
   score                int,
   matrixId             int not null ,
   primary key (matrixId)
);

/*==============================================================*/
/* Table: PetLib                                                */
/*==============================================================*/
create table PetLib
(
   petId                int not null ,
   srcUrl               varchar(255),
   petExt               varchar(255),
   petExt1              varchar(255),
   primary key (petId)
);

/*==============================================================*/
/* Table: ServiceItem                                           */
/*==============================================================*/
create table ServiceItem
(
   usrId                int,
   Ext                  varchar(255),
   serviceItemId        int not null ,
   itemId               int,
   primary key (serviceItemId)
);

/*==============================================================*/
/* Table: ServicePet                                            */
/*==============================================================*/
create table ServicePet
(
   petId                int,
   usrId                int,
   servicePetId         int not null ,
   level                int,
   exp                  int,
   Ext                  varchar(255),
   primary key (servicePetId)
);

/*==============================================================*/
/* Table: User                                                  */
/*==============================================================*/
create table User
(
   usrId                int not null ,
   usrToken             varchar(255),
   uExt1                varchar(255),
   uExt2                varchar(255),
   primary key (usrId)
);

/*==============================================================*/
/* Table: usrFrontInfo                                          */
/*==============================================================*/
create table usrFrontInfo
(
   serviceItemId        int,
   usrId                int,
   itemId               int,
   servicePetId         int,
   frontId              int not null ,
   primary key (frontId)
);

alter table Matrix add constraint FK_Reference_1 foreign key (itemId)
      references ItemMatrix (itemId) on delete restrict on update restrict;

alter table Matrix add constraint FK_Reference_2 foreign key (usrId)
      references User (usrId) on delete restrict on update restrict;

alter table ServiceItem add constraint FK_Reference_3 foreign key (usrId)
      references User (usrId) on delete restrict on update restrict;

alter table ServiceItem add constraint FK_Reference_4 foreign key (itemId)
      references ItemLib (itemId) on delete restrict on update restrict;

alter table ServicePet add constraint FK_Reference_5 foreign key (petId)
      references PetLib (petId) on delete restrict on update restrict;

alter table ServicePet add constraint FK_Reference_6 foreign key (usrId)
      references User (usrId) on delete restrict on update restrict;

