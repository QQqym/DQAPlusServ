#2、TCP下行流量
SELECT   sum(DlDataLen)  from p2p where L4Protocol=0
