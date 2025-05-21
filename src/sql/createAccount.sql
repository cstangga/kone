-- PDB 전환 (현재 세션이 CDB에 있을 경우)
ALTER SESSION SET CONTAINER = xepdb1;

-- 사용자 생성
CREATE USER mega IDENTIFIED BY coffee;

-- 권한 부여
GRANT CONNECT, RESOURCE TO mega;

-- 테이블스페이스 지정 (옵션)
ALTER USER mega DEFAULT TABLESPACE users;
ALTER USER mega TEMPORARY TABLESPACE temp;

-- --------------------------mysql -------------------
-- 1. 데이터베이스 생성
CREATE DATABASE konedb;

-- 2. 사용자 생성 (비밀번호는 'coffee')
CREATE USER 'mega'@'%' IDENTIFIED BY 'coffee';

-- 3. 사용자에게 konedb 데이터베이스에 대한 모든 권한 부여
GRANT ALL PRIVILEGES ON konedb.* TO 'mega'@'%';

-- 4. 권한 적용
FLUSH PRIVILEGES;
