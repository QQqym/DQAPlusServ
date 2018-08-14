#2、TCP上行流量
SELECT   sum(UlDataLen)  from VOIP where L4Protocol=0
