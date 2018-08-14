#2、TCP下行流量
SELECT   sum(DlDataLen)  from http where L4Protocol=0 and IMSI=460078504254634
