#2、TCP下行流量
SELECT   sum(DlDataLen)  from rtsp where L4Protocol=0
