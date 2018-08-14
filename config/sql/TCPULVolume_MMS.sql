#2、TCP上行流量
SELECT   sum(UlDataLen)  from MMS where L4Protocol=0
