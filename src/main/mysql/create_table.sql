USE garbagecollection;

-- auto-generated definition
create table user
(
    id          int auto_increment
        primary key,
    password    varchar(64)  null,
    name        varchar(64)  null,
    sex         varchar(2)   null,
    stage       varchar(64)  null,
    msg         varchar(64)  null,
    img         varchar(256) null,
    phone       varchar(256) null,
    update_time timestamp    null,
    create_time timestamp    null
);

-- auto-generated definition
create table qq_user
(
    id          int auto_increment
        primary key,
    qq_id       varchar(64) null,
    phone       char(11)    null,
    update_time timestamp   null,
    create_time timestamp   null
);
