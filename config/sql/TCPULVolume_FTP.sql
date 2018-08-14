#2、TCP上行流量
SELECT   sum(UlDataLen)  from FTP where L4Protocol=0
