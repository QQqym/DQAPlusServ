#2、TCP下行流量
SELECT   sum(DlDataLen)  from im where L4Protocol=0
