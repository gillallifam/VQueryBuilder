USE `vqbuilder`;
-- BBBBBBBBBBBBBBBBBBBBBBBBB
-- 666666666666666666666666666
drop procedure if exists sp_vertabelas;
delimiter #
create procedure sp_vertabelas(p_bd varchar(300))
begin
	select table_name 'tabelas' from information_schema.tables where table_schema = p_bd;
end#
delimiter ;
call sp_vertabelas('vqbuilder');

-- 7777777777777777777777777777777777777
drop procedure if exists sp_listarcampos;
delimiter #
create procedure sp_listarcampos(p_bd varchar(300), p_table varchar(300))
begin
	SELECT COLUMN_NAME as 'campos' FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = p_bd AND TABLE_NAME = p_table;
end#
delimiter ;
call sp_listarcampos('vqbuilder','cliente');
desc cliente;
show create table cliente;
SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = Schema() AND TABLE_NAME = 'cliente';

-- 8888888888888888888888888888888
drop procedure if exists sp_listarchaves;
delimiter #
create procedure sp_listarchaves(p_bd varchar(300), p_table varchar(300))
begin
	SELECT
		column_name AS 'keys' FROM	information_schema.key_column_usage
	WHERE
		table_schema = p_bd AND table_name = p_table;
end#
delimiter ;
call sp_listarchaves('vqbuilder','venda');

-- 99999999999999999999999999
drop procedure if exists sp_listarrelacoes;
delimiter #
create procedure sp_listarrelacoes(p_bd varchar(300), p_table varchar(300))
begin
	SELECT
    referenced_table_name AS 'relacionamentos'
FROM
    information_schema.key_column_usage
WHERE
  referenced_table_name IS NOT NULL and table_schema = p_bd AND table_name = p_table;
end#
delimiter ;
call sp_listarrelacoes('vqbuilder','funcionario');

-- 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 
drop procedure if exists sp_reladoNnomes;
delimiter #
create procedure sp_reladoNnomes(p_bd varchar(300), p_table1 varchar(300), p_table2 varchar(300))
begin
declare rsp varchar(500) default "";
declare stmp varchar(1000) default "";
declare isRelated boolean default false;
set isRelated=(SELECT 1
				WHERE p_table2 in (SELECT
									referenced_table_name
									FROM
									information_schema.key_column_usage
									WHERE
									referenced_table_name IS NOT NULL and table_schema = p_bd AND table_name = p_table1));
	if(isRelated is null)then
		set isRelated=(SELECT 1
		WHERE p_table1 in (SELECT
							referenced_table_name
							FROM
							information_schema.key_column_usage
							WHERE
							referenced_table_name IS NOT NULL and table_schema = p_bd AND table_name = p_table2));
		if(isRelated is null)then
			set rsp= concat("As tabelas nao tem relaçao.");
		else
			set rsp= concat(p_table2," tem relaçao com ",p_table1,'. A ',p_table2 ,' é o lado N da relaçao.');
            set stmp =(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=p_table1 and COLUMN_KEY='PRI');
            set rsp = concat(rsp,' A chave primaria é: ',stmp,'.');
            set stmp = (select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table2 and REFERENCED_TABLE_NAME = p_table1);
            set rsp = concat(rsp,' A chave estrangeira é: ',stmp,'.');
		end if;
	else
		set rsp= concat(p_table1," tem relaçao com ",p_table2,'. A ',p_table1 ,' é o lado N da relaçao.');
        set stmp =(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=p_table2 and COLUMN_KEY='PRI');
            set rsp = concat(rsp,' A chave primaria é: ',stmp,'.');
            set stmp = (select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table1 and REFERENCED_TABLE_NAME = p_table2);
            set rsp = concat(rsp,' A chave estrangeira é: ',stmp,'.');
	end if;
	select rsp as 'rsp';
end#
delimiter ;
call sp_reladoNnomes('vqbuilder','fornecedor','produto');
call sp_reladoNnomes('vqbuilder','bairro','zona');
call sp_reladoNnomes('vqbuilder','zona','bairro');
call sp_reladoNnomes('vqbuilder','itemvenda','bairro');

-- 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 
drop procedure if exists sp_tstrelacao;
delimiter #
create procedure sp_tstrelacao(p_bd varchar(300), p_table1 varchar(300), p_table2 varchar(300))
begin
declare rsp varchar(500) default "";
declare stmp varchar(1000) default "";
declare k_pri varchar(150) default "";
declare k_sec varchar(150) default "";
declare isRelated boolean default false;
set isRelated=(SELECT 1
				WHERE p_table2 in (SELECT
									referenced_table_name
									FROM
									information_schema.key_column_usage
									WHERE
									referenced_table_name IS NOT NULL and table_schema = p_bd AND table_name = p_table1));
	if(isRelated is null)then
		set isRelated=(SELECT 1
		WHERE p_table1 in (SELECT
							referenced_table_name
							FROM
							information_schema.key_column_usage
							WHERE
							referenced_table_name IS NOT NULL and table_schema = p_bd AND table_name = p_table2));
		if(isRelated is null)then
			set rsp= concat('%');
		else
            set stmp =(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=p_table1 and COLUMN_KEY='PRI');
            set rsp = concat(rsp,stmp,'#');
            set stmp = (select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table2 and REFERENCED_TABLE_NAME = p_table1);
            set rsp = concat(p_table1,'#',p_table2,'#',rsp,stmp);
		end if;
	else
        set stmp =(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=p_table2 and COLUMN_KEY='PRI');
            set rsp = concat(rsp,stmp,'#');
            set stmp = (select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table1 and REFERENCED_TABLE_NAME = p_table2);
            set rsp = concat(p_table2,'#',p_table1,'#',rsp,stmp);
	end if;
	select rsp as 'rsp';
