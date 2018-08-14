#2、TCP上行流量
SELECT   sum(UlDataLen)  from IM where L4Protocol=0
