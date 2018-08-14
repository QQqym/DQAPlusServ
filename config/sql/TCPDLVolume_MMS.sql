#2、TCP下行流量
SELECT   sum(DlDataLen)  from mms where L4Protocol=0
