#2、TCP上行流量
SELECT   sum(UlDataLen)  from DNS where L4Protocol=0
