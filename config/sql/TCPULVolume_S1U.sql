#2、TCP上行流量
SELECT   sum(UlDataLen)  from s1_u  where  L4Protocol=0
