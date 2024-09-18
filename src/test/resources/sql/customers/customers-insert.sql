INSERT INTO users (id, username, password, role) VALUES (100, 'ana@gmail.com', '$2a$12$owXthEOpMCf19KQFz/u8y.Cd3pJXBtf4Z7q2vm9TGUeFAy2HRiTC6', 'ROLE_ADMIN');
INSERT INTO users (id, username, password, role) VALUES (101, 'maria@gmail.com', '$2a$12$owXthEOpMCf19KQFz/u8y.Cd3pJXBtf4Z7q2vm9TGUeFAy2HRiTC6', 'ROLE_CLIENTE');
INSERT INTO users (id, username, password, role) VALUES (102, 'bob@gmail.com', '$2a$12$owXthEOpMCf19KQFz/u8y.Cd3pJXBtf4Z7q2vm9TGUeFAy2HRiTC6', 'ROLE_CLIENTE');
INSERT INTO users (id, username, password, role) VALUES (103, 'tobias@gmail.com', '$2a$12$owXthEOpMCf19KQFz/u8y.Cd3pJXBtf4Z7q2vm9TGUeFAy2HRiTC6', 'ROLE_CLIENTE');

INSERT INTO customers (id, nome, cpf, id_usuario) VALUES (11, 'Maria Santos', '89087145020', 101);
INSERT INTO customers (id, nome, cpf, id_usuario) VALUES (12, 'Roberto Silva', '92775436048', 102);