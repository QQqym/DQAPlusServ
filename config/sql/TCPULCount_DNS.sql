#2、TCP下行重传率
SELECT   sum(UlIpPacketNum)  from DNS where L4Protocol=0
