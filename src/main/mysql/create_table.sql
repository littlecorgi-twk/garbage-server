USE garbagecollection;

create table user
(
    id          int auto_increment,
    password    varchar(64)  null,
    name        varchar(64)  null,
    sex         varchar(2)   null,
    stage       varchar(64)  null,
    msg         varchar(64)  null,
    img         varchar(256) null,
    photo       varchar(256) null,
    update_time timestamp    null,
    create_time timestamp    null,
    constraint user_pk
        primary key (id)
);

create table qq_user
(
    id          int auto_increment,
    qq_id       varchar(64) null,
    phone       char(11)    null,
    update_time timestamp   null,
    create_time timestamp   null,
    constraint qq_user_pk
        primary key (id)
);
