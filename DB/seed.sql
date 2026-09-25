-- ========================================
-- 1. roles
-- ========================================

INSERT INTO roles (
    role_code,
    role_name,
    description
) VALUES
('READ_ONLY', '閲覧担当', '住民情報の参照を行う職員'),
('EDIT',      '編集担当', '住民情報の登録・更新を行う職員'),
('ADMIN',     'システム管理者', 'ユーザー・権限を含むシステム管理を行う職員'),
('VENDOR',    '保守業者', 'システム保守・メンテナンスを行う業者アカウント');


-- ========================================
-- 2. permissions
-- ========================================

INSERT INTO permissions (
    permission_code,
    permission_name,
    description
) VALUES
('RESIDENT_READ',   '住民情報参照', '住民情報を参照できる'),
('RESIDENT_CREATE', '住民情報登録', '住民情報を登録できる'),
('RESIDENT_UPDATE', '住民情報更新', '住民情報を更新できる'),
('RESIDENT_DELETE', '住民情報削除', '住民情報を削除できる'),
('USER_READ',       'ユーザー参照', 'システムユーザーを参照できる'),
('USER_UPDATE',     'ユーザー更新', 'システムユーザーを更新できる');


-- ========================================
-- 3. role_permissions
-- ========================================

-- READ_ONLY
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.role_code = 'READ_ONLY'
  AND p.permission_code = 'RESIDENT_READ';


-- EDIT
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.role_code = 'EDIT'
  AND p.permission_code IN (
      'RESIDENT_READ',
      'RESIDENT_CREATE',
      'RESIDENT_UPDATE'
  );


-- ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.role_code = 'ADMIN'
  AND p.permission_code IN (
      'RESIDENT_READ',
      'RESIDENT_CREATE',
      'RESIDENT_UPDATE',
      'RESIDENT_DELETE',
      'USER_READ',
      'USER_UPDATE'
  );


-- VENDOR
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.role_code = 'VENDOR'
  AND p.permission_code IN (
      'USER_READ',
      'USER_UPDATE'
  );


-- ========================================
-- 4. users
-- ========================================

INSERT INTO users (
    organization_code,
    username,
    password,
    account_type,
    enabled
) VALUES
('001', 'yamada',  'password', 'STAFF',  1),
('001', 'sato',    'password', 'STAFF',  1),
('001', 'suzuki',  'password', 'STAFF',  1),
('99',  'vendor01', 'password', 'VENDOR', 1);


-- ========================================
-- 5. user_roles
-- ========================================

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r
WHERE u.organization_code = '001'
  AND u.username = 'yamada'
  AND r.role_code = 'READ_ONLY';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r
WHERE u.organization_code = '001'
  AND u.username = 'sato'
  AND r.role_code = 'EDIT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r
WHERE u.organization_code = '001'
  AND u.username = 'suzuki'
  AND r.role_code = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r
WHERE u.organization_code = '99'
  AND u.username = 'vendor01'
  AND r.role_code = 'VENDOR';

  -- CI/CD repeat test