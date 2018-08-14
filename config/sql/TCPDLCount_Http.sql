#1、TCP下行报文数
SELECT  sum(DlIpPacketNum)  from http where L4Protocol=0 