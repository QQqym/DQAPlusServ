#2、TCP上行流量
SELECT   sum(UlDataLen)  from Http where L4Protocol=0
