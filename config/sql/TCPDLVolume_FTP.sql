#2、TCP下行流量
SELECT   sum(DlDataLen)  from ftp where L4Protocol=0
