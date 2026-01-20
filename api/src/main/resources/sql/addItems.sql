INSERT INTO categories(name, parent_id)
VALUES ('Обувь', null),
       ('Одежда', null);

INSERT INTO brands(name, image)
VALUES ('Carhartt', 'D:\jprojects\myApp\images\logo.png');

INSERT INTO items(name, brand_id, category_id, description, price, weight)
VALUES ('Carhartt Chase T-Shirt', 1, 2,
        'Супер крутая футболка 100% SWAG внатуре',
        1000, 100),
       ('Carhartt Detroit Jacket', 1, 2,
        'Супер жакетик',
        10000, 1000);

INSERT INTO sizes(name, chest_cm, waist_cm, length_cm)
VALUES ('M', 100, 10, 10),
       ('XL', 100, 100, 100);

INSERT INTO item_sizes(item_id, size_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2);

INSERT INTO item_images(item_id, image, is_main)
VALUES (1, 'D:\jprojects\myApp\images\photo1.png', false),
       (1, 'D:\jprojects\myApp\images\photo2.jpg', true),
       (2, 'D:\jprojects\myApp\images\image.jpg', true);