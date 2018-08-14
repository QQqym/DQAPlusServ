#2、TCP下行流量
SELECT   sum(DlDataLen)  from DNS where L4Protocol=0
