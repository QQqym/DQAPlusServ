#2、TCP上行流量
SELECT   sum(UlDataLen)  from RTSP where L4Protocol=0
