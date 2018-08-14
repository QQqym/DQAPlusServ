#2、TCP上行流量
SELECT   sum(UlDataLen)  from  p2p  where  L4Protocol=0