end#
delimiter ;

call sp_tstrelacao('vqbuilder','bairro','zona');
call sp_tstrelacao('vqbuilder','zona','bairro');
call sp_tstrelacao('vqbuilder','cliente','venda');
call sp_tstrelacao('vqbuilder','venda','cliente');
call sp_tstrelacao('vqbuilder','funcionario','funcionario');


-- 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11

drop procedure if exists sp_tipochave;
delimiter #
create procedure sp_tipochave(p_bd varchar(300), p_table1 varchar(300))
begin
	declare rsp varchar(500) default "";
    declare nP,nE int default 0;
    set nP=(select count(*) from information_schema.COLUMNS where table_schema = p_bd and TABLE_NAME=p_table1 and COLUMN_KEY='PRI');
    if (nP>1)then
		set rsp = concat(rsp,'Chave primaria composta.');
    else
		set rsp = concat(rsp,'Chave primaria simples.');
    end if;
    set nE = (select count(*) from information_schema.COLUMNS where table_schema = p_bd and TABLE_NAME=p_table1 and COLUMN_KEY='MUL');
    if(nE>0)then
		set rsp = concat(rsp,' Essa tabela tem ',nE,' chaves estrangeiras.');
    end if;
    select rsp;
end#
delimiter ;
call sp_tipochave('vqbuilder','venda');
call sp_tipochave('vqbuilder','itemvenda');

drop procedure if exists sp_cometome;
delimiter #
create procedure sp_cometome(p_bd varchar(300), p_table1 varchar(300))
begin
	SELECT 
  `TABLE_NAME`  'relators'
FROM
  `INFORMATION_SCHEMA`.`KEY_COLUMN_USAGE`  -- Will fail if user don't have privilege
WHERE
  `TABLE_SCHEMA` = p_bd                -- Detect current schema in USE 
  AND `REFERENCED_TABLE_NAME`= p_table1;
end#
delimiter ;

drop procedure if exists sp_getpk;
delimiter #
create procedure sp_getpk(p_bd varchar(300), p_table1 varchar(300))
begin
	SELECT COLUMN_NAME as 'prikeys'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = p_bd
   AND TABLE_NAME = p_table1
   AND COLUMN_KEY = 'PRI';
end#
delimiter ;

drop procedure if exists sp_keypair;
delimiter #
create procedure sp_keypair(p_bd varchar(100), p_table1 varchar(100), p_table2 varchar(100))
begin
declare k1 varchar(100) default "";
declare k2 varchar(100) default "";
declare k3 varchar(100) default "";
        set k1 =(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=p_table1 and COLUMN_KEY='PRI');
            set k2 = (select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table2 and REFERENCED_TABLE_NAME = p_table1);
            if(k2 is null)then
            set k1 =(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=p_table2 and COLUMN_KEY='PRI');
            set k2 = (select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table1 and REFERENCED_TABLE_NAME = p_table2);
            end if;
            set k3 = concat(k1,'#',k2);
	select k3 as 'rsp';
end#
delimiter ;


drop procedure if exists sp_keytest;
delimiter #
create procedure sp_keytest(p_bd varchar(100), p_table1 varchar(100), p_table2 varchar(100))
begin
	declare pri varchar(100) default "";
    declare sec varchar(100) default "";
    declare priTable varchar(100) default "";
	declare rsp varchar(100) default "";
	set sec=(select COLUMN_NAME from information_schema.key_column_usage
            where table_schema = p_bd and TABLE_NAME=p_table1 and REFERENCED_TABLE_NAME = p_table2);
            if(sec is null)then
               set sec=(select COLUMN_NAME from information_schema.key_column_usage
					where table_schema = p_bd and TABLE_NAME=p_table2 and REFERENCED_TABLE_NAME = p_table1);
                    set priTable=p_table1;
				else
                set priTable=p_table2;
            end if;
            set pri=(select COLUMN_NAME from information_schema.COLUMNS
            where table_schema = p_bd and TABLE_NAME=priTable and COLUMN_KEY='PRI');
             set rsp = concat(pri,'#',sec);
             select rsp;
end#
delimiter ;
call sp_keytest('vqbuilder','venda','itemvenda');
call sp_keytest('vqbuilder','itemvenda','venda');
call sp_keytest('vqbuilder','funcionario','funcionario');

