#2、TCP上行流量
SELECT   sum(UlDataLen)  from Email where L4Protocol=0
