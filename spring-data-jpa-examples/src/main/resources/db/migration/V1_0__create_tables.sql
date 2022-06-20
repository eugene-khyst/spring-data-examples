create table author (
    id serial primary key,
    full_name varchar(255) not null
);

create table book (
    id serial primary key,
    isbn varchar(255) unique not null,
    publication_date date not null,
    title varchar(255) not null
);

create table book_authors (
    book_id integer not null,
    authors_id integer not null
);

create table book_categories (
    book_id int8 not null,
    categories_id int8 not null,
    primary key (book_id, categories_id)
);

create table book_rating (
    id int8 not null,
    number_of_ratings int4 not null,
    rating numeric(19, 2),
    version int4 not null,
    book_id int8,
    primary key (id)
);

create table book_with_batch_size (
    id int8 not null,
    isbn varchar(255),
    publication_date date,
    title varchar(255),
    primary key (id)
);

create table book_with_batch_size_authors (
    book_with_batch_size_id int8 not null,
    authors_id int8 not null
);

create table book_with_batch_size_categories (
    book_with_batch_size_id int8 not null,
    categories_id int8 not null,
    primary key (book_with_batch_size_id, categories_id)
);

create table book_with_fetch_mode_join (
    id int8 not null,
    isbn varchar(255),
    publication_date date,
    title varchar(255),
    primary key (id)
);

create table book_with_fetch_mode_join_authors (
    book_with_fetch_mode_join_id int8 not null,
    authors_id int8 not null
);

create table book_with_fetch_mode_join_categories (
    book_with_fetch_mode_join_id int8 not null,
    categories_id int8 not null,
    primary key (book_with_fetch_mode_join_id, categories_id)
);

create table book_with_fetch_mode_select (
    id int8 not null,
    isbn varchar(255),
    publication_date date,
    title varchar(255),
    primary key (id)
);

create table book_with_fetch_mode_select_authors (
    book_with_fetch_mode_select_id int8 not null,
    authors_id int8 not null
);

create table book_with_fetch_mode_select_categories (
    book_with_fetch_mode_select_id int8 not null,
    categories_id int8 not null,
    primary key (book_with_fetch_mode_select_id, categories_id)
);

create table book_with_fetch_mode_subselect (
    id int8 not null,
    isbn varchar(255),
    publication_date date,
    title varchar(255),
    primary key (id)
);

create table book_with_fetch_mode_subselect_authors (
    book_with_fetch_mode_subselect_id int8 not null,
    authors_id int8 not null
);

create table book_with_fetch_mode_subselect_categories (
    book_with_fetch_mode_subselect_id int8 not null,
    categories_id int8 not null,
    primary key (book_with_fetch_mode_subselect_id, categories_id)
);

create table book_with_multiple_bags (
    id int8 not null,
    isbn varchar(255),
    publication_date date,
    title varchar(255),
    primary key (id)
);

create table book_with_multiple_bags_authors (
    book_with_multiple_bags_id int8 not null,
    authors_id int8 not null
);

create table book_with_multiple_bags_categories (
    book_with_multiple_bags_id int8 not null,
    categories_id int8 not null
);

create table category (
    id int8 not null,
    name varchar(255),
    primary key (id)
);